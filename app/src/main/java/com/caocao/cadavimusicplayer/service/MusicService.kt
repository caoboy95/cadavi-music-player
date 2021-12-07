package com.caocao.cadavimusicplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.data.repository.PreferenceRepository
import org.koin.android.ext.android.inject


class MusicService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var musicPlayer: MusicPlayer
    private val preferenceRepository: PreferenceRepository by inject()
    private var isPlaybackDelayed = false
    private var isPlaybackNowAuthorized = false
    private var wasPlaying = false
    private var isPausing = false
    private var currentSongPosition: Int = 0
    private val listPlayingSongs = mutableListOf<Song>()
    private var listWillBePlayedSongs = listOf<Song>()
    //mode
    private val playingMode: Int
        get() = preferenceRepository.repeatMode
    private val isShuffleMode: Boolean
        get() = preferenceRepository.isShuffle

    var currentPosition : Int
        set(value) { musicPlayer.seekTo(value) }
        get() = musicPlayer.getCurrentPosition()

    //notification
    private lateinit var _mediaSession : MediaSessionCompat
    val mediaSession : MediaSessionCompat
        get() = _mediaSession
    private lateinit var _notifyManager: NotifyManager
    val notifyManager : NotifyManager
        get() = _notifyManager

    //focus audio
    private lateinit var audioManager: AudioManager
    private val focusLock = Any()
    private var focusRequest: AudioFocusRequest? = null
    private val focusHandler = Handler(Looper.getMainLooper())
    private var resumeOnFocusGain = false

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.d(TAG, "loss")
                synchronized(focusLock) {
                    resumeOnFocusGain = false
                    isPlaybackDelayed = false
                }
                pause(false)
                isPlaybackNowAuthorized = false
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d(TAG, "loss transient")
                synchronized(focusLock) {
                    resumeOnFocusGain = wasPlaying
                    isPlaybackDelayed = false
                }
                pause(false)
                isPlaybackNowAuthorized = false
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d(TAG, "gain")
                if (isPlaybackDelayed || resumeOnFocusGain) {
                    Log.d(TAG, "gain playbackDelayed:$isPlaybackDelayed resumeOnFocusGain:$resumeOnFocusGain")
                    mediaSession.isActive = true
                    synchronized(focusLock) {
                        isPlaybackDelayed = false
                        resumeOnFocusGain = false
                    }
                    play()
                }
                isPlaybackNowAuthorized = true
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        init()
        startMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand intent:${intent?.extras}")
        intent?.let {
            when (it.action) {
                Intent.ACTION_MEDIA_BUTTON -> {
                    androidx.media.session.MediaButtonReceiver.handleIntent(_mediaSession, it)
                }
                ACTION_STOP -> {
                    stop()
                }
                else -> {}
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return MusicBinder(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        musicPlayer.release()
        preferenceRepository.preferences.unregisterOnSharedPreferenceChangeListener(this)
        stopSelf()
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (listPlayingSongs.isNotEmpty()) checkAndShuffle(listPlayingSongs[currentSongPosition])
    }

    private fun init() {
        musicPlayer = MusicPlayer(this)
        _notifyManager = NotifyManager(this)
        preferenceRepository.preferences.registerOnSharedPreferenceChangeListener(this)
    }

    fun initSongs(songs: List<Song>, position: Int) {
        currentSongPosition = position
        listWillBePlayedSongs = songs
        checkAndShuffle(songs[position])
        initPlayerSong(songs[position])
    }

    private fun initPlayerSong(song: Song) {
        musicPlayer.initSong(song)
        isPausing = false
    }

    fun getCurrentSongOrNull() = listPlayingSongs.getOrNull(currentSongPosition)

    fun getSizeOfListSongs() = listPlayingSongs.size

    fun getCurrentPlayingSongPosition() = currentSongPosition

    fun setMetaDataMedia(metaData : MediaMetadataCompat) {
        _mediaSession.setMetadata(metaData)
    }

    private fun checkAndShuffle(song: Song) {
        when {
            isShuffleMode -> listPlayingSongs.run {
                val temp = mutableListOf<Song>().apply {
                    addAll(listWillBePlayedSongs)
                    remove(song)
                    shuffle()
                }
                clear()
                add(song)
                addAll(temp)
                currentSongPosition = 0
                return
            }
            else -> {
                listPlayingSongs.clear()
                listPlayingSongs.addAll(listWillBePlayedSongs)
                currentSongPosition = listPlayingSongs.indexOf(song)
            }
        }
    }

    fun shuffleAndPlay(songs: List<Song>) {
        currentSongPosition = 0
        listWillBePlayedSongs = songs
        listPlayingSongs.clear()
        listPlayingSongs.addAll(songs.shuffled())
        initPlayerSong(listPlayingSongs[0])
    }

    fun playPauseToggle() {
        if (isPlaying()) {
            pause(true)
        } else {
            play()
        }
    }

    //-----------------CONTROL MUSIC--------------------
    fun play() {
        when {
            isPausing -> {
                resume()
                return
            }
            isPlaybackNowAuthorized -> {
                wasPlaying = true
                musicPlayer.start()
            }
            else -> {
                gainAudioFocusAndPlay()
                return
            }
        }
        notifyManager.notifyChange(InternalIntents.METADATA_CHANGED, false)
        notifyManager.notifyChange(InternalIntents.PLAYBACK_STATE_CHANGED, true)
    }

    fun pause(canShowNotification: Boolean) {
        isPausing = true
        musicPlayer.pause()
        notifyManager.notifyChange(InternalIntents.PLAYBACK_STATE_CHANGED, canShowNotification)
    }

    private fun resume() {
        if (isPlaybackNowAuthorized) {
            musicPlayer.resume()
        } else {
            gainAudioFocusAndPlay()
            return
        }
        notifyManager.notifyChange(InternalIntents.PLAYBACK_STATE_CHANGED, true)
    }

    fun stop() {
        removeGainAudioFocus()
        notifyManager.notifyChange(InternalIntents.PLAYBACK_STATE_CHANGED, false)
        Log.e(TAG, "stop")
    }

    fun goToBack() {
        when {
            musicPlayer.isPreparing() -> return
            isEmptyPlayingSongs() -> return
            currentSongPosition == 0 -> currentSongPosition = listPlayingSongs.lastIndex
            else -> currentSongPosition--
        }
        Log.e(TAG, "Go to Back")
        initPlayerSong(listPlayingSongs[currentSongPosition])
    }

    fun goToNext() {
        when {
            musicPlayer.isPreparing() -> return
            isEmptyPlayingSongs() -> return
            currentSongPosition == listPlayingSongs.lastIndex -> currentSongPosition = 0
            else -> currentSongPosition++
        }
        Log.e(TAG, "Go to Next")
        initPlayerSong(listPlayingSongs[currentSongPosition])
    }

    fun nextSongWhenCompleted() {
        when(playingMode) {
            MusicPlayer.PlayingMode.NORMAL_MODE -> {
                if (currentSongPosition == listPlayingSongs.lastIndex) {
                    currentPosition = 0
                    stop()
                } else { goToNext() }
            }
            MusicPlayer.PlayingMode.REPEAT_MODE -> {
                goToNext()
            }
            MusicPlayer.PlayingMode.REPEAT_ONE_MODE -> {
                currentPosition = 0
                play()
            }
        }
    }

    private fun isEmptyPlayingSongs() = listPlayingSongs.isEmpty()

    fun isPlaying(): Boolean = musicPlayer.isPlaying()
    //-----------------CONTROL MUSIC--------------------

    @Suppress("DEPRECATION")
    private fun gainAudioFocusAndPlay() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            val mPlaybackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mPlaybackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener, focusHandler)
                .build()
            focusRequest?.let{
                val res = audioManager.requestAudioFocus (it)
                synchronized(focusLock) {
                    when (res) {
                        AudioManager.AUDIOFOCUS_REQUEST_FAILED -> isPlaybackNowAuthorized = false
                        AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                            isPlaybackNowAuthorized = true
                            mediaSession.isActive = true
                            play()
                        }
                        AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                            isPlaybackDelayed = true
                            isPlaybackNowAuthorized = false
                        }
                    }
                }
            }
        } else {
            val result: Int = audioManager.requestAudioFocus(
                audioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN
            )

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                isPlaybackNowAuthorized = true
                mediaSession.isActive = true
                wasPlaying = true
                musicPlayer.start()
            }
        }
    }

    private fun removeGainAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            return
        }
        @Suppress("DEPRECATION")
        audioManager.abandonAudioFocus(audioFocusChangeListener)
    }

    private fun startMediaSession() {
        _mediaSession = MediaSessionCompat(this, "CCMusicPlayer", null, null)
        _mediaSession.setCallback(object : MediaSessionCompat.Callback() {

            override fun onPause() {
                pause(true)
            }

            override fun onPlay() {
                play()

            }

            override fun onSeekTo(pos: Long) {
                pause(true)
                musicPlayer.setPauseTime(pos.toInt())
                play()
                Log.e(TAG, "onSeekTo")
            }

            override fun onSkipToNext() {
                goToNext()
            }

            override fun onSkipToPrevious() {
                goToBack()
            }

            override fun onStop() {
                stop()
            }
        })
//        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, androidx.media.session.MediaButtonReceiver::class.java)

        val mbrIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
        _mediaSession.setMediaButtonReceiver(mbrIntent)
    }

    fun setPlaybackStateMediaSession(playbackStateCompat: PlaybackStateCompat) {
        _mediaSession.setPlaybackState(playbackStateCompat)
    }

    object InternalIntents {
        const val PLAYBACK_STATE_CHANGED =
            "InternalIntents.PLAYBACK_STATE_CHANGED"
        const val METADATA_CHANGED = "InternalIntents.METADATA_CHANGED"
        const val SERVICE_CONNECTED =
            "InternalIntents.SERVICE_CONNECTED"
    }

    companion object {
        @JvmStatic
        val ACTION_STOP = "Intents.ACTION_STOP"
        private const val TAG = "Music Service"
    }
}
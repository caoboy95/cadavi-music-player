package com.caocao.cadavimusicplayer.service

import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.caocao.cadavimusicplayer.data.model.OnlineSong
import com.caocao.cadavimusicplayer.data.model.Song

class MusicPlayer(private val musicService: MusicService) : MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnSeekCompleteListener {
    private var mediaPlayer = MediaPlayer()
    private lateinit var song: Song
    private var isPrepared = false
    private var pauseTime : Int = 0
    private var isPreparing: Boolean = false

    init {
        mediaPlayer.setWakeMode(musicService, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer.setAudioAttributes(
             AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnSeekCompleteListener(this)
    }

    fun initSong(song: Song) {
        this.song = song
        pauseTime = 0
        isPrepared = false
        val trackUri =
            if (song !is OnlineSong)
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.id)
            else song.url.toUri()

        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(musicService, trackUri)
            mediaPlayer.prepareAsync()
            isPreparing = true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting data source", e)
            Toast.makeText(musicService, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun setPauseTime(position: Int) {
        pauseTime = position
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun isPreparing() = isPreparing

    fun getCurrentPosition() = mediaPlayer.currentPosition

    fun start() {
        try {
            Log.e(TAG, "Play music List songs size: ${musicService.getSizeOfListSongs()} Position: ${musicService.getCurrentPlayingSongPosition()}")
            mediaPlayer.setVolume(1.0f, 1.0f)
            mediaPlayer.start()
        } catch (e: RuntimeException) {
            Log.e(TAG, "Error pausing MultiPlayer: " + e.localizedMessage)
        }
    }

    fun pause() {
        Log.e(TAG, "Pause Music")
        pauseTime = mediaPlayer.currentPosition
        mediaPlayer.pause()
    }

    fun resume() {
        Log.e(TAG, "Resume Music")
        mediaPlayer.seekTo(pauseTime)
        try {
            mediaPlayer.start()
        } catch (e: RuntimeException) {
            Log.e(TAG, "Error pausing MultiPlayer: " + e.localizedMessage)
        }
    }

    fun stop() {
        Log.e(TAG, "Stop Music")
        pauseTime = 0
        isPrepared = false
        mediaPlayer.stop()
        musicService.notifyManager.notifyChange(MusicService.InternalIntents.PLAYBACK_STATE_CHANGED, false)
    }

    fun release() {
        mediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        Log.d(TAG, "isPlaying : ${mediaPlayer.isPlaying}")
        return try {
            mediaPlayer.isPlaying
        } catch (ignored: IllegalStateException) {
            false
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e(TAG, "onError")
        isPrepared = false
        return false
    }


    override fun onPrepared(mp: MediaPlayer?) {
        Log.d(TAG, "onPrepared")
        isPrepared = true
        isPreparing = false
        musicService.play()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion")
        musicService.nextSongWhenCompleted()
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        Log.d(TAG, "onSeekComplete")
    }

    object PlayingMode {
        const val REPEAT_MODE = 3
        const val REPEAT_ONE_MODE = 2
        const val NORMAL_MODE = 1
    }

    companion object {
        private const val TAG = "MusicPlayer"
    }
}
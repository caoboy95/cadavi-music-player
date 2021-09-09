package com.caocao.cadavimusicplayer.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.ui.HomeActivity
import com.caocao.cadavimusicplayer.util.ArtURI
import com.caocao.cadavimusicplayer.util.MediaStyleHelper
import com.caocao.cadavimusicplayer.util.createActivityPendingIntent
import com.caocao.cadavimusicplayer.util.createServicePendingIntent

class NotifyManager(private val service: MusicService) {

    private var mContext = service.applicationContext
    private var channel: NotificationChannel? = null
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val playbackBuilder = PlaybackStateCompat.Builder()
//    private var isServiceStarted = false

    init {
        channel = createNotificationChannel()
    }

    fun notifyChange(whatMediaChange: String, canCreateNotification: Boolean) {
        Log.d(TAG, "notifyChange whatMediaChange: $whatMediaChange, createNotification: $canCreateNotification")
        when (whatMediaChange) {
            MusicService.InternalIntents.METADATA_CHANGED -> {
                updateMetaData()
            }
            MusicService.InternalIntents.PLAYBACK_STATE_CHANGED -> {
                updatePlaybackState()
                if (canCreateNotification) {
                    showNotification()
                } else {
                    removeNotification()
                }
            }
        }
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(mContext).sendBroadcast(Intent().setAction(whatMediaChange))
    }

    private fun updateMetaData() {
        Log.d(TAG, "Update MetaData")
        service.getCurrentSongOrNull()?.let { currentSong ->
            val uri = ContentUris.withAppendedId(ArtURI, currentSong.albumId)
            metadataBuilder.apply {
                putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.artistName)
                putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.title)
                putString(MediaMetadataCompat.METADATA_KEY_ART_URI, uri.toString())
                putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, uri.toString())
            }
            service.setMetaDataMedia(metadataBuilder.build())
        }
    }

    private fun updatePlaybackState() {
        Log.d(TAG, "Update PlaybackState")
        setPlaybackBuilder()
        service.setPlaybackStateMediaSession(playbackBuilder.build())
    }

    private fun setPlaybackBuilder() {
        playbackBuilder.apply {
            if (service.isPlaying()) {
                setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_STOP)
                setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                return
            }
            setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_STOP)
            setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
        }
    }

    private fun showNotification() {
        try {
            createNotification(service.isPlaying())
        } catch (e: Exception) {
            Log.e(TAG, ": + $e")
        }
    }

    private fun createNotification(isPlay: Boolean) {
        Log.d(TAG, "createNotification isPlay:$isPlay")

        val builder = MediaStyleHelper.from(service, service.mediaSession).apply {
            val stopIntent = Intent(service, MusicService::class.java).apply { action = MusicService.ACTION_STOP }
            val notIntent = Intent(service, HomeActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
            val launchNowPlayingPendingIntent = createActivityPendingIntent(notIntent, service, PendingIntent.FLAG_UPDATE_CURRENT)
            val cancelIntent = createServicePendingIntent(stopIntent, service)

            setSmallIcon(R.drawable.ic_notification)
            color = ContextCompat.getColor(service, R.color.colorPrimaryDark)
            setColorized(true)
            setShowWhen(false)
            setContentIntent(launchNowPlayingPendingIntent)
            setDeleteIntent(launchNowPlayingPendingIntent)
            setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0,1,2)
                .setMediaSession(service.mediaSession.sessionToken)
                .setCancelButtonIntent(cancelIntent)
                .setShowCancelButton(true))

            channel?.let { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setChannelId(it.id)
            }}

            addAction(NotificationCompat.Action(R.drawable.ic_icon_pre,
                "Back",
                androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(service, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
            // Actions
            if (!isPlay){
                addAction(NotificationCompat.Action(R.drawable.ic_play_notifi,
                    "Play",
                    androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(service, PlaybackStateCompat.ACTION_PLAY)))
            } else{
                addAction(NotificationCompat.Action(R.drawable.ic_pause_notifi,
                    "Pause",
                    androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(service, PlaybackStateCompat.ACTION_PAUSE)))
            }
            addAction(NotificationCompat.Action(R.drawable.ic_icon_next,
                "Next",
                androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(service, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
        }

        //Build the notification object.
        val notification = builder.build()
        NotificationManagerCompat.from(service).notify(NOTIFY_ID, notification)
        if (isPlay) {
            service.startForeground(NOTIFY_ID, notification)
//            isServiceStarted = true
            return
        }
//        if (isServiceStarted) {
            service.stopForeground(false)
//            isServiceStarted = false
//        }
    }

    private fun createNotificationChannel(): NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val id = "cadavi_music_player_channel"
            val name = "Cadavi Music Player"
            val description = "Cadavi music"
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW).apply {
                this.description = description
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            return channel
        }
        return null
    }

    private fun removeNotification(){
        NotificationManagerCompat.from(mContext).cancel(NOTIFY_ID)
        service.stopForeground(true)
//        isServiceStarted = false
        Log.e(TAG, "Stop Foreground")
    }

    companion object {
        const val NOTIFY_ID = 1998
        private const val TAG = "NotifyManager"
    }

}
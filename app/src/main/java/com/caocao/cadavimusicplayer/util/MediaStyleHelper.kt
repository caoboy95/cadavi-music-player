package com.caocao.cadavimusicplayer.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.service.MusicService

object MediaStyleHelper {
    fun from(
        context: Context,
        mediaSession: MediaSessionCompat,
        channelId: String? = null
    ) : NotificationCompat.Builder {
        val description = mediaSession.controller.metadata.description

        var bitmap = description.iconBitmap
        bitmap?: let { bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_default_not_radius) }

        val stopIntent = Intent(context, MusicService::class.java)
        stopIntent.action = MusicService.ACTION_STOP
        val stopPendingIntent = createServicePendingIntent(stopIntent, context)

        val builder = if (channelId == null) {
            @Suppress("DEPRECATION") NotificationCompat.Builder(context)
        } else {
            NotificationCompat.Builder(context, channelId)
        }
        builder.setContentTitle(description.title)
            .setContentText(description.subtitle)
            .setSubText(description.description)
            .setLargeIcon(bitmap)
            .setDeleteIntent(stopPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        return builder

    }
}

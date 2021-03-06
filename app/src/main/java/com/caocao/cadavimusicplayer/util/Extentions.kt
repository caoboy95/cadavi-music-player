package com.caocao.cadavimusicplayer.util

import android.Manifest
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.transition.Fade
import android.util.Log
import android.util.LongSparseArray
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import androidx.fragment.app.Fragment
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.data.model.Album
import com.caocao.cadavimusicplayer.data.model.OnlineSong
import com.caocao.cadavimusicplayer.data.model.Song
import com.squareup.picasso.Picasso
import com.squareup.sqlbrite2.BriteContentResolver
import com.squareup.sqlbrite2.SqlBrite
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayInputStream


fun getService() = ServiceConnectionUtil.musicBinder?.service?.get()

fun Int.getColorWithAlpha(alpha: Int): Int =
    Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))

fun dispose(disposable: Disposable?) {
    if (disposable?.isDisposed == false) {
        disposable.dispose()
    }
}

fun AppCompatActivity.replaceFragmentToActivity(fragment: Fragment, frameId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        fragment.enterTransition = Fade()
        fragment.exitTransition = Fade()
    }
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(frameId, fragment)
    transaction.commit()
}

fun ContentResolver.toBrite(): BriteContentResolver {
    val sqlBrite = SqlBrite.Builder().build()
    return sqlBrite.wrapContentProvider(this, Schedulers.io())
}

fun <T : Fragment> T.isPermissionGranted(): Boolean {
    context?.let {
        return ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
    return false
}

fun createActivityPendingIntent(intent: Intent, context: Context ,flag: Int? = null): PendingIntent {
    flag?.let {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or it)
        } else {
            PendingIntent.getActivity(context, 0, intent, it)
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    } else {
        PendingIntent.getActivity(context, 0, intent, 0)
    }
}

fun createServicePendingIntent(intent: Intent, context: Context ,flag: Int? = null): PendingIntent {
    flag?.let {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or it)
        } else {
            PendingIntent.getService(context, 0, intent, it)
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    } else {
        PendingIntent.getService(context, 0, intent, 0)
    }
}

fun playSong(allSongs: List<Song>, position: Int) {
    getService()?.run {
        initSongs(allSongs, position)
    }
}

fun shuffleAndPlay(allSongs: List<Song>) {
    getService()?.run {
        shuffleAndPlay(allSongs)
    }
}

fun exchangeDurationToText(duration: Int): String {
    val sec = (duration%60000)/1000
    val minute = duration/60000
    return "${if (minute < 10) "0${minute}" else minute}:${if (sec < 10) "0${sec}" else sec}"
}

fun ImageView.loadArtSong(song: Song?) {
    try {
        song?.let {
            when {
                song is OnlineSong && song.art != null -> {
                    setImageBitmap(BitmapFactory.decodeStream(ByteArrayInputStream(song.art)))
                    return
                }
                song !is OnlineSong -> {
                    loadArtSong(song.albumId)
                    return
                }
                else -> {}
            }
        }
        setImageResource(R.drawable.ic_default_not_radius)
    } catch (e : Exception) { Log.e("Load Art Song", e.toString()) }
}

fun ImageView.loadArtSong(id: Long) {
    val uri = ContentUris.withAppendedId(ArtURI, id)

    Picasso.get()
        .load(uri)
        .placeholder(R.drawable.ic_default_not_radius)
        .error(R.drawable.ic_default_not_radius)
        .fit()
        .into(this@loadArtSong)
}

fun <T> LongSparseArray<T>.toList(): ArrayList<T> {
    val list = ArrayList<T>()
    this.forEach { _, value ->
        list.add(value)
    }
    return list
}

fun songsToAlbums(songs: List<Song>): List<Album> {
    val albumMap = LongSparseArray<Album>()
    songs.forEach { song ->
        val album = Album(song.albumId, song.albumName, song.artistName)
        val oldAlbum = albumMap[album.id]
        oldAlbum?.let {
            oldAlbum.songCount++
        } ?: run {
            album.songCount++
            albumMap.put(album.id, album)
        }
    }
    return albumMap.toList()
}
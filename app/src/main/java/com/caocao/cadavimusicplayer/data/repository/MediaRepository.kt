package com.caocao.cadavimusicplayer.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.util.Query
import com.caocao.cadavimusicplayer.util.toBrite
import com.example.testapp.data.db.AppDatabase
import io.reactivex.Observable


class MediaRepository constructor(
    context: Context,
    private val localDataSource: AppDatabase
) {
    private val contentResolver = context.contentResolver
    private val briteContentResolver = contentResolver.toBrite()

    // Projections
    private val songProjection = arrayOf(MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION)

    private val genreProjection = arrayOf(MediaStore.Audio.Genres._ID,
        MediaStore.Audio.Genres.NAME)


    private val playlistProjection = arrayOf(MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME)

    // Queries
    private val songQuery = Query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        songProjection,
        "(" + MediaStore.Audio.Media.IS_MUSIC + "=1 OR " + MediaStore.Audio.Media.IS_PODCAST + "=1 )",
        null,
        MediaStore.Audio.Media.TITLE
    )

    private val genreQuery = Query(
        MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
        genreProjection,
        null,
        null,
        MediaStore.Audio.Genres.NAME
    )

    fun getSong(uri: Uri): Observable<List<Song>> {
        val query = songQuery.copy(uri = uri)
        return briteContentResolver
            .createQuery(query.uri, query.projection, query.selection, query.args, query.sort, false)
            .mapToList { Song(it) }
            .take(1)
    }

    fun getSongs(): Observable<List<Song>> {
        return briteContentResolver.createQuery(songQuery.uri, songQuery.projection, songQuery.selection,
            songQuery.args, songQuery.sort, false)
            .mapToList { Song(it) }
    }
}
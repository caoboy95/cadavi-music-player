package com.caocao.cadavimusicplayer.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import com.caocao.cadavimusicplayer.data.model.Album
import com.caocao.cadavimusicplayer.data.model.Favorite
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.util.Query
import com.caocao.cadavimusicplayer.util.songsToAlbums
import com.caocao.cadavimusicplayer.util.toBrite
import io.reactivex.Observable

class MediaRepository (context: Context) {
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

    fun getAlbums(): Observable<List<Album>> = getSongs().flatMap { Observable.just(songsToAlbums(it)) }

    fun getAlbumSongs(albumId: Long): Observable<List<Song>> {
        return getSongs().map { songs -> songs.filter { it.albumId == albumId } }
    }

    fun getFavorites(favorites: Observable<List<Favorite>>): Observable<List<Song>> {
        return favorites.flatMap { it ->
                val songsQuery = songQuery.copy(
                    selection = songQuery.selection + " AND " + MediaStore.Audio.Media._ID + " IN (" +
                            Stream.of(it)
                                .map { it.id.toString() }
                                .collect(Collectors.joining(",")) +
                            ")"
                )
                return@flatMap contentResolver.toBrite()
                    .createQuery(songsQuery.uri, songsQuery.projection, songsQuery.selection, songsQuery.args,
                        songsQuery.sort, false)
                    .mapToList { Song(it) }
            }
    }
}
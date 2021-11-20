package com.caocao.cadavimusicplayer.data.db

import androidx.room.*
import com.caocao.cadavimusicplayer.data.model.Favorite
import io.reactivex.Observable

@Dao
interface DataDao {

    // Favorites
    @Query("SELECT * FROM favorites WHERE fav = :songId")
    fun getFavorite(songId: Long): Favorite?

    @Query("SELECT * FROM favorites")
    fun getFavorites(): Observable<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavorite(favorite: Favorite)

    @Delete
    fun deleteFavorite(favorite: Favorite)

//    @Query("SELECT * FROM playlist WHERE idPlaylist = :idPlaylist")
//    fun getPlaylist(idPlaylist: Int): Playlist
//
//    @Query("SELECT * FROM playlist ORDER BY playlist.namePlaylist")
//    fun getPlaylists():  List<Playlist>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun addPlaylist(playlist: Playlist)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun addSongToPlaylist(playlistSong: Playlist_Song)
//
//    @Query("DELETE FROM Playlist_Song WHERE idPlaylist = :idPlaylist")
//    fun deletePlaylistSong(idPlaylist: Int)
//
//    @Query("SELECT * FROM songs JOIN playlist_song on playlist_song.media_store_id = songs.media_store_id  WHERE idPlaylist = :idPlaylist  ORDER BY songs.title COLLATE UNICODE")
//    fun getSongFromPlaylists(idPlaylist: Int):  List<Song>
//
//    @Query("SELECT * FROM songs JOIN playlist_song on playlist_song.media_store_id = songs.media_store_id  ORDER BY songs.title COLLATE UNICODE")
//    fun getAllPlaylistSongs(): LiveData<List<Song>>
//
//    @Query("SELECT * FROM songs JOIN playlist_song on playlist_song.media_store_id = songs.media_store_id  WHERE idPlaylist = :idPlaylist  ORDER BY songs.title COLLATE UNICODE")
//    fun getLiveDataPlaylistSongs(idPlaylist: Int): LiveData<List<Song>>
//
//    @Query("SELECT * FROM songs ORDER BY songs.title COLLATE UNICODE")
//    fun getListSongDB(): List<Song>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun addSongToDB(listSong: List<Song>)
//
//    @Query("DELETE FROM songs")
//    fun deleteAllSongsFromDB()
//
//    @Query("UPDATE playlist SET namePlaylist = :name WHERE idPlaylist = :idPlaylist")
//    fun updatePlaylist(name: String, idPlaylist: Int)
//
//    @Query("DELETE FROM playlist WHERE idPlaylist = :id")
//    fun deletePlaylist(id: Int)
//
//    @Query("DELETE FROM playlist_song WHERE idPlaylist = :id")
//    fun deletePlaylist_Song(id: Int)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun addSongHaveGenre(listSong: List<SongHaveGenre>)
//
//    @Query("SELECT * FROM songhavegenre ORDER BY songhavegenre.title")
//    fun getSongHaveGenre():  List<SongHaveGenre>
}
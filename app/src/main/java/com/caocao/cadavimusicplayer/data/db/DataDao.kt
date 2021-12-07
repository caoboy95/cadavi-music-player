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
}
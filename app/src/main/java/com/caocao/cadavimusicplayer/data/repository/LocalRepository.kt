package com.caocao.cadavimusicplayer.data.repository

import com.caocao.cadavimusicplayer.data.db.DataDao
import com.caocao.cadavimusicplayer.data.model.Favorite

class LocalRepository(private val dataDao: DataDao) {

    fun getFavorites() = dataDao.getFavorites()

    fun addFavorite(favorite: Favorite) {
        dataDao.addFavorite(favorite)
    }

    fun getFavorite(songId: Long) = dataDao.getFavorite(songId)

    fun deleteFavorite(favorite: Favorite) {
        dataDao.deleteFavorite(favorite)
    }
}
package com.caocao.cadavimusicplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.caocao.cadavimusicplayer.base.BaseViewModel
import com.caocao.cadavimusicplayer.data.model.Album

class AlbumViewModel(application: Application): BaseViewModel(application) {
    private val album = MutableLiveData<Album>()

    override fun fetchSongs() {

    }

    override fun disposeSongs() {

    }

    companion object {
        const val TAG = "AlbumViewModel"
    }
}
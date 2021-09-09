package com.caocao.cadavimusicplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.caocao.cadavimusicplayer.base.BaseViewModel
import com.caocao.cadavimusicplayer.data.model.Album
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class AlbumViewModel(application: Application,
    private val mediaRepository: MediaRepository
    ): BaseViewModel(application) {
    private val _album = MutableLiveData<List<Album>>()
    val album: LiveData<List<Album>>
        get() = _album

    override fun fetchSongs() {
        disposable = mediaRepository.getAlbums()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .take(1)
            .map { albums ->
                Collections.sort(albums) { a, b -> a.compareTo(b) }
                return@map albums
            }
            .subscribe {
                it?.let {
                    _album.value = it
                }
            }
    }

    override fun disposeSongs() {

    }

    companion object {
        const val TAG = "AlbumViewModel"
    }
}
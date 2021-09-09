package com.caocao.cadavimusicplayer.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.util.dispose
import com.caocao.cadavimusicplayer.util.playSong
import io.reactivex.disposables.Disposable

abstract class BaseViewModel(
    application: Application
) : AndroidViewModel(application) {

    protected var broadcastDisposable: Disposable? = null
    val currentSongId = MutableLiveData<Long>()

    private val songs = MutableLiveData<List<Song>>()
    val liveSongs : LiveData<List<Song>>
        get() = songs

    // Events
    var isPlaying = MutableLiveData<Boolean>()

    open fun start() {
        fetchSongs()
    }

    open fun stop() {
        disposeSongs()
        dispose(broadcastDisposable)
    }

    abstract fun fetchSongs()

    abstract fun disposeSongs()

    fun songItemClicked(position: Int) {
        songs.value?.let { playSong(it, position) }
    }

    fun setSongs(songs: List<Song>) {
        this.songs.value = songs
    }

    fun songItemClickedSearch(song : Song) {
        var listSearch: ArrayList<Song> = ArrayList()
        listSearch.add(song)
//        playSong(0, listSearch, true)
    }
}
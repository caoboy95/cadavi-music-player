package com.caocao.cadavimusicplayer.ui.viewmodel

import android.app.Application
import android.content.IntentFilter
import android.util.Log
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.base.BaseViewModel
import com.caocao.cadavimusicplayer.data.repository.PreferenceRepository
import com.caocao.cadavimusicplayer.service.MusicService
import com.caocao.cadavimusicplayer.util.dispose
import com.caocao.cadavimusicplayer.util.getService
import com.caocao.cadavimusicplayer.util.playSong
import com.caocao.cadavimusicplayer.util.shuffleAndPlay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.Collections.sort
import kotlin.random.Random


class AllSongViewModel(
    application: Application,
    private val mediaRepository: MediaRepository,
    private val preferenceRepository: PreferenceRepository
): BaseViewModel(application) {
    private var disposable: Disposable? = null
    private var playingDisposable: Disposable? = null

    override fun start() {
        super.start()
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.InternalIntents.PLAYBACK_STATE_CHANGED)
        }
        playingDisposable = RxBroadcast.fromLocalBroadcast(getApplication(), intentFilter)
            .subscribe {
                if (it?.action == MusicService.InternalIntents.PLAYBACK_STATE_CHANGED) {
                    isPlaying.value = getService()?.isPlaying()
                }
            }
    }

    override fun fetchSongs() {
        Log.e("AllSongViewModel","fetch")
        disposeSongs()
        disposable = mediaRepository.getSongs()
            .observeOn(AndroidSchedulers.mainThread())
            .map { songs -> sort(songs) { a, b -> a.compareTo(b)}
                return@map songs
            }
            .subscribe( { setSongs(it) }, { Log.ERROR } )
        Log.e("AllSongViewModel","fetched")
    }

    override fun disposeSongs() {
        dispose(disposable)
        dispose(playingDisposable)
    }

    fun shuffleAll() {
        liveSongs.value?.let {
            preferenceRepository.isShuffle = true
            shuffleAndPlay(it)
        }
    }

    companion object {
        const val TAG = "AllSongViewModel"
    }
}
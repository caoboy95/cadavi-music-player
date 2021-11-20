package com.caocao.cadavimusicplayer.ui.viewmodel

import android.app.Application
import android.content.IntentFilter
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.caocao.cadavimusicplayer.base.BaseViewModel
import com.caocao.cadavimusicplayer.data.repository.LocalRepository
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.service.MusicService
import com.caocao.cadavimusicplayer.util.dispose
import com.caocao.cadavimusicplayer.util.getService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class FavoriteViewModel(application: Application,
    private val mediaRepository: MediaRepository,
    private val localRepository: LocalRepository
): BaseViewModel(application) {

    override fun start() {
        super.start()
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.InternalIntents.PLAYBACK_STATE_CHANGED)
        }
        dispose(broadcastDisposable)
        broadcastDisposable = RxBroadcast.fromLocalBroadcast(getApplication(), intentFilter)
            .subscribe {
                if (it?.action == MusicService.InternalIntents.PLAYBACK_STATE_CHANGED) {
                    isPlaying.value = getService()?.isPlaying()
                }
            }
    }

    override fun fetchSongs() {
        dispose(disposable)
        disposable = mediaRepository.getFavorites(localRepository.getFavorites())
            .map { songs -> Collections.sort(songs) { a, b -> a.compareTo(b) }
                return@map songs
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it?.let { setSongs(it) }
            }
    }

    override fun disposeSongs() {

    }
}
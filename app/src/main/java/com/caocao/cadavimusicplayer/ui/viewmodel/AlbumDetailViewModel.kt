package com.caocao.cadavimusicplayer.ui.viewmodel

import android.app.Application
import android.content.IntentFilter
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.caocao.cadavimusicplayer.base.BaseViewModel
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.data.repository.PreferenceRepository
import com.caocao.cadavimusicplayer.service.MusicService
import com.caocao.cadavimusicplayer.util.getService
import com.caocao.cadavimusicplayer.util.shuffleAndPlay
import io.reactivex.android.schedulers.AndroidSchedulers

class AlbumDetailViewModel(
    application: Application,
    private val mediaRepository: MediaRepository,
    private val preferenceRepository: PreferenceRepository
): BaseViewModel(application) {

    override fun start() {
        super.start()
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.InternalIntents.PLAYBACK_STATE_CHANGED)
        }
        broadcastDisposable = RxBroadcast.fromLocalBroadcast(getApplication(), intentFilter)
            .subscribe {
                if (it?.action == MusicService.InternalIntents.PLAYBACK_STATE_CHANGED) {
                    isPlaying.value = getService()?.isPlaying()
                }
            }
    }

    override fun fetchSongs() {

    }

    override fun disposeSongs() {

    }

    fun getSongs(albumId: Long) {
        disposable = mediaRepository.getAlbumSongs(albumId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it?.let {
                    setSongs(it)
                }
            }
    }

    fun shuffleAll() {
        liveSongs.value?.let {
            preferenceRepository.isShuffle = true
            shuffleAndPlay(it)
        }
    }
}
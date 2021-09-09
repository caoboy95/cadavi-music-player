package com.caocao.cadavimusicplayer.base

import android.app.Application
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.service.MusicService
import com.caocao.cadavimusicplayer.util.dispose
import com.caocao.cadavimusicplayer.util.getService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

open class BasePlayerViewModel(application: Application): AndroidViewModel(application) {
    val currentSong = MutableLiveData<Song>()
    val process = MutableLiveData<Int>()
    val isPlaying = MutableLiveData<Boolean>()

    private var broadcastDisposable: Disposable? = null
    private var isLoaded: Boolean = false
    private var progressDisposable: Disposable? = null

    open fun start() {
        try {
            val filter = IntentFilter().apply {
                addAction(MusicService.InternalIntents.METADATA_CHANGED)
                addAction(MusicService.InternalIntents.PLAYBACK_STATE_CHANGED)
                addAction(MusicService.InternalIntents.SERVICE_CONNECTED)
            }

            broadcastDisposable = RxBroadcast.fromLocalBroadcast(getApplication(), filter)
                    .subscribe {
                        when (it.action) {
                            MusicService.InternalIntents.METADATA_CHANGED -> {
                                onMetadataChange()
                            }
                            MusicService.InternalIntents.PLAYBACK_STATE_CHANGED -> {
                                onPlaybackChange()
                            }
                            MusicService.InternalIntents.SERVICE_CONNECTED -> {
                                if (!isLoaded) {
                                    onInitMetadata()
                                }
                            }
                        }
                    }

            if (!isLoaded) {
                onInitMetadata()
            }
        }catch (e : Exception){
            Log.d("BasePlayerModel", "broadcastDisposable : $e")
        }
    }

    open fun stop() {
        dispose(broadcastDisposable)
        dispose(progressDisposable)
    }

    open fun onInitMetadata() {
        getService()?.getCurrentSongOrNull()?.let {
            currentSong.value = it
            isLoaded = true
            return
        }
        isLoaded = false
    }

    open fun onPlaybackChange() {
        getService()?.run {
            getCurrentSongOrNull()?.let {
                currentSong.value = it
            }
            isPlaying.postValue(isPlaying())
            if (isPlaying()) {
                setProgress(this)
            } else {
                disposeProgress()
            }
        }
    }

    open fun onMetadataChange() {
        getService()?.run {
            getCurrentSongOrNull()?.let {
                currentSong.value = it
            }
            if (isPlaying()) {
                setProgress(this)
            } else {
                disposeProgress()
            }
        }
    }

    private fun setProgress(service: MusicService) {
        disposeProgress()
        progressDisposable = Observable.interval(100, TimeUnit.MILLISECONDS)
                .map { service.currentPosition }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {  }
                .subscribe {
                    it?.let { process.postValue(it) }
                }
    }

    private fun disposeProgress() {
        dispose(progressDisposable)
    }
}
package com.caocao.cadavimusicplayer.ui.viewmodel

import android.app.Application
import android.content.SharedPreferences
import com.caocao.cadavimusicplayer.base.BasePlayerViewModel
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.data.repository.PreferenceRepository
import com.caocao.cadavimusicplayer.service.MusicPlayer

class MusicPlayerViewModel(
    application: Application,
    private val preferenceRepository: PreferenceRepository,
    private val mediaRepository: MediaRepository
): BasePlayerViewModel(application) {

    var playingMode: Int
        set(repeatMode) {
            preferenceRepository.repeatMode = repeatMode
        }
        get() = preferenceRepository.repeatMode

    var isShuffle: Boolean
        set(isShuffle) {
            preferenceRepository.isShuffle = isShuffle
        }
        get() = preferenceRepository.isShuffle

    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun start() {
        super.start()
        preferenceRepository.preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun stop() {
        super.stop()
        listener?.let { preferenceRepository.preferences.unregisterOnSharedPreferenceChangeListener(it) }
    }

    fun setOnShuffleChangeListener(callback: () -> Unit) {
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _,key ->
            if (key == PreferenceRepository.SHUFFLE) callback()
        }
    }

    fun changePlayingMode() {
        when (playingMode) {
            MusicPlayer.PlayingMode.NORMAL_MODE -> {
                playingMode = MusicPlayer.PlayingMode.REPEAT_MODE
            }
            MusicPlayer.PlayingMode.REPEAT_ONE_MODE -> {
                playingMode = MusicPlayer.PlayingMode.NORMAL_MODE
            }
            MusicPlayer.PlayingMode.REPEAT_MODE -> {
                playingMode = MusicPlayer.PlayingMode.REPEAT_ONE_MODE
            }
        }
    }
}
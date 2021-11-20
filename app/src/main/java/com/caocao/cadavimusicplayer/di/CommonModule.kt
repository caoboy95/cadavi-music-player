package com.caocao.cadavimusicplayer.di

import androidx.preference.PreferenceManager
import com.caocao.cadavimusicplayer.ui.view.MiniPlayerFragment
import com.caocao.cadavimusicplayer.ui.view.MusicPlayerFragment
import org.koin.dsl.module

val commonModule = module {
    factory { MiniPlayerFragment() }
    factory { MusicPlayerFragment() }
    single { PreferenceManager.getDefaultSharedPreferences(get()) }
}
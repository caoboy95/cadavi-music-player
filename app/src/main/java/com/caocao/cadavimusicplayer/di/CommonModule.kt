package com.caocao.cadavimusicplayer.di

import androidx.preference.PreferenceManager
import com.caocao.cadavimusicplayer.data.db.AppDatabase
import com.caocao.cadavimusicplayer.ui.view.MiniPlayerFragment
import com.caocao.cadavimusicplayer.ui.view.MusicPlayerFragment
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.module

val commonModule = module {
    factory { MiniPlayerFragment() }
    factory { MusicPlayerFragment() }
    single { PreferenceManager.getDefaultSharedPreferences(get()) }
    single { AppDatabase.buildDatabase(get()).getDao() }
    single { FirebaseStorage.getInstance().reference }
}
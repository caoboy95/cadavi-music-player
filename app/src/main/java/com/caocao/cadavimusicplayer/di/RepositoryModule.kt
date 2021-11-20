package com.caocao.cadavimusicplayer.di

import com.caocao.cadavimusicplayer.data.db.AppDatabase
import com.caocao.cadavimusicplayer.data.repository.LocalRepository
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.data.repository.PreferenceRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MediaRepository(get()) }
    single { PreferenceRepository(get()) }
    single { LocalRepository(get()) }
    factory { AppDatabase.invoke(get()).getDao() }
}
package com.caocao.cadavimusicplayer.di

import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.data.repository.PreferenceRepository
import com.example.testapp.data.db.AppDatabase
import org.koin.dsl.module

val repositoryModule = module {
    single { MediaRepository(get(), get()) }
    single { PreferenceRepository(get()) }
    factory { AppDatabase.invoke(get()) }
}
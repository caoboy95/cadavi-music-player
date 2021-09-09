package com.caocao.cadavimusicplayer.di

import com.caocao.cadavimusicplayer.ui.viewmodel.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AllSongViewModel(get(), get(), get()) }
    viewModel { MiniPlayerViewModel(get()) }
    viewModel { MusicPlayerViewModel(get(), get(), get()) }
    viewModel { AlbumViewModel(get(), get()) }
    viewModel { AlbumDetailViewModel(get(), get(), get()) }
}
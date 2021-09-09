package com.caocao.cadavimusicplayer.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.data.repository.*

class ViewModelFactory(
    private val repository: BaseRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
//            modelClass.isAssignableFrom(ProductViewModel::class.java) -> ProductViewModel(repository as ProductRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}
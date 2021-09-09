package com.caocao.cadavimusicplayer.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.ui.view.AllSongsFragment
import com.caocao.cadavimusicplayer.util.isPermissionGranted
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.RemoteDataSource
import com.example.testapp.data.repository.BaseRepository
import net.simplifiedcoding.mvvmsampleapp.data.preferences.PreferenceProvider
import org.koin.android.ext.android.inject

//import org.kodein.di.KodeinAware
//import org.kodein.di.android.x.kodein
//import org.kodein.di.generic.instance

abstract class BaseFragment<VM: BaseViewModel, B: ViewBinding> : Fragment() {
    //instance of viewmodel
    abstract val viewModel: VM
    protected lateinit var binding : B
    protected val mediaRepository : MediaRepository by inject()
//    protected val prefs: PreferenceProvider by inject()
    protected val database: AppDatabase by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (isPermissionGranted()) {
            getDataSongs()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stop()
    }

    fun getDataSongs() {
        viewModel.start()
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
}
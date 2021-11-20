package com.caocao.cadavimusicplayer.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.caocao.cadavimusicplayer.data.db.AppDatabase
import com.caocao.cadavimusicplayer.data.repository.MediaRepository
import com.caocao.cadavimusicplayer.util.isPermissionGranted
import org.koin.android.ext.android.inject

abstract class BaseFragment<VM: BaseViewModel, B: ViewBinding> : Fragment() {

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
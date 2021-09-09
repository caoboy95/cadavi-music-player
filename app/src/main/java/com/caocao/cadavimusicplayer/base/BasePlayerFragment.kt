package com.caocao.cadavimusicplayer.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.caocao.cadavimusicplayer.data.model.Song

abstract class BasePlayerFragment<VM: BasePlayerViewModel, B: ViewBinding>: Fragment() {
    protected abstract val viewModel: VM
    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.run {
            process.observe(viewLifecycleOwner, Observer {
                it?.let {
                    setProgressAction(it)
                }
            })
            currentSong.observe(viewLifecycleOwner, Observer {
                it?.let {
                    setCurrentSongAction(it)
                }
            })
            isPlaying.observe(viewLifecycleOwner, Observer {
                it?.let {
                    setPlayPauseAction(it)
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stop()
    }

    abstract fun setProgressAction(process: Int)

    abstract fun setCurrentSongAction(song: Song)

    abstract fun setPlayPauseAction(isPlaying: Boolean)

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
}
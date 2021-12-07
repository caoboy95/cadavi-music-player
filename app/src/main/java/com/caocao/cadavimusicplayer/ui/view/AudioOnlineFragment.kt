package com.caocao.cadavimusicplayer.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caocao.cadavimusicplayer.base.BaseFragment
import com.caocao.cadavimusicplayer.databinding.LayoutListSongBinding
import com.caocao.cadavimusicplayer.ui.HomeActivity
import com.caocao.cadavimusicplayer.ui.adapter.SongAdapter
import com.caocao.cadavimusicplayer.ui.viewmodel.AudioOnlineViewModel
import com.caocao.cadavimusicplayer.util.NetworkLiveData
import com.caocao.cadavimusicplayer.util.getService
import org.koin.android.ext.android.inject

class AudioOnlineFragment : BaseFragment<AudioOnlineViewModel, LayoutListSongBinding>() {
    override val viewModel: AudioOnlineViewModel by inject()
    private lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
        binding.swipeRefresh.setOnRefreshListener {
            displayLoading()
            songAdapter.reset()
            viewModel.checkInternetConnectionAndFetch()
        }
    }

    override fun onStart() {
        displayLoading()
        if (NetworkLiveData.value == false) displayNoNetworkConnection()
        songAdapter.reset()
        super.onStart()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        NetworkLiveData.observe(viewLifecycleOwner, Observer {
            if (!it) {
                displayNoNetworkConnection()
                songAdapter.reset()
            } else {
                displayLoading()
                viewModel.checkInternetConnectionAndFetch()
            }
        })
        viewModel.liveSongs.observe(viewLifecycleOwner, Observer {
            it?.let { songs ->
                displayEmptySongs()
                songAdapter.accept(songs)
                if (binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        })
        viewModel.isPlaying.observe(viewLifecycleOwner, Observer {
            getService()?.getCurrentSongOrNull()?.let {
                notifyAdapterDataSetChanged()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapterDataSetChanged() {
        songAdapter.notifyDataSetChanged()
    }

    private fun setRecycleView() {
        songAdapter = SongAdapter().apply {
            val listener = object : SongAdapter.OnClickSongListener {
                override fun onClickSong(position: Int) {
                    viewModel.songItemClicked(position)
                    (activity as? HomeActivity)?.openPlayer()
                    notifyAdapterDataSetChanged()
                }
            }
            setOnClickSongListener(listener)
        }
        binding.listSong.run {
            displayLoading()
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            hasFixedSize()
            isNestedScrollingEnabled = false
            setItemViewCacheSize(20)
            adapter = songAdapter
        }
    }

    private fun displayLoading() {
        binding.listSong.setEmptyView(binding.loadingLayout.loadingView)
        binding.emptyLayout.emptyView.visibility = View.GONE
        binding.noNetworkLayout.noNetworkView.visibility = View.GONE
    }

    private fun displayNoNetworkConnection() {
        binding.listSong.setEmptyView(binding.noNetworkLayout.noNetworkView)
        binding.emptyLayout.emptyView.visibility = View.GONE
        binding.loadingLayout.loadingView.visibility = View.GONE
    }

    private fun displayEmptySongs() {
        binding.listSong.setEmptyView(binding.emptyLayout.emptyView)
        binding.loadingLayout.loadingView.visibility = View.GONE
        binding.noNetworkLayout.noNetworkView.visibility = View.GONE
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = LayoutListSongBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance() = AudioOnlineFragment()
        const val TAG = "AudioOnlineFragment"
    }
}
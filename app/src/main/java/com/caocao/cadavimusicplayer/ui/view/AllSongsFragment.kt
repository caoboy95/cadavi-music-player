package com.caocao.cadavimusicplayer.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caocao.cadavimusicplayer.base.BaseFragment
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.databinding.FragmentAllSongsBinding
import com.caocao.cadavimusicplayer.ui.HomeActivity
import com.caocao.cadavimusicplayer.ui.adapter.SongAdapter
import com.caocao.cadavimusicplayer.ui.viewmodel.AllSongViewModel
import com.caocao.cadavimusicplayer.util.getService
import com.caocao.cadavimusicplayer.util.isPermissionGranted
import org.koin.android.ext.android.inject


class AllSongsFragment : BaseFragment<AllSongViewModel, FragmentAllSongsBinding>() {
    override val viewModel: AllSongViewModel by inject()
    private lateinit var songAdapter : SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
        setHasOptionsMenu(true)
        binding.shimmerViewContainer.startShimmerAnimation()
        binding.recycleListSong
        binding.shuffleAll.setOnClickListener {
            viewModel.shuffleAll()
            (activity as? HomeActivity)?.openPlayer()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveSongs.observe(viewLifecycleOwner, Observer { listSongs ->
            listSongs?.let { songs ->
                    when {
                        !isPermissionGranted() -> {
                            updateWithPermissionDenied()
                        }
                        else -> {
                            updateWithPermissionAllow(songs)
                        }
                    }
                }
            })
        viewModel.isPlaying.observe(viewLifecycleOwner, Observer {
            getService()?.getCurrentSongOrNull()?.let {
                notifyAdapterDataSetChanged()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.recycleListSong.stopObserver()
    }

    //Activity will call
    fun updateWithPermissionDenied() {
        stopShimmerView()
        binding.recycleListSong.visibility = View.GONE
        binding.emptySongs.emptyViewAll.visibility = View.GONE
    }

    private fun updateWithPermissionAllow(songs : List<Song>) {
        songAdapter.accept(songs)
        stopShimmerView()
    }

    private fun stopShimmerView() {
        binding.waitingLayout.visibility = View.INVISIBLE
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapterDataSetChanged() {
        songAdapter.notifyDataSetChanged()
    }

    private fun setRecycleView() {
        songAdapter = SongAdapter().apply {
            setOnClickSongListener(object : SongAdapter.OnClickSongListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onClickSong(position: Int) {
                    viewModel.songItemClicked(position)
                    (activity as? HomeActivity)?.openPlayer()
                    notifyAdapterDataSetChanged()
                }
            })
        }
        binding.recycleListSong.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            hasFixedSize()
            isNestedScrollingEnabled = false
            setEmptyView(binding.emptySongs.emptyViewAll)
        }
        binding.recycleListSong.adapter = songAdapter
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAllSongsBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance() = AllSongsFragment()
        private const val TAG = "AllSongsFragment"
    }
}
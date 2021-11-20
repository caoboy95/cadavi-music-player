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
import com.caocao.cadavimusicplayer.databinding.FragmentFavoriteBinding
import com.caocao.cadavimusicplayer.ui.HomeActivity
import com.caocao.cadavimusicplayer.ui.adapter.SongAdapter
import com.caocao.cadavimusicplayer.ui.viewmodel.FavoriteViewModel
import org.koin.android.ext.android.inject

class FavoriteFragment: BaseFragment<FavoriteViewModel, FragmentFavoriteBinding>() {
    override val viewModel: FavoriteViewModel by inject()
    private lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveSongs.observe(viewLifecycleOwner, Observer {
            songAdapter.accept(it)
        })
        viewModel.isPlaying.observe(viewLifecycleOwner, Observer {
            notifyAdapterDataSetChanged()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapterDataSetChanged() {
        songAdapter.notifyDataSetChanged()
    }

    private fun setRecycleView() {
        songAdapter = SongAdapter().apply {
            setOnClickSongListener(object : SongAdapter.OnClickSongListener {
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
            setEmptyView(binding.emptyLayout.emptyView)
        }
        binding.recycleListSong.adapter = songAdapter
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFavoriteBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance() = FavoriteFragment()
        const val TAG = "FavoriteFragment"
    }
}
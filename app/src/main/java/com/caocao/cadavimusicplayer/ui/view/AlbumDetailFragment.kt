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
import com.caocao.cadavimusicplayer.data.model.Album
import com.caocao.cadavimusicplayer.databinding.FragmentAlbumDetailBinding
import com.caocao.cadavimusicplayer.ui.HomeActivity
import com.caocao.cadavimusicplayer.ui.adapter.SongAdapter
import com.caocao.cadavimusicplayer.ui.viewmodel.AlbumDetailViewModel
import com.caocao.cadavimusicplayer.util.ArgumentUtil
import com.caocao.cadavimusicplayer.util.getService
import com.caocao.cadavimusicplayer.util.loadArtSong
import org.koin.android.ext.android.inject

class AlbumDetailFragment: BaseFragment<AlbumDetailViewModel, FragmentAlbumDetailBinding>() {
    override val viewModel: AlbumDetailViewModel by inject()
    private lateinit var songAdapter: SongAdapter
    private var album: Album? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.run {
            val albumId = getLong(ArgumentUtil.ARGUMENT_ID_ALBUMS)
            val albumName = getString(ArgumentUtil.ARGUMENT_NAME_ALBUMS)
            val artistName = getString(ArgumentUtil.ARGUMENT_ARTIST_ALBUMS)
            val songCount =  getInt(ArgumentUtil.ARGUMENT_COUNT_ALBUMS)
            album?.let {
                if (it.id != albumId && albumName != null && artistName != null) {
                    album = Album(albumId, albumName, artistName).apply { this.songCount = songCount }
                }
            } ?: run {
                if (albumName != null && artistName != null) {
                    album = Album(albumId, albumName, artistName).apply { this.songCount = songCount }
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAlbumDetailBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        updateUI()
        album?.let { viewModel.getSongs(it.id) }
        viewModel.liveSongs.observe(viewLifecycleOwner, Observer {
            it?.let {
                songAdapter.accept(it)
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

    private fun updateUI() {
        album?.run {
            binding.imgAlbums.loadArtSong(id)
            binding.artistAlbum.text = artist
            binding.back.setOnClickListener { activity?.onBackPressed() }
            binding.imgShuffAlbum.setOnClickListener {
                viewModel.shuffleAll()
                (activity as? HomeActivity)?.openPlayer()
            }
            val songCount = "${this.songCount} Song"
            binding.countSongAlbums.text = songCount
            binding.nameAlbums.text = title
        }
    }

    private fun setRecyclerView() {
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
        binding.songAlbumRecyclerView.run {
            hasFixedSize()
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = songAdapter
            isNestedScrollingEnabled = false
        }
    }

    companion object {
        const val TAG = "AlbumDetailFragment"
    }
}
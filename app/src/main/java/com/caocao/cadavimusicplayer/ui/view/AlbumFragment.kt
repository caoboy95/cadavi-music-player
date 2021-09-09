package com.caocao.cadavimusicplayer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.base.BaseFragment
import com.caocao.cadavimusicplayer.data.model.Album
import com.caocao.cadavimusicplayer.databinding.FragmentAlbumBinding
import com.caocao.cadavimusicplayer.ui.adapter.AlbumAdapter
import com.caocao.cadavimusicplayer.ui.viewmodel.AlbumViewModel
import com.caocao.cadavimusicplayer.util.ArgumentUtil.ARGUMENT_ARTIST_ALBUMS
import com.caocao.cadavimusicplayer.util.ArgumentUtil.ARGUMENT_COUNT_ALBUMS
import com.caocao.cadavimusicplayer.util.ArgumentUtil.ARGUMENT_ID_ALBUMS
import com.caocao.cadavimusicplayer.util.ArgumentUtil.ARGUMENT_NAME_ALBUMS
import com.caocao.cadavimusicplayer.util.OnMyItemClickListener
import org.koin.android.ext.android.inject

class AlbumFragment: BaseFragment<AlbumViewModel, FragmentAlbumBinding>() {
    override val viewModel: AlbumViewModel by inject()
    private lateinit var albumAdapter: AlbumAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.album.observe(viewLifecycleOwner, Observer {
            it?.let {
                albumAdapter.accept(it)
            }
        })
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAlbumBinding.inflate(inflater, container, false)

    fun init() {

    }

    private fun setRecycleView() {
        albumAdapter = AlbumAdapter().apply {
            listener = object : OnMyItemClickListener {
                override fun onItemClick(position: Int) {
                    viewModel.album.value?.get(position)?.let { openAlbumDetail(it) }
                }
            }
        }
        binding.albumsList.run {
            setEmptyView(binding.emptyLayout.emptyView)
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            hasFixedSize()
            isNestedScrollingEnabled = false
            setItemViewCacheSize(20)
            adapter = albumAdapter
        }
    }

    private fun openAlbumDetail(album: Album) {
        val bundle = Bundle()
        bundle.putLong(ARGUMENT_ID_ALBUMS, album.id)
        bundle.putString(ARGUMENT_NAME_ALBUMS, album.title)
        bundle.putInt(ARGUMENT_COUNT_ALBUMS, album.songCount)
        bundle.putString(ARGUMENT_ARTIST_ALBUMS, album.artist)
        var mainFragment = AlbumDetailFragment()
        mainFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().add(R.id.relative_home, mainFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = AlbumFragment()
        const val TAG = "AlbumFragment"
    }
}
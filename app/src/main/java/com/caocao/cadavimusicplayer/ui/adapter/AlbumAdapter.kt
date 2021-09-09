package com.caocao.cadavimusicplayer.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caocao.cadavimusicplayer.data.model.Album
import com.caocao.cadavimusicplayer.databinding.AlbumItemBinding
import com.caocao.cadavimusicplayer.util.OnMyItemClickListener
import com.caocao.cadavimusicplayer.util.loadArtSong
import io.reactivex.rxjava3.functions.Consumer

class AlbumAdapter: RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>(), Consumer<List<Album>> {
    private var albums: List<Album> = listOf()
    var listener: OnMyItemClickListener? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun accept(t: List<Album>) {
        this.albums = t
        notifyDataSetChanged()
    }

    inner class AlbumViewHolder(private val binding: AlbumItemBinding, private val listener: OnMyItemClickListener?): RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album, position: Int) {
            binding.albumTitle.text = album.title
            binding.artistTitle.text = album.artist
            binding.gridArt.loadArtSong(album.id)
            binding.root.setOnClickListener { listener?.onItemClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlbumItemBinding.inflate(inflater, parent, false)
        return AlbumViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position], position)
    }

    override fun getItemCount(): Int = albums.size
}
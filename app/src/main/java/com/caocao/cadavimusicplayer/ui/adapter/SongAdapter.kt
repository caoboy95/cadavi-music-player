package com.caocao.cadavimusicplayer.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.databinding.ItemSongBinding
import com.caocao.cadavimusicplayer.util.getService
import com.caocao.cadavimusicplayer.util.loadArtSong
import io.reactivex.functions.Consumer

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongHolder>(), Consumer<List<Song>> {
    private var songs: List<Song> = ArrayList()
    private lateinit var listener : OnClickSongListener

    @SuppressLint("NotifyDataSetChanged")
    override fun accept(songs: List<Song>) {
        this.songs = songs
        notifyDataSetChanged()
        Log.d(TAG, songs.size.toString())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reset() {
        songs = ArrayList()
        notifyDataSetChanged()
    }

    fun setOnClickSongListener(listener: OnClickSongListener) {
        this.listener = listener
    }

    interface OnClickSongListener {
        fun onClickSong(position: Int)
    }

    class SongHolder(private val binding: ItemSongBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, position: Int, listener: OnClickSongListener) {
            setImagePlayPause(song)
            binding.nameSong.text = song.title
            binding.artist.text = song.artistName
            binding.rootListItem.setOnClickListener {
                listener.onClickSong(position)
            }
            binding.imageViewSong.loadArtSong(song)
        }

        private fun setImagePlayPause(song: Song) {
            getService()?.run {
                getCurrentSongOrNull()?.let {
                    if (song.equals(it)) {
                        binding.imgPlaying.visibility = View.VISIBLE
                        binding.nameSong.setTextColor(resources.getColor(R.color.tab_color))
                        if (isPlaying()) {
                            binding.imgPlaying.visibility = View.INVISIBLE
                            binding.imgListening.visibility = View.VISIBLE
                        } else {
                            binding.imgPlaying.visibility = View.VISIBLE
                            binding.imgListening.visibility = View.GONE
                        }
                        return
                    }
                }
                binding.nameSong.setTextColor(resources.getColor(R.color.color_white))
                binding.imgListening.visibility = View.GONE
                binding.imgPlaying.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSongBinding.inflate(inflater, parent, false)
        return SongHolder(binding)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        holder.bind(songs[position], position, listener)
    }

    override fun getItemCount(): Int = if (songs.isNotEmpty()) songs.size else 0

    fun getSongPosition(song: Song) = songs.indexOf(song)

    companion object {
        private const val TAG = "SongAdapter"
    }
}


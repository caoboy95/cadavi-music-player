package com.caocao.cadavimusicplayer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.base.BasePlayerFragment
import com.caocao.cadavimusicplayer.data.model.OnlineSong
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.databinding.FragmentMusicPlayerBinding
import com.caocao.cadavimusicplayer.service.MusicPlayer
import com.caocao.cadavimusicplayer.ui.HomeActivity
import com.caocao.cadavimusicplayer.ui.viewmodel.MusicPlayerViewModel
import com.caocao.cadavimusicplayer.util.exchangeDurationToText
import com.caocao.cadavimusicplayer.util.getService
import com.caocao.cadavimusicplayer.util.loadArtSong
import org.koin.android.ext.android.inject


class MusicPlayerFragment : BasePlayerFragment<MusicPlayerViewModel, FragmentMusicPlayerBinding>(), SeekBar.OnSeekBarChangeListener
         {

    override val viewModel: MusicPlayerViewModel by inject()
    private var canAutoProgress = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePlayingModeButton()
        updateShuffleButton()
        viewModel.setOnShuffleChangeListener(::updateShuffleButton)
    }

    private fun init() {
        binding.run {
            gotoNext.setOnClickListener { getService()?.goToNext() }
            gotoBack.setOnClickListener { getService()?.goToBack() }
            play.setOnClickListener { getService()?.playPauseToggle() }
            progress.setOnSeekBarChangeListener(this@MusicPlayerFragment)
            repeat.setOnClickListener {
                    viewModel.changePlayingMode()
                    updatePlayingModeButton()
                }
            shuffle.setOnClickListener { viewModel.run { isShuffle = !isShuffle } }
            explain.setOnClickListener { (activity as? HomeActivity)?.closePlayer() }
        }
    }

    private fun updatePlayingModeButton() {
        binding.repeat.run {
            when (viewModel.playingMode) {
                MusicPlayer.PlayingMode.NORMAL_MODE -> {
                    setImageResource(R.drawable.ic_repeat_off)
                    contentDescription = resources.getString(R.string.repeat_button)
                }
                MusicPlayer.PlayingMode.REPEAT_ONE_MODE -> {
                    setImageResource(R.drawable.ic_repeat_one)
                    contentDescription = resources.getString(R.string.repeat_off_button)
                }
                MusicPlayer.PlayingMode.REPEAT_MODE -> {
                    contentDescription = resources.getString(R.string.repeat_one_button)
                    setImageResource(R.drawable.ic_repeat_all)
                }
            }
        }
    }

    private fun updateShuffleButton() {
        binding.shuffle.run {
            contentDescription = if (viewModel.isShuffle) {
                setImageResource(R.drawable.ic_shuffle_white)
                resources.getString(R.string.shuffle_on_button)
            } else {
                setImageResource(R.drawable.ic_un_shuffle)
                resources.getString(R.string.shuffle_off_button)
            }
        }
    }

    private fun updateFavoriteImage(songId: Long) {
        getService()?.getCurrentSongOrNull()?.let {
            if (it is OnlineSong) {
                binding.favorite.visibility = View.GONE
                return
            } else {
                binding.favorite.visibility = View.VISIBLE
            }
        }
        if (viewModel.isFavorited(songId)) {
            binding.favorite.setImageResource(R.drawable.ic_favorite)
            binding.favorite.setOnClickListener {
                viewModel.removeFavorite(songId)
                updateFavoriteImage(songId)
            }
            return
        }
        binding.favorite.setImageResource(R.drawable.ic_unfavorite)
        binding.favorite.setOnClickListener {
            viewModel.addFavorite(songId)
            updateFavoriteImage(songId)
        }
    }

    fun setVisibility(visibility: Int) {
        binding.settingContent.visibility = visibility
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        canAutoProgress = false
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.run {
            getService()?.currentPosition = progress
            canAutoProgress = true
        }
    }

    override fun setProgressAction(process: Int) {
        if (canAutoProgress) {
            binding.progress.progress = process
        }
        binding.current.text = exchangeDurationToText(process)
    }

    override fun setCurrentSongAction(song: Song) {
        binding.title.text = song.title
        binding.progress.max = song.duration.toInt()
        binding.duration.text = exchangeDurationToText(song.duration.toInt())
        getService()?.run {
            binding.playerArt.loadArtSong(getCurrentSongOrNull())
        }
        updateFavoriteImage(song.id)
    }

    override fun setPlayPauseAction(isPlaying: Boolean) {
        if (isPlaying) {
            binding.play.setImageResource(R.drawable.ic_pause)
            binding.play.contentDescription = resources.getString(R.string.play_button)
            binding.playingState.text = resources.getString(R.string.now_is_playing)
        } else {
            binding.play.setImageResource(R.drawable.ic_play)
            binding.play.contentDescription = resources.getString(R.string.pause_button)
            binding.playingState.text = resources.getString(R.string.now_is_pausing)
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMusicPlayerBinding.inflate(inflater, container, false)

    companion object {
        const val TAG = "MusicPlayerFragment"
    }
}
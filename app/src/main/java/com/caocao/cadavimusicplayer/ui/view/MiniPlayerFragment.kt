package com.caocao.cadavimusicplayer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caocao.cadavimusicplayer.R
import com.caocao.cadavimusicplayer.base.BasePlayerFragment
import com.caocao.cadavimusicplayer.data.model.Song
import com.caocao.cadavimusicplayer.databinding.FragmentMiniPlayerBinding
import com.caocao.cadavimusicplayer.ui.HomeActivity
import com.caocao.cadavimusicplayer.ui.viewmodel.MiniPlayerViewModel
import com.caocao.cadavimusicplayer.util.getService
import com.caocao.cadavimusicplayer.util.loadArtSong
import org.koin.android.ext.android.inject

class MiniPlayerFragment : BasePlayerFragment<MiniPlayerViewModel, FragmentMiniPlayerBinding>() {

    override val viewModel: MiniPlayerViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        binding.run {
            miniPlayerTitle.isSelected = true
            miniPlayerLayout.setOnClickListener { (activity as? HomeActivity)?.openPlayer() }
            miniPlayerNext.setOnClickListener { getService()?.goToNext() }
            miniPlayerPlaypause.setOnClickListener { getService()?.playPauseToggle() }
        }
    }

    fun setAlpha(alpha: Float) {
        binding.miniPlayerLayout.alpha = alpha
    }

    fun setVisibility(visibility: Int) {
        binding.miniPlayerLayout.visibility = visibility
    }

    override fun setProgressAction(process: Int) {
        binding.miniPlayerProgress.progress = process
    }

    override fun setCurrentSongAction(song: Song) {
        binding.miniPlayerTitle.text = song.title
        binding.miniPlayerProgress.max = song.duration.toInt()
        getService()?.run {
            binding.miniPlayerArt.loadArtSong(getCurrentSongOrNull())
        }
    }

    override fun setPlayPauseAction(isPlaying: Boolean) {
        if (isPlaying) {
            binding.miniPlayerPlaypause.setImageResource(R.drawable.ic_pause)
            binding.miniPlayerPlaypause.contentDescription = resources.getString(R.string.play_button)
        } else {
            binding.miniPlayerPlaypause.setImageResource(R.drawable.ic_play)
            binding.miniPlayerPlaypause.contentDescription = resources.getString(R.string.pause_button)
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMiniPlayerBinding.inflate(inflater, container, false)

    companion object {
        const val TAG = "MiniPlayerFragment"
    }
}
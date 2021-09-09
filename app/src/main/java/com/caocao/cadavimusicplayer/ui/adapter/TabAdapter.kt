package com.caocao.cadavimusicplayer.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caocao.cadavimusicplayer.ui.view.AlbumFragment
import com.caocao.cadavimusicplayer.ui.view.AllSongsFragment

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }
    
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AllSongsFragment.newInstance()
            else -> AlbumFragment.newInstance()
        }
    }
}

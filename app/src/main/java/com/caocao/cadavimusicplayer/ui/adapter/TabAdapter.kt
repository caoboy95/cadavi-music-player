package com.caocao.cadavimusicplayer.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caocao.cadavimusicplayer.ui.view.AllSongsFragment

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

//    override fun getItem(position: Int): Fragment {
//
//    }
//
//    override fun getPageTitle(position: Int): CharSequence {
//        return when(position) {
//            0 -> context.resources.getString(R.string.all_song)
//            1 -> context.resources.getString(R.string.favorite)
//            2 -> context.resources.getString(R.string.artist)
//            3 -> context.resources.getString(R.string.playlist)
//            4 -> context.resources.getString(R.string.album)
//            else -> context.resources.getString(R.string.genres)
//        }
//    }

    override fun getItemCount(): Int {
        return 1
    }
    
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AllSongsFragment.newInstance()
            else -> AllSongsFragment.newInstance()
        }
    }
}

package com.engineersapps.eapps.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class VideoTabViewPagerAdapter internal constructor(
    private val size: Int,
    fragment: Fragment,
    private val isTab4: Boolean = false
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            //0 -> if (isTab4) Tab4Fragment() else VideoListFragment()
            0 -> VideoListFragment()
            1 -> Tab1Fragment()
            2 -> Tab2Fragment()
            3 -> Tab3Fragment()
            else -> VideoListFragment()
        }
    }
}
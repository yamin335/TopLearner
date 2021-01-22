package com.rtchubs.engineerbooks.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rtchubs.engineerbooks.models.chapter.ChapterField
import com.rtchubs.engineerbooks.ui.solution.SolutionFragment

class VideoTabViewPagerAdapter internal constructor(
    private val size: Int,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> VideoListFragment()
            1 -> SolutionFragment()
            2 -> SetCFragment()
            3 -> QuizListFragment()
            else -> VideoListFragment()
        }
    }
}
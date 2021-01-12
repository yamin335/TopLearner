package com.rtchubs.engineerbooks.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.VideoListFragmentBinding
import com.rtchubs.engineerbooks.models.VideoItem
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment

class VideoListFragment : BaseFragment<VideoListFragmentBinding, VideoListViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_video_list

    override val viewModel: VideoListViewModel by viewModels { viewModelFactory }

    lateinit var videoListAdapter: VideoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoListAdapter = VideoListAdapter (
            appExecutors
        ) { item ->
            setFragmentResult("playVideo", bundleOf("VideoItem" to item))
        }

        viewDataBinding.rvVideoList.adapter = videoListAdapter

        videoListAdapter.submitList(LoadWebViewFragment.chapter.fields)
    }
}
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

class VideoListFragment : BaseFragment<VideoListFragmentBinding, VideoListViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_video_list

    override val viewModel: VideoListViewModel by viewModels { viewModelFactory }

    lateinit var videoListAdapter: VideoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoListAdapter = VideoListAdapter(
            appExecutors
        ) { item ->
            setFragmentResult("playVideo", bundleOf("VideoItem" to item))
        }

        viewDataBinding.rvVideoList.adapter = videoListAdapter

        val list = ArrayList<VideoItem>()

        var i = 1
         while (i <= 10) {
             list.add(VideoItem(i, "Video Lesson Part - $i", "", "Video can be interlaced or progressive. " +
                     "In progressive scan systems, each refresh period updates all scan lines in each frame in sequence. " +
                     "When displaying a natively progressive broadcast or recorded signal, the result is optimum spatial " +
                     "resolution of both the stationary and moving parts of the image."))
             i++
         }

        videoListAdapter.submitList(list)
    }
}
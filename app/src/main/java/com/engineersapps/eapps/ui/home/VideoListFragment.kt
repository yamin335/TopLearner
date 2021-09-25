package com.engineersapps.eapps.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.VideoListFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.video_play.LoadWebViewFragment

class VideoListFragment : BaseFragment<VideoListFragmentBinding, VideoListViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_video_list

    override val viewModel: VideoListViewModel by viewModels { viewModelFactory }

    lateinit var videoListAdapter: VideoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "showHideProgress",
            viewLifecycleOwner, FragmentResultListener { key, bundle ->
                val shouldShowProgress = bundle.getBoolean("progressStatus")
                val progress = bundle.getInt("value", 0)
                try {
                    if (shouldShowProgress) {
                        viewDataBinding.progressView.visibility = View.VISIBLE
                        viewDataBinding.loader.progress = progress
                        viewDataBinding.progress.text = "$progress%"
                    } else {
                        viewDataBinding.progressView.visibility = View.GONE
                        videoListAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        )

        videoListAdapter = VideoListAdapter ( appExecutors, { item ->
            setFragmentResult("playVideo", bundleOf("VideoItem" to item))
        }, { item ->
            setFragmentResult("downloadVideo", bundleOf("VideoItem" to item))
        })

        viewDataBinding.rvVideoList.adapter = videoListAdapter

        val animationList = LoadWebViewFragment.chapter.fields?.filter { it.type == "video" }
        if (animationList.isNullOrEmpty()) {
            viewDataBinding.emptyView.visibility = View.VISIBLE
        } else {
            viewDataBinding.emptyView.visibility = View.GONE
            videoListAdapter.submitList(animationList)
        }
    }
}
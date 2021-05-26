package com.rtchubs.engineerbooks.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseDetailsFragmentBinding
import com.rtchubs.engineerbooks.models.home.CourseSubject
import com.rtchubs.engineerbooks.models.home.CourseTeacher
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class CourseDetailsFragment : BaseFragment<CourseDetailsFragmentBinding, CourseDetailsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_course_details
    override val viewModel: CourseDetailsViewModel by viewModels { viewModelFactory }

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var teachersAdapter: CourseDetailsTeachersListAdapter
    private lateinit var subjectsAdapter: CourseDetailsSubjectListAdapter

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )

        player = SimpleExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector).build()
        viewDataBinding.videoView.player = player

        val mediaItem = MediaItem.Builder()
            .setUri("https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0")
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        player?.setMediaItem(mediaItem)
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare()
    }

    private fun releasePlayer() {
        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.release()
            player = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if ((Build.VERSION.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        val teacheers = listOf(CourseTeacher(1, "Alexander Houluo", "University of Dhaka"),
            CourseTeacher(2, "Alexander Houluo", "University of Dhaka"),
            CourseTeacher(3, "Alexander Houluo", "University of Dhaka"),
            CourseTeacher(4, "Alexander Houluo", "University of Dhaka"),
            CourseTeacher(5, "Alexander Houluo", "University of Dhaka"),
            CourseTeacher(6, "Alexander Houluo", "University of Dhaka"))

        teachersAdapter = CourseDetailsTeachersListAdapter(appExecutors) {

        }

        viewDataBinding.teachersRecycler.adapter = teachersAdapter
        teachersAdapter.submitList(teacheers)

        val subjects = listOf(
            CourseSubject(1, "Subject - 1"),
            CourseSubject(2, "Subject - 2"),
            CourseSubject(3, "Subject - 3"),
            CourseSubject(4, "Subject - 4"),
            CourseSubject(5, "Subject - 5"),
            CourseSubject(6, "Subject - 6"))

        subjectsAdapter = CourseDetailsSubjectListAdapter(appExecutors) {

        }
        viewDataBinding.courseDetailsRecycler.setHasFixedSize(true)
        viewDataBinding.courseDetailsRecycler.itemAnimator = DefaultItemAnimator()
        viewDataBinding.courseDetailsRecycler.adapter = subjectsAdapter
        subjectsAdapter.submitList(subjects)
    }
}
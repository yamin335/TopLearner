package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseDetailsFragmentBinding
import com.rtchubs.engineerbooks.models.home.PaidBook
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
    private lateinit var teachersAdapter: CourseTeachersListAdapter
    private lateinit var contentsAdapter: CourseContentListAdapter
    private lateinit var faqsAdapter: CourseFaqListAdapter

    private val args: CourseDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        val course = args.course
        var price = 0.0

        if (course.discount_price != null && course.discount_price != 0) {
            viewDataBinding.price.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                text = course.price.toString()
            }
            viewDataBinding.discountPrice.visibility = View.VISIBLE
            viewDataBinding.discountPrice.text = course.discount_price.toString()
            price = course.discount_price?.toDouble() ?: 0.0
        } else {
            viewDataBinding.price.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                text = course.price.toString()
            }
            viewDataBinding.discountPrice.visibility = View.GONE
            price = course.price?.toDouble() ?: 0.0
        }

        viewDataBinding.btnBuyNow.setOnClickListener {
            navigateTo(CourseDetailsFragmentDirections.actionCourseDetailsFragmentToPaymentFragment(
                PaidBook(0, course.title, 1, course.class_name, false, price)
            ))
        }

//        val teacheers = listOf(
//            CourseTeachers(1, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(2, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(3, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(4, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(5, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(6, "Alexander Houluo", "University of Dhaka")
//        )

        teachersAdapter = CourseTeachersListAdapter(appExecutors) {

        }

        viewDataBinding.teachersRecycler.adapter = teachersAdapter
        teachersAdapter.submitList(course.course_teachers)

//        val subjects = listOf(
//            CourseSubject(1, "Subject - 1"),
//            CourseSubject(2, "Subject - 2"),
//            CourseSubject(3, "Subject - 3"),
//            CourseSubject(4, "Subject - 4"),
//            CourseSubject(5, "Subject - 5"),
//            CourseSubject(6, "Subject - 6")
//        )

        contentsAdapter = CourseContentListAdapter(appExecutors) {

        }
        viewDataBinding.courseDetailsRecycler.setHasFixedSize(true)
        viewDataBinding.courseDetailsRecycler.itemAnimator = DefaultItemAnimator()
        viewDataBinding.courseDetailsRecycler.adapter = contentsAdapter
        contentsAdapter.submitList(course.course_content)

        faqsAdapter = CourseFaqListAdapter(appExecutors) {

        }
        viewDataBinding.faqRecycler.setHasFixedSize(true)
        viewDataBinding.faqRecycler.itemAnimator = DefaultItemAnimator()
        viewDataBinding.faqRecycler.adapter = faqsAdapter
        faqsAdapter.submitList(course.course_faq)
    }

    private fun mediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer")
        ).createMediaSource(uri)
    }

    @SuppressLint("StaticFieldLeak")
    private fun initializePlayer() {
        val videoUrl = args.course.vediolink
        val trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )

        player = SimpleExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector).build()
        viewDataBinding.videoView.player = player

        object : YouTubeExtractor(requireContext()) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>, vMeta: VideoMeta) {
                val downloadUrl = ytFiles[18].url
                try {
                    player?.addMediaSource(mediaSource(Uri.parse(downloadUrl)))
                    player?.playWhenReady = playWhenReady
                    player?.seekTo(currentWindow, playbackPosition)
                    player?.prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.extract(videoUrl, true, true)
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
}
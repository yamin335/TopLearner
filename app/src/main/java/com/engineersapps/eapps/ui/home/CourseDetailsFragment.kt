package com.engineersapps.eapps.ui.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.CourseDetailsFragmentBinding
import com.engineersapps.eapps.models.home.Course
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.payment.PaymentFragment
import com.engineersapps.eapps.util.showErrorToast
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CourseDetailsFragment : BaseFragment<CourseDetailsFragmentBinding, CourseDetailsViewModel>() {
    
    companion object {
        var course: Course? = null
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_course_details
    override val viewModel: CourseDetailsViewModel by viewModels { viewModelFactory }

    private var player: SimpleExoPlayer? = null
    private lateinit var teachersAdapter: TeachersListAdapter
    private lateinit var contentsAdapter: CourseContentListAdapter
    private lateinit var faqsAdapter: FaqListAdapter

    //private val args: CourseDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //registerToolbar(viewDataBinding.toolbar)

        //val course = args.course
        val price: String

        val totalPrice = course?.price ?: 0
        val totalDiscount = course?.discount_price ?: 0

        viewDataBinding.title.text = course?.title

        viewDataBinding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        if (totalDiscount != 0) {
            viewDataBinding.price.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                text = totalPrice.toString()
            }
            viewDataBinding.discountPrice.visibility = View.VISIBLE
            val discountedPrice = totalPrice - totalDiscount
            viewDataBinding.discountPrice.text = discountedPrice.toString()
            price = discountedPrice.toString()
        } else {
            viewDataBinding.price.apply {
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                text = totalPrice.toString()
            }
            viewDataBinding.discountPrice.visibility = View.GONE
            price = totalPrice.toString()
        }

        viewDataBinding.btnBuyNow.setOnClickListener {
            PaymentFragment.firstPackageTitle = course?.first_payment_title ?: ""
            PaymentFragment.secondPackageTitle = course?.second_payment_title ?: ""
            PaymentFragment.thirdPackageTitle = course?.third_payment_title ?: ""

            PaymentFragment.firstPackagePrice = course?.first_payment_amount ?: 0
            PaymentFragment.secondPackagePrice = course?.second_payment_amount ?: 0
            PaymentFragment.thirdPackagePrice = course?.third_payment_amount ?: 0

            val firstDuration = course?.first_duration ?: "0"
            val secondDuration = course?.second_duration ?: "0"
            val thirdDuration = course?.third_duration ?: "0"
            PaymentFragment.first_duration = firstDuration.toInt()
            PaymentFragment.second_duration = secondDuration.toInt()
            PaymentFragment.third_duration = thirdDuration.toInt()

            navigateTo(CourseDetailsFragmentDirections.actionCourseDetailsFragmentToPaymentNav(course?.book_id ?: 0, course?.title, course?.id ?:0, price, "", "", 0))
        }

        course?.course_items?.let {
            for (courseItem in it) {
                val chip = layoutInflater.inflate(R.layout.chip_for_course_details, viewDataBinding.chipGroup, false) as Chip
                chip.text = courseItem.description
                viewDataBinding.chipGroup.addView(chip)
            }
        }

//        val teacheers = listOf(
//            CourseTeachers(1, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(2, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(3, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(4, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(5, "Alexander Houluo", "University of Dhaka"),
//            CourseTeachers(6, "Alexander Houluo", "University of Dhaka")
//        )

        teachersAdapter = TeachersListAdapter(appExecutors) {

        }
        viewDataBinding.teachersRecycler.adapter = teachersAdapter
        teachersAdapter.submitList(course?.course_teachers)
        viewDataBinding.tvLabelTeacher.visibility = if (course?.course_teachers.isNullOrEmpty()) View.GONE else View.VISIBLE

//        val subjects = listOf(
//            CourseSubject(1, "Subject - 1"),
//            CourseSubject(2, "Subject - 2"),
//            CourseSubject(3, "Subject - 3"),
//            CourseSubject(4, "Subject - 4"),
//            CourseSubject(5, "Subject - 5"),
//            CourseSubject(6, "Subject - 6")
//        )

        contentsAdapter = CourseContentListAdapter()
        (viewDataBinding.courseDetailsRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        //viewDataBinding.courseDetailsRecycler.setHasFixedSize(true)
        //viewDataBinding.courseDetailsRecycler.itemAnimator = DefaultItemAnimator()
        viewDataBinding.courseDetailsRecycler.adapter = contentsAdapter
//        contentsAdapter.submitList(course?.course_chapter)

        faqsAdapter = FaqListAdapter(appExecutors) {

        }
        viewDataBinding.faqRecycler.itemAnimator = DefaultItemAnimator()
        viewDataBinding.faqRecycler.adapter = faqsAdapter

        viewModel.allFaqList.observe(viewLifecycleOwner, Observer {
            faqsAdapter.submitList(it)
        })

        viewDataBinding.btnBookDetails.setOnClickListener {
            val bookId = course?.demo_book_id
            if (bookId == null) {
                showErrorToast(requireContext(), "কোন বই পাওয়া যায় নি")
                return@setOnClickListener
            }

            viewModel.getCourseFreeBookFromDB(bookId).observe(viewLifecycleOwner, Observer {
                val book = it
                if (book == null) {
                    showErrorToast(requireContext(), "কোন বই পাওয়া যায় নি")
                    return@Observer
                }
                navigateTo(CourseDetailsFragmentDirections.actionCourseDetailsFragmentToChapterNav(book.id, book.title))
            })
        }

        viewModel.getAllFaqs()
    }

    private fun mediaSource(uri: Uri): MediaSource {
        return ProgressiveMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer")
        ).createMediaSource(uri)
    }

    private fun initializePlayer() {
        val videoUrl = course?.vediolink
        val trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )
        player = SimpleExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector).build()
        viewDataBinding.videoView.player = player
        if (viewModel.youtubePlayerUrl.isNotBlank()) {
            playYoutubeVideo()
//            if (viewModel.youtubeVideoLink == videoUrl && player != null) {
//                player?.play()
//            } else {
//                playYoutubeVideo()
//            }
        } else {
            extractYoutubeVideoLink(videoUrl)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun extractYoutubeVideoLink(videoLink: String?) {
        viewModel.youtubeVideoLink = videoLink ?: ""
        object : YouTubeExtractor(requireContext()) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                if (ytFiles == null) return
                viewModel.youtubePlayerUrl = ytFiles[18]?.url ?: ""
                playYoutubeVideo()
            }
        }.extract(videoLink, true, true)
    }

    private fun playYoutubeVideo() {
        try {
            player?.addMediaSource(mediaSource(Uri.parse(viewModel.youtubePlayerUrl)))
            player?.playWhenReady = viewModel.playWhenReady
            player?.seekTo(viewModel.currentWindow, viewModel.playbackPosition)
            player?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releasePlayer() {
        player?.let {
            viewModel.playWhenReady = it.playWhenReady
            viewModel.playbackPosition = it.currentPosition
            viewModel.currentWindow = it.currentWindowIndex
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
        lifecycleScope.launch {
            delay(300)
            CoroutineScope(Dispatchers.Main.immediate).launch {
                course?.course_chapters?.let {
                    var i = 0
                    while (i < it.size) {
                        contentsAdapter.addItemToList(it[i], i)
                        if (i % 3 == 0) delay(150)
                        i++
                    }
                }
            }
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
package com.rtchubs.engineerbooks.ui.home

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
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.material.chip.Chip
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseDetailsFragmentBinding
import com.rtchubs.engineerbooks.models.home.Course
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.payment.PaymentFragment
import com.rtchubs.engineerbooks.util.showErrorToast
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
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private lateinit var teachersAdapter: TeachersListAdapter
    private lateinit var contentsAdapter: CourseChapterListAdapter
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

            navigateTo(CourseDetailsFragmentDirections.actionCourseDetailsFragmentToPaymentNav(course?.book_id ?: 0, course?.title, course?.id ?:0, price, "", ""))
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

        contentsAdapter = CourseChapterListAdapter(appExecutors) {

        }
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
        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer")
        ).createMediaSource(uri)
    }

    @SuppressLint("StaticFieldLeak")
    private fun initializePlayer() {
//        val videoUrl = args.course?.vediolink
        val videoUrl = course?.vediolink
        val trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )

        player = SimpleExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector).build()
        viewDataBinding.videoView.player = player

        object : YouTubeExtractor(requireContext()) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                if (ytFiles == null) return
                val downloadUrl = ytFiles[18]?.url
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
        lifecycleScope.launch {
            delay(1500)
            CoroutineScope(Dispatchers.Main.immediate).launch {
                contentsAdapter.submitList(course?.course_chapters)
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
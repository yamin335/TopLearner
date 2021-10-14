package com.engineersapps.eapps.ui.video_play

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.VideoPlayFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.util.FLAGS_FULLSCREEN
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

private const val IMMERSIVE_FLAG_TIMEOUT = 1000L
class VideoPlayFragment : BaseFragment<VideoPlayFragmentBinding, VideoPlayViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_video_play
    override val viewModel: VideoPlayViewModel by viewModels { viewModelFactory }

    var systemUiVisibility: Int = 0
    private var systemUIConfigurationBackup: Int = 0

    private val args: VideoPlayFragmentArgs by navArgs()

    var videoUrl = ""

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onResume() {
        super.onResume()
        if ((Build.VERSION.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        hideSystemUI()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        restoreSystemUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        args.url?.let {
            videoUrl = it
        }

        viewDataBinding.backArrow.setOnClickListener {
            navController.popBackStack()
        }

        viewDataBinding.videoView.setControllerVisibilityListener { visibility: Int ->
            if (visibility == 0) {
                viewDataBinding.backArrow.visibility = View.VISIBLE
            } else {
                viewDataBinding.backArrow.visibility = View.GONE
            }
        }
    }

    private fun hideSystemUI() {
        try {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            systemUIConfigurationBackup = requireActivity().window.decorView.systemUiVisibility
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            requireActivity().window.statusBarColor = Color.TRANSPARENT
            systemUiVisibility = viewDataBinding.mainContainer.systemUiVisibility
            viewDataBinding.mainContainer.postDelayed({
                viewDataBinding.mainContainer.systemUiVisibility = FLAGS_FULLSCREEN
            }, IMMERSIVE_FLAG_TIMEOUT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun restoreSystemUI() {
        try {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            requireActivity().window.decorView.systemUiVisibility = systemUIConfigurationBackup
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            viewDataBinding.mainContainer.postDelayed({
                viewDataBinding.mainContainer.systemUiVisibility = systemUiVisibility
            }, 0L)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun mediaSource(uri: Uri): MediaSource {
        return ProgressiveMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer")
        ).createMediaSource(uri)
    }

    @SuppressLint("StaticFieldLeak")
    private fun initializePlayer() {
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

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= 24) {
            releasePlayer()
        }
    }
}
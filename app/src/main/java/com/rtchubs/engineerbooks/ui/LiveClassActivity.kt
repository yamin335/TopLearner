package com.rtchubs.engineerbooks.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.LiveClassActivityBinding
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class LiveClassActivity : DaggerAppCompatActivity() {

    companion object {
        var videoUrl = "https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private val viewModel: LiveClassActivityViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    lateinit var binding: LiveClassActivityBinding

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this@LiveClassActivity,
            R.layout.activity_live_class
        )

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.videoView.setControllerVisibilityListener {
            binding.backArrow.visibility = if (it == 0) View.VISIBLE else View.GONE
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(this)
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )

        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        binding.videoView.player = player

        object : YouTubeExtractor(this) {
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

    private fun mediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer")
        ).createMediaSource(uri)
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
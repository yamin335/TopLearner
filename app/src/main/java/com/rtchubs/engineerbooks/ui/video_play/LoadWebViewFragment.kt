package com.rtchubs.engineerbooks.ui.video_play

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.rtchubs.engineerbooks.AppGlobalValues
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.WebViewBinding
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.models.VideoItem
import com.rtchubs.engineerbooks.ui.*
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.home.VideoListFragment
import com.rtchubs.engineerbooks.ui.home.QuizListFragment
import com.rtchubs.engineerbooks.ui.home.SetCFragment
import com.rtchubs.engineerbooks.ui.home.VideoTabViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_load_web_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.progress.ProgressMonitor
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class LoadWebViewFragment: BaseFragment<WebViewBinding, LoadWebViewViewModel>(), ConfigurationChangeCallback {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_load_web_view
    override val viewModel by viewModels<LoadWebViewViewModel>{
        viewModelFactory
    }

    @Inject
    lateinit var applicationContext: Application

    private val url by lazy {
        //arguments?.let { LoadWebViewFragmentArgs.fromBundle(it).url }
        "file:///android_asset/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html"
    }
    private val title by lazy {
        //arguments?.let { LoadWebViewFragmentArgs.fromBundle(it).title }
    }

    //private val viewPagerPageTitles = arrayOf("Video List", "Quiz", "Questions")
    private val viewPagerPageTitles = arrayOf("Video List", "Questions")

    private lateinit var pagerAdapter: VideoTabViewPagerAdapter

    private var viewPagerCurrentItem = 0

    private lateinit var viewPager2PageChangeCallback: ViewPager2PageChangeCallback

    private var isVideoStartedPlaying = false

    private val args: LoadWebViewFragmentArgs by navArgs()

    private var expanded = true
    private lateinit var toggle: Transition

    private val primaryExternalStorageAbsolutePath: String get() {
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(requireContext(), null)
        return externalStorageVolumes[0].absolutePath
    }

    private lateinit var source: String
    private lateinit var destination: String
    private val password = "1234".toCharArray()

    private var bottomNavShowHideCallback: ShowHideBottomNavCallback? = null

    private var systemUIConfigurationBackup: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ShowHideBottomNavCallback) {
            bottomNavShowHideCallback = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        toggle = TransitionInflater.from(requireContext()).inflateTransition(R.transition.search_bar_toogle)
        source = "$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka.zip"
        destination = "$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka"
    }



    override fun onPause() {
        super.onPause()

//        if (viewDataBinding.webView != null ) {
//            viewDataBinding.webView.onPause()
//            viewDataBinding.webView.pauseTimers()
//        }

//        AppGlobalValues.videoWebViewBundle = Bundle()
//        viewDataBinding.webView.saveState(AppGlobalValues.videoWebViewBundle)
    }

//    override fun onResume() {
//        super.onResume()
//        if (viewDataBinding.webView != null ) {
//            viewDataBinding.webView.onResume()
//            viewDataBinding.webView.resumeTimers()
//        }
//    }

    private fun expand() {
        toggle.duration = 150L
        TransitionManager.beginDelayedTransition(viewDataBinding.webView as ViewGroup, toggle)
        viewDataBinding.webView.onResume()
        viewDataBinding.webView.visibility = View.VISIBLE
        expanded = true
    }

    private fun collapse() {
        toggle.duration = 200L
        TransitionManager.beginDelayedTransition(viewDataBinding.webView as ViewGroup, toggle)
        viewDataBinding.webView.onPause()
        viewDataBinding.webView.visibility = View.GONE
        expanded = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewDataBinding.webView.saveState(outState)
        AppGlobalValues.videoWebViewBundle = outState
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //activity?.title = title

        registerToolbar(viewDataBinding.toolbar)

        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.configurationChangeCallback = this
        }

        viewDataBinding.toolbar.title = args.chapterTitle

        val webSettings = viewDataBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowContentAccess = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        //webSettings.pluginState = WebSettings.PluginState.ON_DEMAND;
        //viewDataBinding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true
        }

        webSettings.builtInZoomControls = true

//        url.let {
//            viewDataBinding.webView.loadUrl(it)
//        }

//        viewModel.historyItems.observe(viewLifecycleOwner, Observer {
//            it?.let { list ->
//                val tt = list
//            }
//        })

        viewModel.doesItemExists(args.bookID, args.chapterID).observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                if (list.isNotEmpty()) {
                    viewModel.updateToHistory(list[0].id)
                } else {
                    viewModel.addToHistory(HistoryItem(0, args.bookID, args.bookTitle, args.chapterID, args.chapterTitle, 1))
                }
            }
        })

        viewDataBinding.webView.webChromeClient = object : WebChromeClient() {
            private var mCustomView: View? = null
            private var mCustomViewCallback: CustomViewCallback? = null
            var mFullscreenContainer: FrameLayout? = null
            private var mOriginalOrientation = 0
            private var mOriginalSystemUiVisibility = 0

            override fun getDefaultVideoPoster(): Bitmap? {
                return if (mCustomView == null) {
                    null
                } else BitmapFactory.decodeResource(applicationContext.resources, 2130837573)
            }

            override fun onHideCustomView() {
                (requireActivity().window.decorView as FrameLayout).removeView(this.mCustomView)
                this.mCustomView = null
                requireActivity().window.decorView.systemUiVisibility = this.mOriginalSystemUiVisibility
                requireActivity().requestedOrientation = this.mOriginalOrientation
                this.mCustomViewCallback?.onCustomViewHidden()
                this.mCustomViewCallback = null
            }

            override fun onShowCustomView(paramView: View, paramCustomViewCallback: CustomViewCallback) {
                if (this.mCustomView != null) {
                    onHideCustomView()
                    return
                }
                this.mCustomView = paramView
                this.mOriginalSystemUiVisibility = requireActivity().window.decorView.systemUiVisibility
                this.mOriginalOrientation = requireActivity().requestedOrientation
                this.mCustomViewCallback = paramCustomViewCallback
                (requireActivity().window.decorView as FrameLayout).addView(this.mCustomView, FrameLayout.LayoutParams(-1, -1))
                requireActivity().window.decorView.systemUiVisibility = 3846
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                try {
                    viewDataBinding.progressBar.progress = newProgress
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }


        viewDataBinding.webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                //if (viewDataBinding.progressBar != null) viewDataBinding.progressBar.visibility = View.VISIBLE

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewDataBinding.progressBar.visibility = View.GONE
            }
        }

        pagerAdapter = VideoTabViewPagerAdapter(
            2,
            this
        )

        viewDataBinding.viewPager.adapter = pagerAdapter

        viewDataBinding.viewPager.offscreenPageLimit = 3
        viewDataBinding.viewPager.isUserInputEnabled = false

        viewPager2PageChangeCallback = ViewPager2PageChangeCallback {
            setCurrentPageItemPosition(it)
        }

        viewDataBinding.viewPager.registerOnPageChangeCallback(viewPager2PageChangeCallback)

        TabLayoutMediator(viewDataBinding.tabs, viewDataBinding.viewPager) { tab, position ->
            tab.text = viewPagerPageTitles[position]
            //tab.icon = ContextCompat.getDrawable(requireContext(), viewPagerPageIcons[position])
        }.attach()

        childFragmentManager.setFragmentResultListener(
            "playVideo",
            viewLifecycleOwner, FragmentResultListener { key, bundle ->
                val videoItem = bundle.getSerializable("VideoItem") as VideoItem?
                videoItem?.let {
                    lifecycleScope.launch {
                        playVideo()
                    }
                }
            }
        )

//        if (AppGlobalValues.videoWebViewBundle != null) {
//            viewDataBinding.webView.restoreState(AppGlobalValues.videoWebViewBundle)
//        } else if (!File(destination).exists() && File(source).exists()) {
//            lifecycleScope.launch {
//                unZipFile()
//            }
//        }

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                unZipFile()
            }
        } else {
            viewDataBinding.webView.restoreState(savedInstanceState)
        }



    }

    private fun unZipFile() {
        viewDataBinding.progressBar.visibility = View.VISIBLE
        try {
            val zipFile = ZipFile(File(source))
            if (zipFile.isEncrypted) {
                zipFile.setPassword(password)
            }
            val progressMonitor: ProgressMonitor = zipFile.progressMonitor

            zipFile.isRunInThread = true
            zipFile.extractAll(destination)

            while (progressMonitor.state != ProgressMonitor.State.READY) {
                viewDataBinding.progressBar.progress = progressMonitor.percentDone
                println("Percentage done: " + progressMonitor.percentDone)
                println("Current file: " + progressMonitor.fileName)
                println("Current task: " + progressMonitor.currentTask)

                //Thread.sleep(100)
            }

            when (progressMonitor.result) {
                ProgressMonitor.Result.SUCCESS -> {
                    viewDataBinding.progressBar.visibility = View.GONE
                    lifecycleScope.launch(Dispatchers.Main.immediate) {
                        playVideo()
                    }
                }
                ProgressMonitor.Result.ERROR -> {
                    viewDataBinding.progressBar.visibility = View.GONE
                    println("Error occurred. Error message: " + progressMonitor.exception.message)
                }
                ProgressMonitor.Result.CANCELLED -> {
                    viewDataBinding.progressBar.visibility = View.GONE
                    println("Task cancelled")
                }
                else -> {

                }
            }
        } catch (e: ZipException) {
            e.printStackTrace()
        }
    }

    fun playVideo() {
        if (File("$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html").exists()) {
            viewDataBinding.webView.post { viewDataBinding.webView.loadUrl("file:///$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html") }
        }
    }

    override fun onDestroyView() {
        //viewDataBinding.webView.webViewClient = null
        super.onDestroyView()
    }

    private fun setCurrentPageItemPosition(position: Int) {
        viewPagerCurrentItem = position
        if (viewPagerCurrentItem == 0) expand() else collapse()
    }

    override fun onNewConfiguration(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            restoreSystemUI()
            bottomNavShowHideCallback?.showOrHideBottomNav(true)
        } else {
            hideSystemUI()
            bottomNavShowHideCallback?.showOrHideBottomNav(false)
        }
    }

    private fun hideSystemUI() {
        //requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        viewDataBinding.toolbar.visibility = View.GONE
        // Standard Android full-screen functionality.
        systemUIConfigurationBackup = requireActivity().window.decorView.systemUiVisibility
//        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)


        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        requireActivity().window.statusBarColor = Color.TRANSPARENT



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//            requireActivity().window.statusBarColor = Color.TRANSPARENT
//
//        } else
//            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

//        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun restoreSystemUI() {
        if (viewDataBinding.toolbar != null) {
            viewDataBinding.toolbar.visibility = View.VISIBLE
        }
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.decorView.systemUiVisibility = systemUIConfigurationBackup
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

class ViewPager2PageChangeCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        listener.invoke(position)
    }
}
package com.rtchubs.engineerbooks.ui.video_play

//import ir.mahdi.mzip.zip.ZipArchive
//import net.lingala.zip4j.ZipFile
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.WebViewBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.home.SetAFragment
import com.rtchubs.engineerbooks.ui.home.SetBFragment
import com.rtchubs.engineerbooks.ui.home.SetCFragment
import com.rtchubs.engineerbooks.ui.home.VideoTabViewPagerAdapter
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.progress.ProgressMonitor
import java.io.File


class LoadWebViewFragment: BaseFragment<WebViewBinding, LoadWebViewViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_load_web_view
    override val viewModel by viewModels<LoadWebViewViewModel>{
        viewModelFactory
    }


    private val url by lazy {
        //arguments?.let { LoadWebViewFragmentArgs.fromBundle(it).url }
        "file:///android_asset/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html"
    }
    private val title by lazy {
        //arguments?.let { LoadWebViewFragmentArgs.fromBundle(it).title }
    }

    private lateinit var viewPagerFragments: Array<Fragment>
    private val viewPagerPageTitles = arrayOf("Set A", "Set B", "Set C")

    private lateinit var pagerAdapter: VideoTabViewPagerAdapter

    private var viewPagerCurrentItem = 0

    private lateinit var viewPager2PageChangeCallback: ViewPager2PageChangeCallback

    private val setAFragment: SetAFragment = SetAFragment()
    private val setBFragment: SetBFragment = SetBFragment()
    private val setCFragment: SetCFragment = SetCFragment()

    private var isVideoStartedPlaying = false

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //activity?.title = title

        registerToolbar(viewDataBinding.toolbar)

        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(requireContext(), null)
        val primaryExternalStorageAbsolutePath = externalStorageVolumes[0].absolutePath

        val webSettings = viewDataBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowContentAccess = true
        webSettings.domStorageEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON_DEMAND;
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

        val title = "math_8_4_1_q_1_ka"

        viewModel.doesItemExists(title).observe(viewLifecycleOwner, Observer {
            it?.let { exists ->
                if (exists.isNotEmpty()) {
                    viewModel.updateToHistory(title)
                } else {
                    viewModel.addToHistory(title)
                }
            }
        })

        viewDataBinding.webView.webChromeClient = object : WebChromeClient() {
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

                viewDataBinding.progressBar.visibility = View.VISIBLE

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                var isHandled = false
                try {
                    if (url != null) {
                        Log.i("LoadWebViewFragment", url)
                        when {

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return isHandled
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewDataBinding.progressBar.visibility = View.GONE
            }
        }

        viewPagerFragments = arrayOf(setAFragment, setBFragment, setCFragment)

        pagerAdapter = VideoTabViewPagerAdapter(
            viewPagerFragments,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )

        viewDataBinding.viewPager.adapter = pagerAdapter

        viewPager2PageChangeCallback = ViewPager2PageChangeCallback {
            setCurrentPageItemPosition(it)
        }

        viewDataBinding.viewPager.registerOnPageChangeCallback(viewPager2PageChangeCallback)

        TabLayoutMediator(viewDataBinding.tabs, viewDataBinding.viewPager) { tab, position ->
            tab.text = viewPagerPageTitles[position]
            //tab.icon = ContextCompat.getDrawable(requireContext(), viewPagerPageIcons[position])
        }.attach()

        lifecycleScope.launch {

            val source = "$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka.zip"
            val destination = "$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka"
            val password = "1234".toCharArray()

            if (!File(destination).exists() && File(source).exists()) {
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
                            if (File("$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html").exists()) {
                                viewDataBinding.webView.loadUrl("file:///$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html")
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
            } else if (File("$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html").exists()) {
                viewDataBinding.webView.loadUrl("file:///$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html")
            }
        }
    }

    override fun onDestroyView() {
        viewDataBinding.webView.webViewClient = null
        super.onDestroyView()
    }

    private fun setCurrentPageItemPosition(position: Int) {
        viewPagerCurrentItem = position
    }
}

class ViewPager2PageChangeCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        listener.invoke(position)
    }
}
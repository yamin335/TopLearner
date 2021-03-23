package com.rtchubs.engineerbooks.ui.video_play

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
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
import com.rtchubs.engineerbooks.api.ApiCallStatus
import com.rtchubs.engineerbooks.api.ApiEndPoint.VIDEOS
import com.rtchubs.engineerbooks.databinding.WebViewBinding
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.models.chapter.BookChapter
import com.rtchubs.engineerbooks.models.chapter.ChapterField
import com.rtchubs.engineerbooks.ui.ConfigurationChangeCallback
import com.rtchubs.engineerbooks.ui.MainActivity
import com.rtchubs.engineerbooks.ui.ShowHideBottomNavCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.common.CommonMessageBottomSheetDialog
import com.rtchubs.engineerbooks.ui.home.VideoTabViewPagerAdapter
import com.rtchubs.engineerbooks.util.AppConstants.downloadFolder
import com.rtchubs.engineerbooks.util.AppConstants.unzippedFolder
import com.rtchubs.engineerbooks.util.FLAGS_FULLSCREEN
import com.rtchubs.engineerbooks.util.FileUtils
import com.rtchubs.engineerbooks.util.showErrorToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.progress.ProgressMonitor
import java.io.File
import javax.inject.Inject

private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class LoadWebViewFragment: BaseFragment<WebViewBinding, LoadWebViewViewModel>(), ConfigurationChangeCallback {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_load_web_view
    override val viewModel by viewModels<LoadWebViewViewModel> {
        viewModelFactory
    }

    @Inject
    lateinit var applicationContext: Application

    private val url by lazy {
        //arguments?.let { LoadWebViewFragmentArgs.fromBundle(it).url }
//        "file:///android_asset/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html"
        "file:///android_asset/math_8_4_1_q_1_ka/MATH8_4.1Q1KA_player.html"
    }
    private val title by lazy {
        //arguments?.let { LoadWebViewFragmentArgs.fromBundle(it).title }
    }

    private val viewPagerPageTitles = arrayOf("অ্যানিমেশন", tab1Title, tab2Title, tab3Title)
//    private val viewPagerPageTitles = arrayOf("Video List", "Questions")

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

//    private lateinit var source: String
//    private lateinit var destination: String
    private val password = "1234".toCharArray()

    private var bottomNavShowHideCallback: ShowHideBottomNavCallback? = null

    private var systemUIConfigurationBackup: Int = 0

    //private lateinit var downloadCompleteReceiver: BroadcastReceiver

    var isUSBPluggedIn = false

    private var downloadingFile: File? = null

    lateinit var commonMessageBottomSheetDialog: CommonMessageBottomSheetDialog

    var systemUiVisibility: Int = 0

    private val usbDetectionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_POWER_CONNECTED) {
                val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                    context.registerReceiver(null, ifilter)
                }
                // How are we charging?
                val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
                if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) requireActivity().finish()
            } else if (intent.action == Intent.ACTION_POWER_DISCONNECTED) {
                isUSBPluggedIn = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        detectUSB()
        // get notified when download is complete
//        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
//            downloadCompleteReceiver,
//            IntentFilter(DOWNLOAD_COMPLETE)
//        )
    }

    private fun detectUSB() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        requireActivity().registerReceiver(usbDetectionReceiver, intentFilter)
        //Detect USB
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            requireActivity().registerReceiver(null, ifilter)
        }
        // How are we charging?
        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) isUSBPluggedIn = true
    }

    override fun onPause() {
        super.onPause()
        FileUtils.deleteFolderWithAllFilesFromExternalStorage(requireContext(), unzippedFolder)
        requireActivity().unregisterReceiver(usbDetectionReceiver)



        //LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(downloadCompleteReceiver)

//        if (viewDataBinding.webView != null ) {
//            viewDataBinding.webView.onPause()
//            viewDataBinding.webView.pauseTimers()
//        }

//        AppGlobalValues.videoWebViewBundle = Bundle()
//        viewDataBinding.webView.saveState(AppGlobalValues.videoWebViewBundle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        detectUSB()

        chapter = args.chapter

        if (context is ShowHideBottomNavCallback) {
            bottomNavShowHideCallback = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
        FileUtils.makeEmptyFolderIntoExternalStorageWithTitle(requireContext(), unzippedFolder)
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavShowHideCallback = null
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
        }

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        toggle = TransitionInflater.from(requireContext()).inflateTransition(R.transition.search_bar_toogle)
        detectUSB()
        //source = "$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka.zip"
        //destination = "$primaryExternalStorageAbsolutePath/math_8_4_1_q_1_ka"

//        downloadCompleteReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                intent?.let {
//                    val filePath = it.getStringExtra(FILE_PATH)
//                    filePath?.let { path ->
//                        val fileName = it.getStringExtra(FILE_NAME)
//                        fileName?.let { name ->
//                            try {
//                                val file = File(path, name)
//                                val fileType = it.getStringExtra(FILE_TYPE)
//                                if (fileType == typeVideo) {
//                                    if (file.exists()) {
//                                        val outputDirectoryPath = name.substring(0, name.lastIndexOf("."))
//
//                                        lifecycleScope.launch {
//                                            unZipFile(file, outputDirectoryPath)
//                                        }
//                                    }
//                                } else if (fileType == typePdf) {
//                                    childFragmentManager.setFragmentResult("loadPdf", bundleOf("pdfFilePath" to "$path/$name"))
//                                }
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
//                }
//            }
//        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewDataBinding.webView.saveState(outState)
        AppGlobalValues.videoWebViewBundle = outState
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //activity?.title = title
        detectUSB()
        registerToolbar(viewDataBinding.toolbar)
        viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)

        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.configurationChangeCallback = this
        }

        viewDataBinding.toolbar.title = chapter.name

        commonMessageBottomSheetDialog = CommonMessageBottomSheetDialog(null)

        viewModel.showHideProgress.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                childFragmentManager.setFragmentResult(
                    "showHideProgress",
                    bundleOf("progressStatus" to it)
                )
            }
        })

        viewModel.videoFileDownloadResponse.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                val file = File(it.first, it.second)
                if (file.exists()) {
                    if (isUSBPluggedIn) {
                        showErrorToast(requireContext(), "Please unplug your USB then try again!")
                    } else {
                        val temp = it.second
                        val outputDirectoryName = temp.substring(0, temp.lastIndexOf("."))
                        lifecycleScope.launch {
                            unZipFile(file, outputDirectoryName)
                        }
                    }
                }

                val downloadFilePath = FileUtils.getLocalStorageFilePath(
                    requireContext(),
                    downloadFolder
                )

                var oldestFileName = ""
                var oldestDate = Long.MAX_VALUE
                val downloadFolder = File(downloadFilePath)
                if (!downloadFolder.isDirectory) return@let
                val filesInDownloadFolder = downloadFolder.listFiles() ?: return@let

                if (filesInDownloadFolder.size <= 5) return@let
                for (zipFile in filesInDownloadFolder) {
                    if (!zipFile.isDirectory) {
                        if (zipFile.lastModified() < oldestDate) {
                            oldestDate = zipFile.lastModified()
                            oldestFileName = zipFile.name
                        }
                    }
                }
                val deleteFile = File(downloadFilePath, oldestFileName)
                if (deleteFile.exists()) FileUtils.deleteFileFromExternalStorage(deleteFile)
            }

            if (value == null && downloadingFile != null) {
                FileUtils.deleteFileFromExternalStorage(downloadingFile!!)
                commonMessageBottomSheetDialog.message =
                    "এই অ্যানিমেশনটির ক্লাস এখনও হয়নি, ক্লাস শেষে একটিভ হবে। চেক করার জন্য আপনাকে অসংখ্য ধন্যবাদ।"
                commonMessageBottomSheetDialog.show(childFragmentManager, "#Common_Message_Dialog")
                //showErrorToast(requireContext(), "This video is not available now")
            }

            viewModel.showHideProgress.postValue(false)
        })

        //viewDataBinding.webView.setInitialScale(1)

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

        viewModel.doesItemExists(chapter.book_id ?: 0, chapter.id ?: 0).observe(
            viewLifecycleOwner,
            Observer {
                it?.let { list ->
                    if (list.isNotEmpty()) {
                        viewModel.updateToHistory(list[0].id)
                    } else {
                        viewModel.addToHistory(
                            HistoryItem(
                                0,
                                chapter.book_id ?: 0,
                                chapter.id ?: 0,
                                chapter.name,
                                chapter,
                                1
                            )
                        )
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

            override fun onShowCustomView(
                paramView: View,
                paramCustomViewCallback: CustomViewCallback
            ) {
                if (this.mCustomView != null) {
                    onHideCustomView()
                    return
                }
                this.mCustomView = paramView
                this.mOriginalSystemUiVisibility = requireActivity().window.decorView.systemUiVisibility
                this.mOriginalOrientation = requireActivity().requestedOrientation
                this.mCustomViewCallback = paramCustomViewCallback
                (requireActivity().window.decorView as FrameLayout).addView(
                    this.mCustomView, FrameLayout.LayoutParams(
                        -1,
                        -1
                    )
                )
                requireActivity().window.decorView.systemUiVisibility = 3846
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                try {
                    //viewDataBinding.progressBar?.progress = newProgress
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }


        viewDataBinding.webView.webViewClient = object : WebViewClient() {



            override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
                return super.shouldOverrideKeyEvent(view, event)
                var tt = "S"
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                //if (viewDataBinding.progressBar != null) viewDataBinding.progressBar.visibility = View.VISIBLE

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                //viewDataBinding.progressBar.visibility = View.GONE
            }
        }

        pagerAdapter = VideoTabViewPagerAdapter(
            4,
            this
        )

        viewDataBinding.viewPager.adapter = pagerAdapter

        viewDataBinding.viewPager.offscreenPageLimit = 3
        viewDataBinding.viewPager.isUserInputEnabled = false

        viewPager2PageChangeCallback = ViewPager2PageChangeCallback {
            setCurrentPageItemPosition(it)
            when(it) {
                0 -> requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                1 -> requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                2 -> requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                3 -> requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        viewDataBinding.viewPager.registerOnPageChangeCallback(viewPager2PageChangeCallback)

        TabLayoutMediator(viewDataBinding.tabs, viewDataBinding.viewPager) { tab, position ->
            tab.text = viewPagerPageTitles[position]
            //tab.icon = ContextCompat.getDrawable(requireContext(), viewPagerPageIcons[position])
        }.attach()

        childFragmentManager.setFragmentResultListener(
            "playVideo",
            viewLifecycleOwner, FragmentResultListener { key, bundle ->
                val videoItem = bundle.getSerializable("VideoItem") as ChapterField?
                videoItem?.let {
                    try {
                        val filepath = FileUtils.getLocalStorageFilePath(
                            requireContext(),
                            downloadFolder
                        )
                        val unzipFilepath = FileUtils.getLocalStorageFilePath(
                            requireContext(),
                            unzippedFolder
                        )
                        val fileName = videoItem.video_filename
                        if (fileName.isNullOrBlank()) {
                            commonMessageBottomSheetDialog.message =
                                "এই অ্যানিমেশনটির ক্লাস এখনও হয়নি, ক্লাস শেষে একটিভ হবে। চেক করার জন্য আপনাকে অসংখ্য ধন্যবাদ।"
                            commonMessageBottomSheetDialog.show(
                                childFragmentManager,
                                "#Common_Message_Dialog"
                            )
                            //showWarningToast(requireContext(), "This video is not available now")
                            return@FragmentResultListener
                        }
                        val videoFile = File(filepath, fileName)
                        if (videoFile.exists()) {
                            val videoFolderName = fileName.substring(0, fileName.lastIndexOf("."))

                            val videoFolder = File(unzipFilepath, videoFolderName)
                            if (videoFolder.exists() && videoFolder.isDirectory) {
                                lifecycleScope.launch {
                                    playVideo("$unzipFilepath/$videoFolderName")
                                }
                            } else {
                                lifecycleScope.launch {
                                    unZipFile(File(filepath, fileName), videoFolderName)
                                }
                            }
                        } else {
                            val downloadUrl = "$VIDEOS/$fileName"
                            viewModel.downloadVideoFile(downloadUrl, filepath, fileName)
                            downloadingFile = File(filepath, fileName)
                        }
//                    lifecycleScope.launch {
//                        playVideo()
//                    }
                    } catch (e: Exception) {
                        e.printStackTrace()
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

        viewModel.pdfFileDownloadResponse.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                childFragmentManager.setFragmentResult(
                    "loadPdf",
                    bundleOf("pdfFilePath" to "${it.first}/${it.second}")
                )
                viewModel.filesInDownloadPool.remove(it.second)
            }
        })

        viewModel.solutionPdfFileDownloadResponse.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                childFragmentManager.setFragmentResult(
                    "loadSolutionPdf",
                    bundleOf("solutionPdfFilePath" to "${it.first}/${it.second}")
                )
                viewModel.filesInDownloadPool.remove(it.second)
            }
        })

        if (savedInstanceState == null) {
            //viewDataBinding.webView.loadUrl(url)
            if (isUSBPluggedIn) {
                showErrorToast(requireContext(), "Please unplug your USB then try again!")
            } else {
//                if (!chapter.pdf.isNullOrBlank()) {
//                    val filepath = FileUtils.getLocalStorageFilePath(
//                        requireContext(),
//                        unzippedFolder
//                    )
//                    Tab2Fragment.pdfFilePath = "$filepath/${chapter.pdf}"
//
//                    if (!File(Tab2Fragment.pdfFilePath).exists() && !viewModel.filesInDownloadPool.contains(
//                            chapter.pdf!!
//                        )) {
//                        viewModel.filesInDownloadPool.add(chapter.pdf!!)
//                        viewModel.downloadPdfFile("$PDF/${chapter.pdf}", filepath, chapter.pdf!!)
//                        //downloadFile("$PDF/${chapter.pdf}", filepath, chapter.pdf!!, typePdf)
//                    }
//                } else {
//                    childFragmentManager.setFragmentResult(
//                        "loadPdf",
//                        bundleOf("pdfFilePath" to "")
//                    )
//                }

//                if (!chapter.somadhan.isNullOrBlank()) {
//                    val filepath = FileUtils.getLocalStorageFilePath(
//                        requireContext(),
//                        unzippedFolder
//                    )
//                    Tab1Fragment.solutionPdfFilePath = "$filepath/${chapter.somadhan}"
//
//                    if (!File(Tab1Fragment.solutionPdfFilePath).exists() && !viewModel.filesInDownloadPool.contains(
//                            chapter.somadhan!!
//                        )) {
//                        viewModel.filesInDownloadPool.add(chapter.somadhan!!)
//                        viewModel.downloadSolutionPdfFile(
//                            "$SOMADHAN/${chapter.somadhan}",
//                            filepath,
//                            chapter.somadhan!!
//                        )
//                        //downloadFile("$PDF/${chapter.pdf}", filepath, chapter.pdf!!, typePdf)
//                    }
//                } else {
//                    childFragmentManager.setFragmentResult(
//                        "loadSolutionPdf",
//                        bundleOf("solutionPdfFilePath" to "")
//                    )
//                }

                chapter.fields?.let { videoList ->
                    if (videoList.isNotEmpty()) {
                        try {
                            val video = videoList[0]
                            val filepath = FileUtils.getLocalStorageFilePath(
                                requireContext(),
                                downloadFolder
                            )
                            val unzipFilepath = FileUtils.getLocalStorageFilePath(
                                requireContext(),
                                unzippedFolder
                            )
                            val fileName = video.video_filename ?: ""
                            val videoFile = File(filepath, fileName)
                            if (videoFile.exists()) {
                                val videoFolderName = fileName.substring(
                                    0,
                                    fileName.lastIndexOf(".")
                                )
                                val videoFolder = File(unzipFilepath, videoFolderName)
                                if (videoFolder.exists() && videoFolder.isDirectory) {
                                    lifecycleScope.launch {
                                        playVideo("$unzipFilepath/$videoFolderName")
                                    }
                                } else {
                                    lifecycleScope.launch {
                                        unZipFile(File(filepath, fileName), videoFolderName)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            viewDataBinding.webView.restoreState(savedInstanceState)
        }
    }

//    private fun downloadFile(downloadUrl: String, filePath: String, fileName: String, fileType: String) {
//        val intent = Intent(requireContext(), DownloadService::class.java)
//        intent.putExtra(AppConstants.DOWNLOAD_URL, downloadUrl)
//        intent.putExtra(FILE_PATH, filePath)
//        intent.putExtra(FILE_NAME, fileName)
//        intent.putExtra(FILE_TYPE, fileType)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            requireContext().startForegroundService(intent)
//        } else {
//            requireContext().startService(intent)
//        }
//    }

    private fun unZipFile(inputFile: File, outputFolderName: String) {
        if (isUSBPluggedIn) {
            showErrorToast(requireContext(), "Please unplug your USB then try again!")
        } else {
            viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
            try {
                var outputFilePath = FileUtils.getLocalStorageFilePath(
                    requireContext(),
                    unzippedFolder
                )
                outputFilePath = "$outputFilePath/$outputFolderName"
                val zipFile = ZipFile(inputFile)
                if (zipFile.isEncrypted) {
                    zipFile.setPassword(password)
                }
                val progressMonitor: ProgressMonitor = zipFile.progressMonitor

                zipFile.isRunInThread = true
                zipFile.extractAll(outputFilePath)

                while (progressMonitor.state != ProgressMonitor.State.READY) {
                    //viewDataBinding.progressBar.progress = progressMonitor.percentDone
                    println("Percentage done: " + progressMonitor.percentDone)
                    println("Current file: " + progressMonitor.fileName)
                    println("Current task: " + progressMonitor.currentTask)

                    //Thread.sleep(100)
                }

                when (progressMonitor.result) {
                    ProgressMonitor.Result.SUCCESS -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        lifecycleScope.launch(Dispatchers.Main.immediate) {
                            playVideo(outputFilePath)
                        }
                    }
                    ProgressMonitor.Result.ERROR -> {
                        FileUtils.deleteFileFromExternalStorage(inputFile)
                        viewModel.showHideProgress.postValue(false)
                        viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                        commonMessageBottomSheetDialog.message =
                            "এই অ্যানিমেশনটির ক্লাস এখনও হয়নি, ক্লাস শেষে একটিভ হবে। চেক করার জন্য আপনাকে অসংখ্য ধন্যবাদ।"
                        commonMessageBottomSheetDialog.show(
                            childFragmentManager,
                            "#Common_Message_Dialog"
                        )

                        //showErrorToast(requireContext(), "This video is not available now!")
                    }
                    ProgressMonitor.Result.CANCELLED -> {
                        FileUtils.deleteFileFromExternalStorage(inputFile)
                        viewModel.showHideProgress.postValue(false)
                        viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                        commonMessageBottomSheetDialog.message =
                            "এই অ্যানিমেশনটির ক্লাস এখনও হয়নি, ক্লাস শেষে একটিভ হবে। চেক করার জন্য আপনাকে অসংখ্য ধন্যবাদ।"
                        commonMessageBottomSheetDialog.show(
                            childFragmentManager,
                            "#Common_Message_Dialog"
                        )

                        //showErrorToast(requireContext(), "This video is not available now!")
                    }
                    else -> {
                        FileUtils.deleteFileFromExternalStorage(inputFile)
                        viewModel.showHideProgress.postValue(false)
                        viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                        commonMessageBottomSheetDialog.message = "এই অ্যানিমেশনটির ক্লাস এখনও হয়নি, ক্লাস শেষে একটিভ হবে। চেক করার জন্য আপনাকে অসংখ্য ধন্যবাদ।"
                        commonMessageBottomSheetDialog.show(
                            childFragmentManager,
                            "#Common_Message_Dialog"
                        )

                        //showErrorToast(requireContext(), "This video is not available now!")
                    }
                }
            } catch (e: ZipException) {
                e.printStackTrace()
                FileUtils.deleteFileFromExternalStorage(inputFile)
                viewModel.showHideProgress.postValue(false)
                viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                commonMessageBottomSheetDialog.message = "এই অ্যানিমেশনটির ক্লাস এখনও হয়নি, ক্লাস শেষে একটিভ হবে। চেক করার জন্য আপনাকে অসংখ্য ধন্যবাদ।"
                commonMessageBottomSheetDialog.show(childFragmentManager, "#Common_Message_Dialog")

                //showErrorToast(requireContext(), "This video is not available now!")
            }
        }
    }

    fun playVideo(videoFolderPath: String) {
        if (isUSBPluggedIn) {
            showErrorToast(requireContext(), "Please unplug your USB then try again!")
        } else {
            val middleFolderList = ArrayList<String>() //ArrayList cause you don't know how many files there is

            val folder = File(videoFolderPath) //This is just to cast to a File type since you pass it as a String

            val filesInFolder = folder.listFiles() ?: return // This returns all the folders and files in your path

            for (file in filesInFolder) { //For each of the entries do:
                if (file.isDirectory) { //check that it's a dir
                    middleFolderList.add(file.name) //push the folder name as a string
                }
            }

            if (middleFolderList.isEmpty()) return
            val middleFolderName = middleFolderList.first()
            val middleFolderPath = "$videoFolderPath/$middleFolderName"

            val middleFolderFileList = ArrayList<String>()
            val middleFolder = File(middleFolderPath)
            val filesInMiddleFolder = middleFolder.listFiles() ?: return

            for (file in filesInMiddleFolder) {
                if (!file.isDirectory) {
                    middleFolderFileList.add(file.name)
                }
            }

            var videoFileName = ""
            middleFolderFileList.forEach {
                if (it.contains("player")) videoFileName = it
            }

            if (File("$videoFolderPath/$middleFolderName/$videoFileName").exists()) {
                viewDataBinding.webView.post { viewDataBinding.webView.loadUrl("file:///$videoFolderPath/$middleFolderName/$videoFileName") }
            }

//            val path = Paths.get("$videoFolderPath/")
//            val innerFolderName: String
//            var playFileName = ""
//
//
//            //List all items in the directory. Note that we are using Java 8 streaming API to group the entries by
//            //directory and files
//            val fileDirMap1 = Files.list(path).collect(Collectors.partitioningBy({ it ->
//                Files.isDirectory(
//                    it
//                )
//            }))
//
//            innerFolderName = fileDirMap1[true]?.first().toString()
//            println("Directories")
//
//
//            val newPath = Paths.get(innerFolderName)
//
//            //List all items in the directory. Note that we are using Java 8 streaming API to group the entries by
//            //directory and files
//            val fileDirMap = Files.list(newPath).collect(Collectors.partitioningBy({ it ->
//                Files.isDirectory(
//                    it
//                )
//            }))
//
//            fileDirMap[false]?.forEach({ it ->
////                println("%-20s\t%-5b\t%-5b\t%b".format(
////                    it.fileName,
////                    Files.isReadable(it), //Read attribute
////                    Files.isWritable(it), //Write attribute
////                    Files.isExecutable(it))) //Execute attribute
//
//                if (it.fileName.toString().contains("player")) {
//                    playFileName = it.fileName.toString()
//                }
//
//            })
//
//
//            if (File("$innerFolderName/$playFileName").exists()) {
//                viewDataBinding.webView.post { viewDataBinding.webView.loadUrl("file:///$innerFolderName/$playFileName") }
//            }
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
            viewDataBinding.webView.evaluateJavascript("javascript:disableFullScreen()") {
                Log.d("JavaScriptReturnValue:", it)
            }
            restoreSystemUI()
            bottomNavShowHideCallback?.showOrHideBottomNav(true)
        } else {
            viewDataBinding.webView.evaluateJavascript("javascript:enableFullScreen()") {
                Log.d("JavaScriptReturnValue:", it)
            }
            hideSystemUI()
            bottomNavShowHideCallback?.showOrHideBottomNav(false)
        }
    }

    private fun hideSystemUI() {
        //requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        try {
            viewDataBinding.toolbar.visibility = View.GONE
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

        // Standard Android full-screen functionality.

//        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)


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
        try {
            if (viewDataBinding.toolbar != null) {
                viewDataBinding.toolbar.visibility = View.VISIBLE
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            requireActivity().window.decorView.systemUiVisibility = systemUIConfigurationBackup
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            viewDataBinding.mainContainer.postDelayed({
                viewDataBinding.mainContainer.systemUiVisibility = systemUiVisibility
            }, IMMERSIVE_FLAG_TIMEOUT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        lateinit var chapter: BookChapter
        var tab1Title = ""
        var tab2Title = ""
        var tab3Title = ""
    }
}

class ViewPager2PageChangeCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        listener.invoke(position)
    }
}
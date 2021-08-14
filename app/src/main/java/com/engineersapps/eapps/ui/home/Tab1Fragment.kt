package com.engineersapps.eapps.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.ChapterDetailsTabFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.video_play.LoadWebViewFragment
import com.engineersapps.eapps.util.AppConstants
import com.engineersapps.eapps.util.FileUtils
import com.github.barteksc.pdfviewer.util.FitPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class Tab1Fragment : BaseFragment<ChapterDetailsTabFragmentBinding, Tab1ViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_chapter_details_cmn_tab

    override val viewModel: Tab1ViewModel by viewModels { viewModelFactory }

    lateinit var pdfFileReceiver: BroadcastReceiver

    private var pdfUrl = ""

    override fun onResume() {
        super.onResume()
        if (pdfFilePath1.isNotBlank()) loadPDF(File(pdfFilePath1))
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(pdfFileReceiver,
            IntentFilter(TAG))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(pdfFileReceiver)
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pdfFileReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action != null && intent.action == TAG) {
                    if (pdfFilePath1.isNotBlank()) loadPDF(File(pdfFilePath1))
                }
            }
        }

        val webSettings = viewDataBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowContentAccess = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = true

        viewDataBinding.webView.webChromeClient = WebChromeClient()
        viewDataBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try  {
                    viewDataBinding.loader.visibility = View.VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                try  {
                    viewDataBinding.loader.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                viewDataBinding.webView.visibility = View.GONE
                viewDataBinding.loader.visibility = View.GONE
                viewDataBinding.emptyView.visibility = View.VISIBLE
            }
        }

        try {
            val chapterFields = LoadWebViewFragment.chapter.fields?.filter { it.type == "Tab1" }
            if (chapterFields.isNullOrEmpty()) {
                viewDataBinding.emptyView.visibility = View.VISIBLE
                viewDataBinding.loader.visibility = View.GONE
                return
            }
            val data = chapterFields.first().link

            if (data.isNullOrBlank() || data == "null") {
                viewDataBinding.emptyView.visibility = View.VISIBLE
                viewDataBinding.loader.visibility = View.GONE
            } else {
                viewDataBinding.emptyView.visibility = View.GONE
                if (data.contains(".pdf", true)) {
                    pdfUrl = data
                    viewDataBinding.nestedScrollableHost.visibility = View.VISIBLE
                    viewDataBinding.webView.visibility = View.GONE
                    val filepath = FileUtils.getLocalStorageFilePath(
                        requireContext(),
                        AppConstants.downloadedPdfFiles
                    )
                    val fileName = data.split("/").last()
                    val path = "$filepath/$fileName"

                    if (File(path).exists()) {
                        pdfFilePath1 = path
                        if (pdfFilePath1.isNotBlank()) loadPDF(File(pdfFilePath1))
                    } else {
                        setFragmentResult("downloadPDF", bundleOf("fragment" to TAG, "url" to pdfUrl))
                    }
                } else {
                    viewDataBinding.nestedScrollableHost.visibility = View.GONE
                    viewDataBinding.webView.visibility = View.VISIBLE
                    viewDataBinding.webView.loadUrl(data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            viewDataBinding.emptyView.visibility = View.VISIBLE
            viewDataBinding.loader.visibility = View.GONE
        }
    }

    private fun loadPDF(file: File) {
        if (file.exists()) {
            try {
                lifecycleScope.launch(Dispatchers.Main.immediate) {
                    try {
                        viewDataBinding.pdfView.fromFile(file)
                            .pageFitPolicy(FitPolicy.WIDTH)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .onError {
                                setFragmentResult("downloadPDF", bundleOf("fragment" to TAG, "url" to pdfUrl))
                            }
                            .load()
                        viewDataBinding.loader.visibility = View.GONE
                        viewDataBinding.emptyView.visibility = View.GONE
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var pdfFilePath1 = ""
        const val TAG = "Tab1Fragment"
    }

    override fun onDetach() {
        super.onDetach()
        pdfFilePath1 = ""
    }
}
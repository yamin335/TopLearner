package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.SolutionCFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment

class Tab1Fragment : BaseFragment<SolutionCFragmentBinding, Tab1ViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_solution

    override val viewModel: Tab1ViewModel by viewModels { viewModelFactory }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webSettings = viewDataBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowContentAccess = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = true

        viewDataBinding.webView.webChromeClient = WebChromeClient()
        viewDataBinding.webView.webViewClient = WebViewClient()


        try {
            val chapterFields = LoadWebViewFragment.chapter.fields?.filter { it.type == "Tab1" }
            val solution = chapterFields?.first()?.link

            viewDataBinding.webView.loadUrl(solution)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    private fun loadPDF(file: File) {
//        if (file.exists()) {
//            lifecycleScope.launch(Dispatchers.Main.immediate) {
//                viewDataBinding.pdfView.fromFile(file)
//                    .pageFitPolicy(FitPolicy.WIDTH)
//                    .enableSwipe(true)
//                    .swipeHorizontal(false)
//                    .load()
//                viewDataBinding.loader.visibility = View.GONE
//                viewDataBinding.emptyView.visibility = View.GONE
//            }
//        }
//    }

//    companion object {
//        var solutionPdfFilePath = ""
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        solutionPdfFilePath = ""
//    }
}
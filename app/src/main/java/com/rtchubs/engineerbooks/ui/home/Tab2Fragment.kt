package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.SetCFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment

class Tab2Fragment : BaseFragment<SetCFragmentBinding, Tab2ViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_set_c

    override val viewModel: Tab2ViewModel by viewModels { viewModelFactory }

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
            val chapterFields = LoadWebViewFragment.chapter.fields?.filter { it.type == "Tab2" }
            val solution = chapterFields?.first()?.link

            viewDataBinding.webView.loadUrl(solution)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        parentFragmentManager.setFragmentResultListener(
//            "loadPdf",
//            viewLifecycleOwner, FragmentResultListener { key, bundle ->
//                pdfFilePath = bundle.getString("pdfFilePath") ?: ""
//
//                if (pdfFilePath == "") {
//                    viewDataBinding.loader.visibility = View.GONE
//                } else {
//                    loadPDF(File(pdfFilePath))
//                }
//            }
//        )
//
//        loadPDF(File(pdfFilePath))
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
//            }
//        }
//    }

//    companion object {
//        var pdfFilePath = ""
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        pdfFilePath = ""
//    }
}
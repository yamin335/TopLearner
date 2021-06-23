package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.QuizListFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class Tab3Fragment : BaseFragment<QuizListFragmentBinding, Tab3ViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_quiz_list

    override val viewModel: Tab3ViewModel by viewModels { viewModelFactory }

    //lateinit var quizListAdapter: QuizListAdapter

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "loadPdf",
            viewLifecycleOwner, FragmentResultListener { key, bundle ->
                if (bundle.getString("fragmeent") == TAG) {
                    pdfFilePath = bundle.getString("pdfFilePath") ?: ""

                    if (pdfFilePath == "") {
                        viewDataBinding.loader.visibility = View.GONE
                    } else {
                        loadPDF(File(pdfFilePath))
                    }
                }
            }
        )

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
                viewDataBinding.loader.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewDataBinding.loader.visibility = View.GONE
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
            val chapterFields = LoadWebViewFragment.chapter.fields?.filter { it.type == "Tab3" }
            val solution = chapterFields?.first()?.link

            if (solution.isNullOrBlank() || solution == "null") {
                viewDataBinding.emptyView.visibility = View.VISIBLE
            } else {
                viewDataBinding.emptyView.visibility = View.GONE
                if (solution.contains(".pdf", true)) {
                    loadPDF(File(pdfFilePath))
                    setFragmentResult("downloadPDF", bundleOf("fragment" to TAG, "name" to solution))
                } else {
                    viewDataBinding.webView.loadUrl(solution)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        quizListAdapter = QuizListAdapter(
//            appExecutors
//        ) { item ->
//
//        }
//
//        viewDataBinding.rvQuizList.adapter = quizListAdapter
//
//        val list = ArrayList<QuizItem>()
//
//        var i = 1
//        while (i <= 10) {
//            var j = 1
//            val answerList = ArrayList<QuizAnswerItem>()
//            val answers = arrayOf("England", "America", "Bangladesh", "South Africa", "Germany", "Australia", "India")
//            var serial = "a."
//            while (j <= 5) {
//                answerList.add(QuizAnswerItem(j, serial, answers[j], answers[j] == "Bangladesh"))
//                when(serial) {
//                    "a." -> serial = "b."
//                    "b." -> serial = "c."
//                    "c." -> serial = "d."
//                    "d." -> serial = "e."
//                    "e." -> serial = "f."
//                    "f." -> serial = "g."
//                }
//                j++
//            }
//            list.add(
//                QuizItem(i, "$i.", "Wich country fought for their mother language in 1952.", answerList)
//            )
//            i++
//        }
//
//        quizListAdapter.submitList(list)
//
//        viewDataBinding.btnSubmit.setOnClickListener {
//
//        }
    }

    private fun loadPDF(file: File) {
        if (file.exists()) {
            lifecycleScope.launch(Dispatchers.Main.immediate) {
                viewDataBinding.pdfView.fromFile(file)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .load()
                viewDataBinding.loader.visibility = View.GONE
                viewDataBinding.emptyView.visibility = View.GONE
            }
        }
    }

    companion object {
        private var pdfFilePath = ""
        private const val TAG = "Tab3Fragment"
    }

    override fun onDetach() {
        super.onDetach()
        pdfFilePath = ""
    }
}
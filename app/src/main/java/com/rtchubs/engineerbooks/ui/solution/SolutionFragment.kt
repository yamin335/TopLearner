package com.rtchubs.engineerbooks.ui.solution

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.SolutionCFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment

class SolutionFragment : BaseFragment<SolutionCFragmentBinding, SolutionViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_solution

    override val viewModel: SolutionViewModel by viewModels { viewModelFactory }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webSettings = viewDataBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowContentAccess = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)

        try {
            val chapterFields = LoadWebViewFragment.chapter.fields?.filter { it.type == "Shomadhan" }
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
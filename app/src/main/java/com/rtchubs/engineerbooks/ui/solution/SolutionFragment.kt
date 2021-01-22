package com.rtchubs.engineerbooks.ui.solution

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.scroll.ScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint
import com.rtchubs.engineerbooks.databinding.SetCFragmentBinding
import com.rtchubs.engineerbooks.databinding.SolutionCFragmentBinding
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.models.chapter.ChapterField
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.AppConstants
import com.rtchubs.engineerbooks.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SolutionFragment : BaseFragment<SolutionCFragmentBinding, SolutionViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_solution

    override val viewModel: SolutionViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "loadPdf",
            viewLifecycleOwner, FragmentResultListener { key, bundle ->
                pdfFilePath = bundle.getString("pdfFilePath") ?: ""

                if (pdfFilePath == "") {
                    viewDataBinding.loader.visibility = View.GONE
                } else {
                    loadPDF(File(pdfFilePath))
                }
            }
        )

        loadPDF(File(pdfFilePath))
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
            }
        }
    }

    companion object {
        var pdfFilePath = ""
    }

    override fun onDetach() {
        super.onDetach()
        pdfFilePath = ""
    }
}
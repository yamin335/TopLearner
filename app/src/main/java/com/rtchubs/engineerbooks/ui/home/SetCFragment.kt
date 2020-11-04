package com.rtchubs.engineerbooks.ui.home

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.scroll.ScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.SetCFragmentBinding
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SetCFragment : BaseFragment<SetCFragmentBinding, SetCViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_set_c

    override val viewModel: SetCViewModel by viewModels { viewModelFactory }

    val source : String get() {
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(requireContext(), null)
        val primaryExternalStorageAbsolutePath = externalStorageVolumes[0].absolutePath

        return "$primaryExternalStorageAbsolutePath/Test.pdf"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch(Dispatchers.Main.immediate) {
            loadPDF()
        }
    }

    private fun loadPDF() {
        if (File(source).exists()) {
            viewDataBinding.pdfView.fromFile(File(source))
                .pageFitPolicy(FitPolicy.WIDTH)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .load()
        }
    }
}
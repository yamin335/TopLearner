package com.rtchubs.engineerbooks.ui.solution

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.SolutionCFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment
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
            "loadSolutionPdf",
            viewLifecycleOwner, FragmentResultListener { key, bundle ->
                solutionPdfFilePath = bundle.getString("solutionPdfFilePath") ?: ""

                if (solutionPdfFilePath == "") {
                    viewDataBinding.loader.visibility = View.GONE
                    viewDataBinding.emptyView.visibility = View.VISIBLE
                } else {
                    loadPDF(File(solutionPdfFilePath))
                }
            }
        )

        loadPDF(File(solutionPdfFilePath))
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
        var solutionPdfFilePath = ""
    }

    override fun onDetach() {
        super.onDetach()
        solutionPdfFilePath = ""
    }
}
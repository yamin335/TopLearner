package com.rtchubs.engineerbooks.ui.chapter_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.ChapterListFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class ChapterListFragment : BaseFragment<ChapterListFragmentBinding, ChapterListViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_chapter_list
    override val viewModel: ChapterListViewModel by viewModels { viewModelFactory }

    lateinit var chapterListAdapter: ChapterListAdapter

    val args: ChapterListFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)

        viewDataBinding.toolbar.title = args.book.title

        chapterListAdapter = ChapterListAdapter(appExecutors) { chapter ->
            navController.navigate(
                //ChapterListFragmentDirections.actionChapterListToVideoPlay("vedio_file")
                ChapterListFragmentDirections.actionChapterListToWebView(chapter)
            )
        }

        viewDataBinding.rvChapterList.adapter = chapterListAdapter

        viewModel.chapterList.observe(viewLifecycleOwner, Observer {
            it?.let { chapters ->
                chapterListAdapter.submitList(chapters)
            }
        })

        viewModel.getChapterList(args.book.udid)
    }
}
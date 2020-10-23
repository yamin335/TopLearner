package com.rtchubs.engineerbooks.ui.chapter_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.FragmentChapterListBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class ChapterListFragment : BaseFragment<FragmentChapterListBinding, ChapterListViewModel>() {
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
                ChapterListFragmentDirections.actionChapterListToWebView(args.book.id, args.book.title, chapter.id, chapter.title)
            )
        }

        viewDataBinding.rvChapterList.adapter = chapterListAdapter
        chapterListAdapter.submitList(args.book.chapters)
    }
}
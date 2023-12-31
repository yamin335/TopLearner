package com.engineersapps.eapps.ui.chapter_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.ChapterListFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.video_play.LoadWebViewFragment

class ChapterListFragment : BaseFragment<ChapterListFragmentBinding, ChapterListViewModel>() {

    companion object {
        var bookID = ""
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_chapter_list
    override val viewModel: ChapterListViewModel by viewModels { viewModelFactory }

    lateinit var chapterListAdapter: ChapterListAdapter

    val args: ChapterListFragmentArgs by navArgs()

    override fun onResume() {
        super.onResume()
        viewModel.getChapterList(args.id)
    }

//    override fun onPause() {
//        super.onPause()
//        viewModel.deleteAChapterFromDB(args.id.toString())
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)



        bookID = args.id.toString()

        viewDataBinding.toolbar.title = args.title

        chapterListAdapter = ChapterListAdapter(appExecutors) { chapter ->
            try {
                LoadWebViewFragment.chapter = chapter
                val animationList = chapter.fields?.filter { it.type == "video" }
                LoadWebViewFragment.isAnimationListEmpty = animationList.isNullOrEmpty()
                LoadWebViewFragment.totalTabs = if (LoadWebViewFragment.isAnimationListEmpty) 3 else 4

                LoadWebViewFragment.tab1Title = chapter.fields?.first { it.type == "Tab1" }?.name ?: ""
                LoadWebViewFragment.tab2Title = chapter.fields?.first { it.type == "Tab2" }?.name ?: ""
                LoadWebViewFragment.tab3Title = chapter.fields?.first { it.type == "Tab3" }?.name ?: ""
                LoadWebViewFragment.tab4Title = chapter.fields?.first { it.type == "Tab4" }?.name ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
            navController.navigate(
                //ChapterListFragmentDirections.actionChapterListToVideoPlay("vedio_file")
                ChapterListFragmentDirections.actionChapterListToWebView(chapter)
            )
        }

        viewDataBinding.rvChapterList.adapter = chapterListAdapter

        viewModel.chapterListFromDB.observe(viewLifecycleOwner, Observer { chapters ->
            chapters?.let {
                if (it.chapters.isNullOrEmpty()) {
                    chapterListAdapter.submitList(it.chapters)
                    viewDataBinding.emptyView.visibility = View.VISIBLE
                } else {
                    chapterListAdapter.submitList(it.chapters)
                    viewDataBinding.emptyView.visibility = View.GONE
                }
            }
        })

        viewModel.chapterList.observe(viewLifecycleOwner, Observer { chapters ->
            viewModel.saveBookChaptersInDB(args.id.toString(), chapters ?: ArrayList())
        })
    }
}
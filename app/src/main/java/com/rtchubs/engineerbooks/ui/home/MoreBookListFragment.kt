package com.rtchubs.engineerbooks.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.LayoutAddPaymentMethodBinding
import com.rtchubs.engineerbooks.databinding.MoreBookListFragmentBinding
import com.rtchubs.engineerbooks.models.Book
import com.rtchubs.engineerbooks.models.Chapter
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.GridRecyclerItemDecorator

class MoreBookListFragment :
    BaseFragment<MoreBookListFragmentBinding, MoreBookListViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_more_book_list
    override val viewModel: MoreBookListViewModel by viewModels {
        viewModelFactory
    }

    lateinit var moreBookListAdapter: MoreBookListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)

        moreBookListAdapter = MoreBookListAdapter(
                appExecutors
            ) { item ->
            //navController.navigate(MoreBookListFragmentDirections.actionMoreBookListFragmentToChapterListFragment(item))
        }

        viewDataBinding.rvMoreBookList.addItemDecoration(GridRecyclerItemDecorator(2, 40, true))
        viewDataBinding.rvMoreBookList.layoutManager = GridLayoutManager(mContext, 2)
        viewDataBinding.rvMoreBookList.adapter = moreBookListAdapter

        val chapterList = listOf(
            Chapter(1, "Chapter One", null, null),
            Chapter(2, "Chapter Two", null, null),
            Chapter(3, "Chapter Three", null, null),
            Chapter(4, "Chapter Four", null, null),
            Chapter(5, "Chapter Five", null, null),
            Chapter(6, "Chapter Six", null, null),
            Chapter(7, "Chapter Seven", null, null),
            Chapter(8, "Chapter Eight", null, null),
            Chapter(9, "Chapter Nine", null, null),
            Chapter(10, "Chapter Ten", null, null)
        )

        val list = ArrayList<Book>()
        list.add(Book(1, "Bangla", "", chapterList))
        list.add(Book(2, "English", "",chapterList))
        list.add(Book(3, "General Mathematics", "", chapterList))
        list.add(Book(4, "Biology", "", chapterList))
        list.add(Book(5, "Physics", "", chapterList))
        list.add(Book(6, "Information Technology", "", chapterList))
        list.add(Book(7, "Bangladesh & Global Studies", "", chapterList))
        list.add(Book(8, "Chemistry", "", chapterList))
        list.add(Book(9, "Higher Mathematics", "", chapterList))
        list.add(Book(10, "Islam & Moral Education", "", chapterList))
        list.add(Book(11, "Agriculture", "", chapterList))
        list.add(Book(12, "Electrical", "", chapterList))
        list.add(Book(13, "Mechanical", "", chapterList))
        list.add(Book(14, "Computer", "", chapterList))
        list.add(Book(15, "General Knowledge", "", chapterList))
        list.add(Book(16, "Arabic", "", chapterList))
        list.add(Book(17, "Bangla Grammar", "", chapterList))
        list.add(Book(18, "English Grammar", "", chapterList))
        list.add(Book(19, "Arabic Grammar", "", chapterList))
        list.add(Book(20, "History", "", chapterList))
        moreBookListAdapter.submitList(list)
    }
}
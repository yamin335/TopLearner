package com.engineersapps.eapps.ui.my_course

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.BooksFragmentBinding
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.free_book.FreeBookListAdapter

class BooksFragment : BaseFragment<BooksFragmentBinding, BooksViewModel>() {

    companion object {
        var books: List<ClassWiseBook> = ArrayList()
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_books
    override val viewModel: BooksViewModel by viewModels { viewModelFactory }

    private lateinit var freeBookListAdapter: FreeBookListAdapter

    val args: BooksFragmentArgs by navArgs()

    override fun onResume() {
        super.onResume()
        freeBookListAdapter.submitList(books)
        checkEmptyList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        viewDataBinding.toolbar.title = args.title

        freeBookListAdapter = FreeBookListAdapter(appExecutors) {
            navController.navigate(BooksFragmentDirections.actionBooksFragmentToChapterNav(it.id, it.title))
        }
        viewDataBinding.rvBooksList.adapter = freeBookListAdapter
    }

    private fun checkEmptyList() {
        if (books.isEmpty()) {
            viewDataBinding.emptyView.visibility = View.VISIBLE
        } else {
            viewDataBinding.emptyView.visibility = View.GONE
        }
    }
}
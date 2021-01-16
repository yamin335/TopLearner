package com.rtchubs.engineerbooks.ui.history

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.HistoryFragmentBinding
import com.rtchubs.engineerbooks.databinding.WebViewBinding
import com.rtchubs.engineerbooks.ui.LogoutHandlerCallback
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewViewModel

class HistoryFragment: BaseFragment<HistoryFragmentBinding, HistoryViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_history
    override val viewModel by viewModels<HistoryViewModel>{
        viewModelFactory
    }
    private var listener: LogoutHandlerCallback? = null

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var historyListAdapter: HistoryListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }

        if (context is NavDrawerHandlerCallback) {
            drawerListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        drawerListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyListAdapter = HistoryListAdapter(
            appExecutors
        ) { item ->
            //navController.navigate(HistoryFragmentDirections.actionPayFragmentToLoadWebViewFragment2(item.bookID, item.bookTitle, item.chapterID, item.chapterTitle))
        }

        viewDataBinding.rvHistory.adapter = historyListAdapter

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewModel.historyItems.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                if (list.isEmpty()) {
                    viewDataBinding.emptyHistoryView.visibility = View.VISIBLE
                } else {
                    viewDataBinding.emptyHistoryView.visibility = View.GONE
                    historyListAdapter.submitList(list)
                }
            }
        })
    }

}
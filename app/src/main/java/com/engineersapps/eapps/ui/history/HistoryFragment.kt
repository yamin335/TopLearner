package com.engineersapps.eapps.ui.history

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.HistoryFragmentBinding
import com.engineersapps.eapps.ui.LogoutHandlerCallback
import com.engineersapps.eapps.ui.NavDrawerHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment

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
//            item.chapter?.let {
//                navigateTo(HistoryFragmentDirections.actionPayFragmentToLoadWebViewFragment2(it))
//            }
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
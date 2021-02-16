package com.rtchubs.engineerbooks.ui.transaction

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.TransactionFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class TransactionFragment : BaseFragment<TransactionFragmentBinding, TransactionViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_transaction
    override val viewModel: TransactionViewModel by viewModels {
        viewModelFactory
    }

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var transactionAdapter: TransactionListAdapter
    lateinit var adminTransactionAdapter: AdminTransactionListAdapter

    lateinit var userData: InquiryAccount

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavDrawerHandlerCallback) {
            drawerListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
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
        userData = preferencesHelper.getUser()

        adminTransactionAdapter = AdminTransactionListAdapter(appExecutors) {

        }

        transactionAdapter = TransactionListAdapter(appExecutors) {

        }

        if (userData.customer_type_id == 2) {
            viewDataBinding.linearSummary.visibility = View.VISIBLE
            viewDataBinding.recyclerTransactions.adapter = adminTransactionAdapter
        } else {
            viewDataBinding.linearSummary.visibility = View.GONE
            viewDataBinding.recyclerTransactions.adapter = transactionAdapter
        }

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewModel.salesInvoice.observe(viewLifecycleOwner, Observer { allTransaction ->
            allTransaction?.let {
                viewDataBinding.emptyView.visibility = View.GONE
                transactionAdapter.submitList(it)
            }
        })

        viewModel.adminTransactionResponse.observe(viewLifecycleOwner, Observer { allTransaction ->
            allTransaction?.let {
                viewDataBinding.emptyView.visibility = View.GONE
                adminTransactionAdapter.submitList(it)
            }
        })

        if (userData.customer_type_id == 2) {
            viewModel.getAdminTransactions(1, 1)
        } else {
            viewModel.getAllTransaction(userData.id ?: 0)
        }
    }
}
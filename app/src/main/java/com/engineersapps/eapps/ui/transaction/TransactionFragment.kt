package com.engineersapps.eapps.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.TransactionFragmentBinding
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.common.BaseFragment

class TransactionFragment : BaseFragment<TransactionFragmentBinding, TransactionViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_transaction
    override val viewModel: TransactionViewModel by viewModels {
        viewModelFactory
    }

    lateinit var transactionAdapter: TransactionListAdapter
    lateinit var adminTransactionAdapter: AdminTransactionListAdapter

    lateinit var userData: InquiryAccount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

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

//        viewDataBinding.linearSummary.visibility = View.VISIBLE
//        viewDataBinding.recyclerTransactions.adapter = adminTransactionAdapter

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

        viewModel.partnerPaymentStatus.observe(viewLifecycleOwner, Observer {
            it?.let { data ->
                viewDataBinding.amountEarned.text = "${data.totalamountearns ?: 0} ৳"
                viewDataBinding.amountDue.text = "${data.totalamountdue ?: 0} ৳"
                viewDataBinding.amountPaid.text = "${data.totalamountpaid ?: 0} ৳"
            }
        })

        if (userData.customer_type_id == 2) {
            viewModel.getAdminTransactions(userData.mobile ?: "")
            viewModel.getPartnerPaymentStatus(userData.mobile, userData.CityID, userData.UpazilaID)
        } else {
            viewModel.getAllTransaction(userData.id ?: 0)
        }

//        viewModel.getAdminTransactions("01733255589")
    }
}
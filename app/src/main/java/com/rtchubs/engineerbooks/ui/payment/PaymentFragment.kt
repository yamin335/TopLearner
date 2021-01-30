package com.rtchubs.engineerbooks.ui.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.PaymentFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.home.Home2Fragment
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showSuccessToast
import com.rtchubs.engineerbooks.bkash.Checkout

import com.rtchubs.engineerbooks.bkash.PaymentRequest

import com.rtchubs.engineerbooks.bkash.WebViewCheckoutActivity


class PaymentFragment : BaseFragment<PaymentFragmentBinding, PaymentViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_payment
    override val viewModel: PaymentViewModel by viewModels {
        viewModelFactory
    }

    val args: PaymentFragmentArgs by navArgs()
    lateinit var userData: InquiryAccount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)
        userData = preferencesHelper.getUser()
        viewModel.amount.observe(viewLifecycleOwner, Observer {  mobileNo ->
            mobileNo?.let {
                viewDataBinding.btnPayNow.isEnabled = it.isNotEmpty() && it != "0"
            }
        })

        viewModel.salesInvoice.observe(viewLifecycleOwner, Observer { invoice ->
            invoice?.let {
                Home2Fragment.allBookList.map { classWiseBook ->
                    classWiseBook.isPaid = true
                }
                preferencesHelper.isBookPaid = true
                hideKeyboard()
                if (args.bookId == it.BookID) {
                    navController.popBackStack()
                    showSuccessToast(requireContext(), "Successfully purchased")
                } else {
                    showErrorToast(requireContext(), "Purchase is not successful!")
                }
            }
        })

        viewDataBinding.btnPayNow.setOnClickListener {

            val checkout = Checkout()
            checkout.setAmount("10")

            checkout.setVersion("two")

//            if (sale.isChecked()) {
//                checkout.setIntent("sale")
//            } else {
                checkout.setIntent("authorization")
           // }

            val intent = Intent(context, WebViewCheckoutActivity::class.java)

            intent.putExtra("values", checkout)

            startActivity(intent)

//            val firstName = userData.firstName ?: ""
//            val lastName = userData.lastName ?: ""
//            viewModel.createOrder(CreateOrderBody(userData.id ?: 0, userData.mobile ?: "",
//                viewModel.amount.value?.toInt() ?: 0, 0, 0,
//                0, "", userData.upazila ?: "", userData.city ?: "",
//                userData.UpazilaID ?: 0, userData.CityID ?: 0, "",
//                "", "", args.bookId, userData.class_id ?: 0,
//                "$firstName $lastName", args.bookName))
        }
    }
}
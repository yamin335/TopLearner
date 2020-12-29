package com.rtchubs.engineerbooks.ui.payment

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.databinding.LayoutOperatorSelectionBinding
import com.rtchubs.engineerbooks.databinding.PaymentFragmentBinding
import com.rtchubs.engineerbooks.databinding.SignInBinding
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.home.Home2Fragment
import com.rtchubs.engineerbooks.util.AppConstants.commonErrorMessage
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showSuccessToast

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
                    if(classWiseBook.id == it.BookID) classWiseBook.isPaid = true
                }
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
            val firstName = userData.firstName ?: ""
            val lastName = userData.lastName ?: ""
            viewModel.createOrder(CreateOrderBody(userData.id ?: 0, userData.mobile ?: "",
                viewModel.amount.value?.toInt() ?: 0, 0, 0,
                0, "", userData.upazila ?: "", userData.city ?: "",
                userData.UpazilaID ?: 0, userData.CityID ?: 0, "",
                "", "", args.bookId, userData.class_id ?: 0,
                "$firstName $lastName", args.bookName))
        }
    }
}
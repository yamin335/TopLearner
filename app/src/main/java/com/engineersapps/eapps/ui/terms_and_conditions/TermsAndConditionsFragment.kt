package com.engineersapps.eapps.ui.terms_and_conditions

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.databinding.TermsBinding
import com.engineersapps.eapps.ui.OTPHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment


class TermsAndConditionsFragment : BaseFragment<TermsBinding, TermsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_terms_and_condition
    override val viewModel: TermsViewModel by viewModels {
        viewModelFactory
    }

    private var startOTPListenerCallback: OTPHandlerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OTPHandlerCallback) {
            startOTPListenerCallback = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        startOTPListenerCallback = null
    }

    override fun onResume() {
        super.onResume()
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        viewDataBinding.btnAccept.setOnClickListener {
            isTermsAccepted = true
            navController.popBackStack()
        }

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer {
            viewDataBinding.btnAccept.isEnabled = it != ApiCallStatus.LOADING
        })
    }

    companion object {
        var isTermsAccepted: Boolean? = null
    }

}
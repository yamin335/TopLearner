package com.engineersapps.eapps.ui.terms_and_conditions

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.databinding.TermsBinding
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.OTPHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.util.AppConstants
import com.engineersapps.eapps.util.showErrorToast


class TermsAndConditionsFragment : BaseFragment<TermsBinding, TermsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_terms_and_condition
    override val viewModel: TermsViewModel by viewModels {
        viewModelFactory
    }

    lateinit var registrationLocalHelper: InquiryAccount
    lateinit var registrationRemoteHelper: InquiryAccount

    val args: TermsAndConditionsFragmentArgs by navArgs()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        registrationLocalHelper = args.registrationHelper
        registrationRemoteHelper = args.registrationHelper

        viewDataBinding.btnAccept.setOnClickListener {
            registrationLocalHelper.isAcceptedTandC = true
            registrationRemoteHelper.isAcceptedTandC = true
            requestOTPCode(registrationRemoteHelper)
        }

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer {
            viewDataBinding.btnAccept.isEnabled = it != ApiCallStatus.LOADING
        })

//        viewDataBinding.webView.settings.javaScriptEnabled = true
//        viewDataBinding.webView.settings.loadWithOverviewMode = true
//
//        viewDataBinding.webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                return false
//            }
//        }
//        viewDataBinding.webView.loadUrl(TERMS_AND_CONDITIONS_URL)
    }

    private fun requestOTPCode(registrationHelper: InquiryAccount) {
        startOTPListenerCallback?.onStartOTPListener()
        viewModel.requestOTPCode(registrationHelper).observe(viewLifecycleOwner, Observer { response ->
            response?.data?.Account?.let {
                if (it.isAcceptedTandC == true) {
                    registrationRemoteHelper = it
                    registrationRemoteHelper.mobile_operator = registrationLocalHelper.mobile_operator
                    navController.navigate(TermsAndConditionsFragmentDirections.actionTermsAndConditionsToOtpSignInFragment3(registrationRemoteHelper))
                } else {
                    showErrorToast(mContext, response.msg ?: AppConstants.commonErrorMessage)
                }
            }
        })
    }
}
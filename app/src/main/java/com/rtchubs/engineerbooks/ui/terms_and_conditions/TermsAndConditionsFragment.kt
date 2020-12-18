package com.rtchubs.engineerbooks.ui.terms_and_conditions

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.TermsBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.login.SignInFragmentDirections
import com.rtchubs.engineerbooks.util.AppConstants
import com.rtchubs.engineerbooks.util.AppConstants.TERMS_AND_CONDITIONS_URL
import com.rtchubs.engineerbooks.util.showErrorToast


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

        viewDataBinding.webView.settings.javaScriptEnabled = true
        viewDataBinding.webView.settings.loadWithOverviewMode = true

        viewDataBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }
        viewDataBinding.webView.loadUrl(TERMS_AND_CONDITIONS_URL)
    }

    private fun requestOTPCode(registrationHelper: InquiryAccount) {
        viewModel.requestOTPCode(registrationHelper).observe(viewLifecycleOwner, Observer { response ->
            response?.data?.Account?.let {
                if (it.isAcceptedTandC == true) {
                    registrationRemoteHelper = it
                    registrationRemoteHelper.mobileOperator =registrationLocalHelper.mobileOperator
                    navController.navigate(TermsAndConditionsFragmentDirections.actionTermsAndConditionsToOtpSignInFragment3(registrationRemoteHelper))
                } else {
                    showErrorToast(mContext, response.msg ?: AppConstants.commonErrorMessage)
                }
            }
        })
    }
}
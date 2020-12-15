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

        viewDataBinding.btnAccept.setOnClickListener {
            val helper = args.registrationHelper
            helper.isAcceptedTandC = true
            requestOTPCode(helper)
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
                    navController.navigate(TermsAndConditionsFragmentDirections.actionTermsAndConditionsToOtpSignInFragment3(it))
                } else {
                    showErrorToast(mContext, response.msg ?: AppConstants.commonErrorMessage)
                }
            }
        })
    }
}
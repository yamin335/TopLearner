package com.engineersapps.eapps.ui.payment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.databinding.SSLPaymentFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment


class SSLPaymentFragment : BaseFragment<SSLPaymentFragmentBinding, SSLPaymentViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_ssl_payment
    override val viewModel: SSLPaymentViewModel by viewModels {
        viewModelFactory
    }

    val args: SSLPaymentFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        viewDataBinding.webView.clearCache(false)
        viewDataBinding.webView.isClickable = true
        viewDataBinding.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        viewDataBinding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        viewDataBinding.webView.isScrollbarFadingEnabled = true
        viewDataBinding.webView.settings.useWideViewPort = true
        viewDataBinding.webView.settings.loadWithOverviewMode = true
        viewDataBinding.webView.setInitialScale(1)
        viewDataBinding.webView.settings.allowFileAccess = true
        viewDataBinding.webView.settings.allowFileAccessFromFileURLs = true
        viewDataBinding.webView.settings.allowUniversalAccessFromFileURLs = true
        viewDataBinding.webView.settings.setSupportZoom(true)
        viewDataBinding.webView.settings.builtInZoomControls = true
        viewDataBinding.webView.settings.displayZoomControls = false
        viewDataBinding.webView.settings.domStorageEnabled = true
        viewDataBinding.webView.settings.setAppCachePath(requireContext().cacheDir.absolutePath)
        viewDataBinding.webView.settings.setAppCacheEnabled(true)
        viewDataBinding.webView.settings.javaScriptEnabled = true
        viewDataBinding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        viewDataBinding.webView.settings.defaultTextEncodingName = "utf-8"
        viewDataBinding.webView.webChromeClient = WebChromeClient()
        viewDataBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                if (url?.contains("backend.engineersmath.com/v1/sales/paymentsuccess") == true) {
                    PaymentFragment.isPaymentSuccessful = true
                    navController.popBackStack()
                }
            }
        }
        viewDataBinding.webView.loadUrl(args.paymentUrl)
    }
}
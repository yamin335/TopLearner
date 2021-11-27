package com.engineersapps.eapps.ui.bkash

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.engineersapps.eapps.R
import com.engineersapps.eapps.binding.FragmentDataBindingComponent
import com.engineersapps.eapps.databinding.BKashDialogFragmentBinding
import com.engineersapps.eapps.models.bkash.BKashCreateResponse
import com.engineersapps.eapps.util.autoCleared
import dagger.android.support.DaggerDialogFragment

class BKashDialogFragment internal constructor(
    private val callBack: BkashPaymentCallback,
    private val bkashData: BKashCreateResponse
): DaggerDialogFragment() {

    private var binding by autoCleared<BKashDialogFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()
    private var request = ""

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_bkash,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val paymentRequest = BKashPaymentRequest(checkout.amount, checkout.intent)
//        request = Gson().toJson(paymentRequest)

        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true

        //Below part is for enabling webview settings for using javascript and accessing html files and other assets
        binding.webView.isClickable = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.setAppCacheEnabled(true)
        binding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webView.clearCache(false)
        binding.webView.settings.allowFileAccessFromFileURLs = true
        binding.webView.settings.allowUniversalAccessFromFileURLs = true
//        viewDataBinding.webView.webChromeClient = WebChromeClient()
//        viewDataBinding.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
//        viewDataBinding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
//        viewDataBinding.webView.isScrollbarFadingEnabled = true
//        viewDataBinding.webView.settings.useWideViewPort = true
//        viewDataBinding.webView.settings.loadWithOverviewMode = true
//        viewDataBinding.webView.setInitialScale(1)

        binding.webView.addJavascriptInterface(
            JavaScriptWebViewInterface(requireContext()),
            "AndroidNative"
        )

        binding.webView.webViewClient = object : WebViewClient() {

//            override fun onReceivedSslError(
//                view: WebView?,
//                handler: SslErrorHandler?,
//                error: SslError?
//            ) {
//                handler?.proceed()
//            }
//
//            override fun shouldOverrideUrlLoading(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): Boolean {
//                val url = request?.url?.toString()
//
//                if (url == "https://www.bkash.com/terms-and-conditions") {
//                    val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                    startActivity(myIntent)
//                    return true
//                }
//                return super.shouldOverrideUrlLoading(view, request)
//            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if (binding.loader != null) {
                    binding.loader.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {

                if (url?.contains("status=success") == true && url.contains("backend.engineersmath.com/bkashsuccess")) {
                    callBack.onPaymentSuccess()
                }

                if (url?.contains("status=failure") == true && url.contains("backend.engineersmath.com/bkashsuccess")) {
                    callBack.onPaymentFailed()
                }

                val paymentRequestJson = "{paymentRequest:$request}"
                binding.webView.loadUrl("javascript:callReconfigure( $paymentRequestJson )")
                binding.webView.loadUrl("javascript:clickPayButton()")
                if (binding.loader != null) {
                    binding.loader.visibility = View.GONE
                }
            }
        }

//        binding.webView.loadUrl("file:///android_asset/www/checkout_120.html")
        binding.webView.loadUrl(bkashData.bkashURL ?: "")
    }

    inner class JavaScriptWebViewInterface(context: Context) {
        var mContext: Context = context

        // Handle event from the web page
        @JavascriptInterface
        fun onPaymentSuccess(response: String) {
//            val paymentResponse = Gson().fromJson(response, BKashPaymentResponse::class.java)
//            callBack.onPaymentSuccess(paymentResponse)
        }

        @JavascriptInterface
        fun onPaymentFailed() {
            callBack.onPaymentFailed()
        }

        @JavascriptInterface
        fun onPaymentCancelled() {
            callBack.onPaymentCancelled()
        }
    }

    interface BkashPaymentCallback {
        fun onPaymentSuccess()
        fun onPaymentFailed()
        fun onPaymentCancelled()
    }
}
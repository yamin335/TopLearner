package com.rtchubs.engineerbooks.ui.bkash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiCallStatus
import com.rtchubs.engineerbooks.databinding.BKashFragmentBinding
import com.rtchubs.engineerbooks.models.bkash.BKashPaymentRequest
import com.rtchubs.engineerbooks.models.bkash.BKashPaymentResponse
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.home.Home2Fragment
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showSuccessToast

class BKashFragment : BaseFragment<BKashFragmentBinding, BKashViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_bkash

    override val viewModel: BKashViewModel by viewModels { viewModelFactory }

    val args: BKashFragmentArgs by navArgs()

    private var request = ""
    lateinit var userData: InquiryAccount

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")

        userData = preferencesHelper.getUser()
        viewModel.salesInvoice.observe(viewLifecycleOwner, Observer { invoice ->
            invoice?.let {
                Home2Fragment.allBookList.map { classWiseBook ->
                    classWiseBook.isPaid = true
                }
                preferencesHelper.isBookPaid = true
                hideKeyboard()
                if (args.bookId == it.BookID) {
                    navigateTo(BKashFragmentDirections.actionBkashFragmentToHome2Fragment())
                    showSuccessToast(requireContext(), "Successfully purchased")
                } else {
                    showErrorToast(requireContext(), "Purchase is not successful!")
                }
            }
        })

        val checkout = args.checkout
        val paymentRequest = BKashPaymentRequest(checkout.amount, checkout.intent)
        request = Gson().toJson(paymentRequest)

        val webSettings: WebSettings = viewDataBinding.webView.settings
        webSettings.javaScriptEnabled = true

        //Below part is for enabling webview settings for using javascript and accessing html files and other assets
        viewDataBinding.webView.isClickable = true
        viewDataBinding.webView.settings.domStorageEnabled = true
        viewDataBinding.webView.settings.setAppCacheEnabled(true)
        viewDataBinding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        viewDataBinding.webView.clearCache(false)
        viewDataBinding.webView.settings.allowFileAccessFromFileURLs = true
        viewDataBinding.webView.settings.allowUniversalAccessFromFileURLs = true
//        viewDataBinding.webView.webChromeClient = WebChromeClient()
//        viewDataBinding.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
//        viewDataBinding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
//        viewDataBinding.webView.isScrollbarFadingEnabled = true
//        viewDataBinding.webView.settings.useWideViewPort = true
//        viewDataBinding.webView.settings.loadWithOverviewMode = true
//        viewDataBinding.webView.setInitialScale(1)

        viewDataBinding.webView.addJavascriptInterface(
            JavaScriptWebViewInterface(requireContext()),
            "AndroidNative"
        )

        viewDataBinding.webView.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString()
                if (url == "https://www.bkash.com/terms-and-conditions") {
                    val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(myIntent)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                val paymentRequestJson = "{paymentRequest:$request}"
                viewDataBinding.webView.loadUrl("javascript:callReconfigure( $paymentRequestJson )")
                viewDataBinding.webView.loadUrl("javascript:clickPayButton()")
                viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
            }
        }

        viewDataBinding.webView.loadUrl("file:///android_asset/www/checkout_120.html")
    }

    private fun saveBKaskPayment(response: BKashPaymentResponse) {
        val firstName = userData.firstName ?: ""
        val lastName = userData.lastName ?: ""
        viewModel.createOrder(
            CreateOrderBody(
                userData.id ?: 0, userData.mobile ?: "",
                response.amount.toInt(), 0, 0,
                0, "", userData.upazila ?: "", userData.city ?: "",
                userData.UpazilaID ?: 0, userData.CityID ?: 0, "",
                "", "", args.bookId, userData.class_id ?: 0,
                "$firstName $lastName", args.bookName
            )
        )
    }

    inner class JavaScriptWebViewInterface(context: Context) {
        var mContext: Context = context

        // Handle event from the web page
        @JavascriptInterface
        fun onPaymentSuccess(response: String) {
            val paymentResponse = Gson().fromJson(response, BKashPaymentResponse::class.java)
            saveBKaskPayment(paymentResponse)
        }

        @JavascriptInterface
        fun onPaymentFailed() {

        }

        @JavascriptInterface
        fun onPaymentCancel() {

        }
    }
}
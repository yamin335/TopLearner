package com.engineersapps.eapps.ui.payment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.PaymentFragmentBinding
import com.engineersapps.eapps.models.bkash.BKashCreateResponse
import com.engineersapps.eapps.models.payment.CoursePaymentRequest
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.transactions.CreateOrderBody
import com.engineersapps.eapps.ui.MyCourseTabSelection
import com.engineersapps.eapps.ui.bkash.BKashDialogFragment
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.util.*
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener
import java.security.SecureRandom

class PaymentFragment : BaseFragment<PaymentFragmentBinding, PaymentViewModel>() {

    companion object {
        var firstPackagePrice = 0
        var secondPackagePrice = 0
        var thirdPackagePrice = 0

        var firstPackageTitle = ""
        var secondPackageTitle = ""
        var thirdPackageTitle = ""

        var first_duration = 0
        var second_duration = 0
        var third_duration = 0
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_payment
    override val viewModel: PaymentViewModel by viewModels {
        viewModelFactory
    }

    val args: PaymentFragmentArgs by navArgs()

    lateinit var userData: InquiryAccount

    private lateinit var bkashPgwDialog: BKashDialogFragment

    private lateinit var invoiceNumber: String

    private lateinit var packageAdapter: ArrayAdapter<String>
    private var titlePackageList = arrayOf("--সিলেক্ট করুন--")

    private var courseDuration = 0

    private var myCourseTabSelectionListener: MyCourseTabSelection? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyCourseTabSelection) {
            myCourseTabSelectionListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        myCourseTabSelectionListener = null
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.spPackages.setSelection(1, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceNumber = generateInvoiceID()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)



        userData = preferencesHelper.getUser()

        viewModel.packagePrice.postValue(0)
        viewModel.discount.postValue(0)
        viewModel.amount.postValue(0)

        viewModel.packagePrice.postValue(secondPackagePrice)

        titlePackageList = arrayOf(firstPackageTitle, secondPackageTitle, thirdPackageTitle)
        packageAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_large, titlePackageList)
        packageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spPackages.adapter = packageAdapter

        viewDataBinding.spPackages.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                when(position) {
                    0 -> {
                        viewModel.packagePrice.postValue(firstPackagePrice)
                        courseDuration = first_duration
                    }
                    1 -> {
                        viewModel.packagePrice.postValue(secondPackagePrice)
                        courseDuration = second_duration
                    }
                    2 -> {
                        viewModel.packagePrice.postValue(thirdPackagePrice)
                        courseDuration = third_duration
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        viewModel.packagePrice.postValue(secondPackagePrice)
        viewModel.discount.postValue((userData.discount_amount ?: 0.0).toInt())

        viewModel.packagePrice.observe(viewLifecycleOwner, Observer {
            val discount = viewModel.discount.value ?: 0
            val packagePrice = it ?: 0
            viewModel.amount.postValue(packagePrice - discount)
        })

        viewModel.discount.observe(viewLifecycleOwner, Observer {
            val discount = it ?: 0
            val packagePrice = viewModel.packagePrice.value ?: 0
            viewModel.amount.postValue(packagePrice - discount)
        })

        viewModel.coursePurchaseSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            isSuccess?.let {
                if (it) {
                    hideKeyboard()
                    findNavController().popBackStack()
                    showSuccessToast(requireContext(), "Payment Successful")
                    myCourseTabSelectionListener?.selectMyCourseTab()
                    viewModel.coursePurchaseSuccess.postValue(null)
                }
            }
        })

        viewModel.salesInvoice.observe(viewLifecycleOwner, Observer { invoice ->
            invoice?.let {
//                Home2Fragment.allBookList.map { classWiseBook ->
//                    classWiseBook.isPaid = true
//                }
//                val paidBook = args.book
//                paidBook.isPaid = true
//                preferencesHelper.savePaidBook(paidBook)
                hideKeyboard()
                findNavController().popBackStack()
                showSuccessToast(requireContext(), "Payment Successful")
                myCourseTabSelectionListener?.selectMyCourseTab()
//                if (args.book.bookID == it.BookID) {
//                    findNavController().popBackStack()
//                    showSuccessToast(requireContext(), "Payment Successful")
//                } else {
//                    showErrorToast(requireContext(), "Payment is not successful!")
//                }
            }
        })

        viewModel.isValidPromoCode.observe(viewLifecycleOwner, Observer {
            it?.let { isValid ->
                if (isValid) {
                    showSuccessToast(requireContext(), "Discount from code is applied")
                } else {
                    showErrorToast(requireContext(), "Your code is not valid!")
                }
                viewModel.isValidPromoCode.postValue(null)
            }
        })

        viewModel.promoCodeText.observe(viewLifecycleOwner, Observer { code ->
            code?.let {
                viewDataBinding.btnApply.isEnabled = it.isNotBlank()
            }
        })

        viewModel.promoCode.observe(viewLifecycleOwner, Observer { promoCode ->
            promoCode?.let {
                val promoDiscount = promoCode.discount ?: 0
                var discount = viewModel.discount.value ?: 0
                discount -= viewModel.promoCodeDiscount
                viewModel.promoCodeDiscount = promoDiscount
                discount += viewModel.promoCodeDiscount
                viewModel.discount.postValue(discount)
            }
        })

        viewDataBinding.btnApply.setOnClickListener {
            viewModel.verifyPromoCode()
        }

        viewModel.offers.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                it.forEach { offer ->
                    val firstDate = offer.FromDate?.split("T")?.first()?.getMilliFromDate()
                        ?: Long.MAX_VALUE
                    val lastDate =
                        offer.EndDate?.split("T")?.first()?.getMilliFromDate() ?: Long.MAX_VALUE
                    val date = System.currentTimeMillis()

                    if (offer.archived == false && date in firstDate..lastDate) {
                        val offerAmount = offer.offer_amount ?: 0
                        var discount = viewModel.discount.value ?: 0
                        discount += offerAmount
                        viewModel.discount.postValue(discount)
//                        val packagePrice = viewModel.packagePrice.value ?: 0
//                        viewModel.amount.postValue(packagePrice - discount)
                        return@Observer
                    }
                }
            }
        })

        viewDataBinding.termsAndConditions.setOnClickListener {
            navigateTo(PaymentFragmentDirections.actionPaymentFragmentToTermsNavGraph())
        }

        viewModel.amount.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewDataBinding.btnPayNow.isEnabled = it > 0 && viewDataBinding.cbTerms.isChecked
            }
        })

        viewDataBinding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            val amount = viewModel.amount.value ?: return@setOnCheckedChangeListener
            viewDataBinding.btnPayNow.isEnabled = amount > 0 && isChecked
        }

        viewDataBinding.btnPayNow.setOnClickListener {

            if (userData.customer_type_id == 2) {
                callPartnerPayment()
            } else {
                callPayment()
            }

//            viewModel.getBkashPaymentUrl(userData.mobile ?: "",
//                viewModel.mount.value ?: "0",
//                invoiceNumber ?: generateInvoiceID()).observe(viewLifecycleOwner, Observer { response ->
//                    response?.let {
//                        shoWBkashDialog(viewModel.amount.value ?: "0", it)
//                    }
//            })

//            val checkout = Checkout()
//            checkout.setAmount("10")
//
//            checkout.setVersion("two")
//
////            if (sale.isChecked()) {
////                checkout.setIntent("sale")
////            } else {
//                checkout.setIntent("authorization")
//           // }
//
//            val intent = Intent(context, WebViewCheckoutActivity::class.java)
//
//            intent.putExtra("values", checkout)
//
//            startActivity(intent)

//            viewModel.createOrder(
//                CreateOrderBody(
//                    userData.id ?: 0, userData.mobile ?: "",
//                    viewModel.amount.value?.toDouble()?.toInt() ?: 0, 0, 0,
//                    0, "", userData.upazila ?: "", userData.city ?: "",
//                    userData.UpazilaID ?: 0, userData.CityID ?: 0, generateInvoiceID(),
//                    "", "", args.book.bookID ?: 0, userData.class_id ?: 0,
//                    "${userData.first_name ?: ""} ${userData.last_name ?: ""}", args.book.bookName ?: "", ""
//                )
//            )
        }
        viewModel.getAllOffers(userData.CityID, userData.UpazilaID)
    }

    private fun shoWBkashDialog(amount: String, bkashData: BKashCreateResponse) {
        // --------bKash start--------

        //val checkout = BKashCheckout(viewModel.amount.value ?: "0", "authorization", "two")

        bkashPgwDialog = BKashDialogFragment(object : BKashDialogFragment.BkashPaymentCallback {
            override fun onPaymentSuccess() {
                saveBKaskPayment(amount, bkashData)
                bkashPgwDialog.dismiss()
            }

            override fun onPaymentFailed() {
                showErrorToast(requireContext(), "Payment failed!")
                bkashPgwDialog.dismiss()
            }

            override fun onPaymentCancelled() {
                showErrorToast(requireContext(), "Payment cancelled!")
                bkashPgwDialog.dismiss()
            }

        }, bkashData)
        bkashPgwDialog.isCancelable = true
        bkashPgwDialog.show(childFragmentManager, "#bkash_payment_dialog")

        // --------bKash End--------
    }

    private fun saveBKaskPayment(amount: String, bkashData: BKashCreateResponse) {
        val firstName = userData.first_name ?: ""
        val lastName = userData.last_name ?: ""

        val amount = amount.toDouble()
        val discount = viewModel.discount.value ?: 0
        viewModel.createOrder(
            CreateOrderBody(
                userData.id ?: 0, userData.mobile ?: "",
                amount.toInt(), 0, 0,
                discount, "", userData.upazila ?: "", userData.city ?: "",
                userData.UpazilaID ?: 0, userData.CityID ?: 0, bkashData.invoicenumber ?: "N/A",
                "", bkashData.paymentID ?: "N/A", args.bookId, userData.class_id ?: 0,
                "$firstName $lastName", args.bookName ?: "", bkashData.paymentID ?: "N/A"
            )
        )
    }

    private fun generateInvoiceID(): String {
        val random1 = "${1 + SecureRandom().nextInt(99999)}"
        val random2 = "${1 + SecureRandom().nextInt(99999)}"
        return "${random1}${random2}"
    }

    private fun saveSSLPayment(p0: SSLCTransactionInfoModel?) {
        val response = p0 ?: return

        val firstName = userData.first_name ?: ""
        val lastName = userData.last_name ?: ""

        viewModel.purchaseCourse(
            CreateOrderBody(
                userData.id ?: 0, userData.mobile ?: "",
                response.amount.toDouble().toInt(), 0, 0,
                0, "", userData.upazila ?: "", userData.city ?: "",
                userData.UpazilaID ?: 0, userData.CityID ?: 0, invoiceNumber,
                "", response.bankTranId ?: "N/A", args.bookId, userData.class_id ?: 0,
                "$firstName $lastName", args.bookName ?: "", response.amount ?: "N/A",
                "", "", viewModel.promoCode.value?.code ?: "",
                viewModel.promoCode.value?.partner_id ?: 0
            ),
            CoursePaymentRequest(
                userData.mobile, invoiceNumber,userData.id, args.courseId,
                args.coursePrice.toInt(), response.amount.toDouble().toInt(), courseDuration
            )
        )
    }

    private fun callPartnerPayment() {
        val payLoad = CoursePaymentRequest(
            userData.mobile, invoiceNumber, userData.id, args.courseId,
            args.coursePrice.toInt(), 0, courseDuration
        )

        viewModel.purchaseCourse(null, payLoad)
    }

    private fun callPayment() {
        val amount = viewModel.amount.value?.toDouble() ?: return
        val sslCommerzInitialization = SSLCommerzInitialization ("engineersappslive","61149E9C3398383671",  amount, SSLCCurrencyType.BDT,invoiceNumber, "Book", SSLCSdkType.LIVE)
        IntegrateSSLCommerz
            .getInstance(requireContext())
            .addSSLCommerzInitialization(sslCommerzInitialization)
            .buildApiCall(object : SSLCTransactionResponseListener {
                override fun transactionSuccess(p0: SSLCTransactionInfoModel?) {
                    if (p0?.riskLevel == "0") {
                        //Timber.d("Transaction Successfully completed")
                        saveSSLPayment(p0)
                    } else {
                        //Timber.d("Transaction in risk.")
                    }
                }

                override fun transactionFail(p0: String?) {
                    p0?.let {
                        showWarningToast(requireContext(), it)
                    }
                }

                override fun merchantValidationError(p0: String?) {
                    p0?.let {
                        showWarningToast(requireContext(), it)
                    }
                    val ss = ""
                    when (p0) {
//                        ErrorKeys.USER_INPUT_ERROR -> Timber.e("User Input Error")
//                        ErrorKeys.INTERNET_CONNECTION_ERROR -> Timber.e("INTERNET_CONNECTION_ERROR")
//                        ErrorKeys.DATA_PARSING_ERROR -> Timber.e("DATA_PARSING_ERROR")
//                        ErrorKeys.CANCEL_TRANSACTION_ERROR -> Timber.e("CANCEL_TRANSACTION_ERROR")
//                        ErrorKeys.SERVER_ERROR -> Timber.e("SERVER_ERROR")
//                        ErrorKeys.NETWORK_ERROR -> Timber.e("NETWORK_ERROR")
                    }
                }
            })


        //final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization ("yourStoreID","yourPassword", amount, SSLCCurrencyType.BDT,"123456789098765", "yourProductType", SSLCSdkType.TESTBOX);

//        Also, users can add optional field like below:
//        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization ("yourStoreID","yourPassword", amount, SSLCCurrencyType.BDT,"123456789098765", "yourProductType", SSLCSdkType.TESTBOX).addMultiCardName(“”).addIpnUrl(“”);
//
//        Afterwards, user need to call below class to connect with SSLCommerz:
//        IntegrateSSLCommerz
//            .getInstance(context)
//            .addSSLCommerzInitialization(sslCommerzInitialization)
//            .buildApiCall(this);

//        val mandatoryFieldModel = MandatoryFieldModel("testbox", "qwerty", viewModel.amount.value ?: "0", invoiceNumber ?: generateInvoiceID(), CurrencyType.BDT, SdkType.TESTBOX, SdkCategory.BANK_LIST)
//
//        PayUsingSSLCommerz.getInstance().setData(requireActivity(), mandatoryFieldModel, null, null, null, object :
//            OnPaymentResultListener {
//            override fun transactionSuccess(transactionInfo: TransactionInfo) {
//                if (transactionInfo.riskLevel == "0") {
//                    Timber.d("Transaction Successfully completed")
//                    saveSSLPayment(transactionInfo)
//                } else {
//                    Timber.d("Transaction in risk.")
//                }
//            }
//
//            override fun transactionFail(transactionInfo: TransactionInfo) {
//                Timber.d("Transaction Fail")
//            }
//
//            override fun error(i: Int) {
//                when (i) {
//                    ErrorKeys.USER_INPUT_ERROR -> Timber.e("User Input Error")
//                    ErrorKeys.INTERNET_CONNECTION_ERROR -> Timber.e("INTERNET_CONNECTION_ERROR")
//                    ErrorKeys.DATA_PARSING_ERROR -> Timber.e("DATA_PARSING_ERROR")
//                    ErrorKeys.CANCEL_TRANSACTION_ERROR -> Timber.e("CANCEL_TRANSACTION_ERROR")
//                    ErrorKeys.SERVER_ERROR -> Timber.e("SERVER_ERROR")
//                    ErrorKeys.NETWORK_ERROR -> Timber.e("NETWORK_ERROR")
//                }
//            }
//        })
    }
}
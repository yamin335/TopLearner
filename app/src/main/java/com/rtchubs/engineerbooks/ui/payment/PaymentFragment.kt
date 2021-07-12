package com.rtchubs.engineerbooks.ui.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.PaymentFragmentBinding
import com.rtchubs.engineerbooks.models.bkash.BKashCreateResponse
import com.rtchubs.engineerbooks.models.payment.CoursePaymentRequest
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.ui.bkash.BKashDialogFragment
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.getMilliFromDate
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showSuccessToast
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

    var discount = 0
    var packagePrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceNumber = generateInvoiceID()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        userData = preferencesHelper.getUser()

        viewDataBinding.firstPackage.text = "$firstPackageTitle -- ${getString(R.string.taka_sign)}$firstPackagePrice"
        viewDataBinding.secondPackage.text = "$secondPackageTitle -- ${getString(R.string.taka_sign)}$secondPackagePrice"
        viewDataBinding.thirdPackage.text = "$thirdPackageTitle -- ${getString(R.string.taka_sign)}$thirdPackagePrice"

        viewModel.promoDiscount.postValue(0)
        viewModel.personalDiscount.postValue(0)
        viewModel.offerAmount.postValue(0)
        viewModel.amount.postValue(0)

        packagePrice = secondPackagePrice

        viewDataBinding.packageGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.firstPackage -> packagePrice = firstPackagePrice
                R.id.secondPackage -> packagePrice = secondPackagePrice
                R.id.thirdPackage -> packagePrice = thirdPackagePrice
            }
            viewModel.amount.postValue(packagePrice - discount)
        }

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
//                if (args.book.bookID == it.BookID) {
//                    findNavController().popBackStack()
//                    showSuccessToast(requireContext(), "Payment Successful")
//                } else {
//                    showErrorToast(requireContext(), "Payment is not successful!")
//                }
            }
        })

//        viewModel.amount.observe(viewLifecycleOwner, Observer {  amount ->
//            amount?.let {
//                viewDataBinding.btnPayNow.isEnabled = it.isNotEmpty() && it != "0"
//            }
//        })

        viewModel.offers.observe(viewLifecycleOwner, Observer {
            discount = (userData.discount_amount ?: 0.0).toInt()
            viewModel.personalDiscount.postValue(discount)

            if (it.isNullOrEmpty()) {
                viewModel.amount.postValue(packagePrice - discount)
            } else {
                it.forEach { offer ->
                    val firstDate = offer.FromDate?.split("T")?.first()?.getMilliFromDate()
                        ?: Long.MAX_VALUE
                    val lastDate =
                        offer.EndDate?.split("T")?.first()?.getMilliFromDate() ?: Long.MAX_VALUE
                    val date = System.currentTimeMillis()

                    if (offer.archived == false && date in firstDate..lastDate) {
                        val offerAmount = offer.offer_amount ?: 0
                        viewModel.offerAmount.postValue(offerAmount)
                        discount += offerAmount
                        viewModel.amount.postValue(packagePrice - discount)
                        return@Observer
                    }
                }
            }
        })

        viewDataBinding.btnPayNow.setOnClickListener {

            callPayment()

//            viewModel.getBkashPaymentUrl(userData.mobile ?: "",
//                viewModel.amount.value ?: "0",
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
                "", ""
            ),
            CoursePaymentRequest(
                userData.mobile, invoiceNumber,userData.id, args.courseId, args.coursePrice.toInt(), response.amount.toDouble().toInt()
            )
        )
    }

    private fun callPayment() {
        val amount = viewModel.amount.value?.toDouble() ?: return
        val sslCommerzInitialization = SSLCommerzInitialization ("testbox","qwerty",  amount, SSLCCurrencyType.BDT,invoiceNumber, "Book", SSLCSdkType.TESTBOX)
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
                    //Timber.d("Transaction Fail")
                }

                override fun merchantValidationError(p0: String?) {
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
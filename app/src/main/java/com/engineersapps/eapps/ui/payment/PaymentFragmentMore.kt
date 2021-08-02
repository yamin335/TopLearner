package com.engineersapps.eapps.ui.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.PaymentFragmentBinding
import com.engineersapps.eapps.models.bkash.BKashCreateResponse
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.bkash.BKashDialogFragment
import com.engineersapps.eapps.ui.common.BaseFragment
import java.security.SecureRandom

class PaymentFragmentMore : BaseFragment<PaymentFragmentBinding, PaymentViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_payment
    override val viewModel: PaymentViewModel by viewModels {
        viewModelFactory
    }

    //val args: PaymentFragmentMoreArgs by navArgs()

    lateinit var userData: InquiryAccount

    private lateinit var bkashPgwDialog: BKashDialogFragment

    private var invoiceNumber: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        userData = preferencesHelper.getUser()

//        viewModel.salesInvoice.observe(viewLifecycleOwner, Observer { invoice ->
//            invoice?.let {
//                Home2Fragment.allBookList.map { classWiseBook ->
//                    classWiseBook.isPaid = true
//                }
//                val paidBook = args.book
//                paidBook.isPaid = true
//                preferencesHelper.savePaidBook(paidBook)
//                hideKeyboard()
//                if (args.book.bookID == it.BookID) {
//                    findNavController().popBackStack()
//                    showSuccessToast(requireContext(), "Payment Successful")
//                } else {
//                    showErrorToast(requireContext(), "Purchase is not successful!")
//                }
//            }
//        })

//        viewModel.amount.observe(viewLifecycleOwner, Observer {  mobileNo ->
//            mobileNo?.let {
//                viewDataBinding.btnPayNow.isEnabled = it.isNotEmpty() && it != "0"
//            }
//        })

//        viewModel.offers.observe(viewLifecycleOwner, Observer {
//            val discount = (userData.discount_amount ?: 0.0).toInt()
//            it?.let { offers ->
//                if (offers.isNotEmpty()) {
//                    offers.forEach { offer ->
//                        val firstDate = offer.FromDate?.split("T")?.first()?.getMilliFromDate()
//                            ?: Long.MAX_VALUE
//                        val lastDate =
//                            offer.EndDate?.split("T")?.first()?.getMilliFromDate() ?: Long.MAX_VALUE
//                        val date = System.currentTimeMillis()
//
//                        if (offer.archived == false && date in firstDate..lastDate) {
//                            val offerAmount = offer.offer_amount ?: 0
//                            val temp = args.book.price
//                            var amount = temp.toInt()
//                            val totalDiscount = offerAmount + discount
//                            if (totalDiscount > 0) {
//                                viewDataBinding.linearTotalPayable.visibility = View.VISIBLE
//                                viewDataBinding.linearTotalDiscount.visibility = View.VISIBLE
//                                viewDataBinding.totalPayableAmount.text = "${amount}৳"
//                                viewDataBinding.totalDiscount.text = "${totalDiscount}৳"
//                            } else {
//                                viewDataBinding.linearTotalPayable.visibility = View.GONE
//                                viewDataBinding.linearTotalDiscount.visibility = View.GONE
//                            }
//                            amount -= totalDiscount
//                            viewModel.amount.postValue(amount.toString())
//                            return@Observer
//                        }
//                    }
//                }
//            }
//
//            val amount = args.book.price.toInt()
//            if (discount > 0) {
//                viewDataBinding.linearTotalPayable.visibility = View.VISIBLE
//                viewDataBinding.linearTotalDiscount.visibility = View.VISIBLE
//                viewDataBinding.totalPayableAmount.text = "${amount}৳"
//                viewDataBinding.totalDiscount.text = "${discount}৳"
//            } else {
//                viewDataBinding.linearTotalPayable.visibility = View.GONE
//                viewDataBinding.linearTotalDiscount.visibility = View.GONE
//            }
//            viewModel.amount.postValue((args.book.price - discount).toString())
//        })

        viewDataBinding.btnPayNow.setOnClickListener {

//            viewModel.getBkashPaymentUrl(userData.mobile ?: "",
//                viewModel.amount.value ?: "0",
//                invoiceNumber ?: generateInvoiceID()).observe(viewLifecycleOwner, Observer { response ->
//                response?.let {
//                    shoWBkashDialog(viewModel.amount.value ?: "0", it)
//                }
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

//        bkashPgwDialog = BKashDialogFragment(object : BKashDialogFragment.BkashPaymentCallback {
//            override fun onPaymentSuccess() {
//                saveBKaskPayment(amount, bkashData)
//                bkashPgwDialog.dismiss()
//            }
//
//            override fun onPaymentFailed() {
//                showErrorToast(requireContext(), "Payment failed!")
//                bkashPgwDialog.dismiss()
//            }
//
//            override fun onPaymentCancelled() {
//                showErrorToast(requireContext(), "Payment cancelled!")
//                bkashPgwDialog.dismiss()
//            }
//
//        }, bkashData)
//        bkashPgwDialog.isCancelable = true
//        bkashPgwDialog.show(childFragmentManager, "#bkash_payment_dialog")

        // --------bKash End--------
    }

//    private fun saveBKaskPayment(response: BKashPaymentResponse) {
//        val firstName = userData.first_name ?: ""
//        val lastName = userData.last_name ?: ""
//
//        val amount = response.amount.toDouble()
//        viewModel.createOrder(
//            CreateOrderBody(
//                userData.id ?: 0, userData.mobile ?: "",
//                amount.toInt(), 0, 0,
//                0, "", userData.upazila ?: "", userData.city ?: "",
//                userData.UpazilaID ?: 0, userData.CityID ?: 0, "",
//                "", "", args.book.bookID ?: 0, userData.class_id ?: 0,
//                "$firstName $lastName", args.book.bookName ?: ""
//            )
//        )
//    }

//    private fun saveBKaskPayment(amount: String, bkashData: BKashCreateResponse) {
//        val firstName = userData.first_name ?: ""
//        val lastName = userData.last_name ?: ""
//
//        val amount = amount.toDouble()
//        viewModel.createOrder(
//            CreateOrderBody(
//                userData.id ?: 0, userData.mobile ?: "",
//                amount.toInt(), 0, 0,
//                0, "", userData.upazila ?: "", userData.city ?: "",
//                userData.UpazilaID ?: 0, userData.CityID ?: 0, bkashData.invoicenumber ?: "N/A",
//                "", bkashData.paymentID ?: "N/A", args.book.bookID ?: 0, userData.class_id ?: 0,
//                "$firstName $lastName", args.book.bookName ?: "", bkashData.paymentID ?: "N/A"
//            )
//        )
//    }

    private fun generateInvoiceID(): String {
        val random1 = "${1 + SecureRandom().nextInt(9999)}"
        val random2 = "${1 + SecureRandom().nextInt(9999)}"
        return "${random1}${random2}"
    }
}
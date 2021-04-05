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
import com.rtchubs.engineerbooks.models.bkash.BKashCheckout
import com.rtchubs.engineerbooks.models.bkash.BKashCreateResponse
import com.rtchubs.engineerbooks.models.bkash.BKashPaymentResponse
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.ui.bkash.BKashDialogFragment
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.home.Home2Fragment
import com.rtchubs.engineerbooks.util.getMilliFromDate
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showSuccessToast
import java.security.SecureRandom

class PaymentFragment : BaseFragment<PaymentFragmentBinding, PaymentViewModel>() {

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

    private var invoiceNumber: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        invoiceNumber = generateInvoiceID()

        userData = preferencesHelper.getUser()

        viewModel.salesInvoice.observe(viewLifecycleOwner, Observer { invoice ->
            invoice?.let {
                Home2Fragment.allBookList.map { classWiseBook ->
                    classWiseBook.isPaid = true
                }
                val paidBook = args.book
                paidBook.isPaid = true
                preferencesHelper.savePaidBook(paidBook)
                hideKeyboard()
                if (args.book.bookID == it.BookID) {
                    findNavController().popBackStack()
                    showSuccessToast(requireContext(), "Successfully purchased")
                } else {
                    showErrorToast(requireContext(), "Purchase is not successful!")
                }
            }
        })

        viewModel.amount.observe(viewLifecycleOwner, Observer {  amount ->
            amount?.let {
                viewDataBinding.btnPayNow.isEnabled = it.isNotEmpty() && it != "0"
            }
        })

        viewModel.offers.observe(viewLifecycleOwner, Observer {
            val discount = (userData.discount_amount ?: 0.0).toInt()
            it?.let { offers ->
                if (offers.isNotEmpty()) {
                    offers.forEach { offer ->
                        val firstDate = offer.FromDate?.split("T")?.first()?.getMilliFromDate()
                            ?: Long.MAX_VALUE
                        val lastDate =
                            offer.EndDate?.split("T")?.first()?.getMilliFromDate() ?: Long.MAX_VALUE
                        val date = System.currentTimeMillis()

                        if (offer.archived == false && date in firstDate..lastDate) {
                            val offerAmount = offer.offer_amount ?: 0
                            val temp = args.book.price
                            var amount = temp.toInt()
                            val totalDiscount = offerAmount + discount
                            if (totalDiscount > 0) {
                                viewDataBinding.linearTotalPayable.visibility = View.VISIBLE
                                viewDataBinding.linearTotalDiscount.visibility = View.VISIBLE
                                viewDataBinding.totalPayableAmount.text = "${amount}৳"
                                viewDataBinding.totalDiscount.text = "${totalDiscount}৳"
                            } else {
                                viewDataBinding.linearTotalPayable.visibility = View.GONE
                                viewDataBinding.linearTotalDiscount.visibility = View.GONE
                            }
                            amount -= totalDiscount
                            viewModel.amount.postValue(amount.toString())
                            return@Observer
                        }
                    }
                }
            }

            val amount = args.book.price.toInt()
            if (discount > 0) {
                viewDataBinding.linearTotalPayable.visibility = View.VISIBLE
                viewDataBinding.linearTotalDiscount.visibility = View.VISIBLE
                viewDataBinding.totalPayableAmount.text = "${amount}৳"
                viewDataBinding.totalDiscount.text = "${discount}৳"
            } else {
                viewDataBinding.linearTotalPayable.visibility = View.GONE
                viewDataBinding.linearTotalDiscount.visibility = View.GONE
            }
            viewModel.amount.postValue((args.book.price - discount).toString())
        })

        viewDataBinding.btnPayNow.setOnClickListener {

            viewModel.getBkashPaymentUrl(userData.mobile ?: "",
                viewModel.amount.value ?: "0",
                invoiceNumber ?: generateInvoiceID()).observe(viewLifecycleOwner, Observer { response ->
                    response?.let {
                        shoWBkashDialog(it)
                    }
            })

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

    private fun shoWBkashDialog(bkashData: BKashCreateResponse) {
        // --------bKash start--------

        val checkout = BKashCheckout(viewModel.amount.value ?: "0", "authorization", "two")

        bkashPgwDialog = BKashDialogFragment(object : BKashDialogFragment.BkashPaymentCallback {
            override fun onPaymentSuccess(bkashResponse: BKashPaymentResponse) {
                saveBKaskPayment(bkashResponse)
                bkashPgwDialog.dismiss()
            }

            override fun onPaymentFailed() {
                showErrorToast(requireContext(), "Purchase is not successful, Payment failed!")
                bkashPgwDialog.dismiss()
            }

            override fun onPaymentCancelled() {
                showErrorToast(requireContext(), "Purchase is not successful, Payment cancelled!")
                bkashPgwDialog.dismiss()
            }

        }, bkashData)
        bkashPgwDialog.isCancelable = true
        bkashPgwDialog.show(childFragmentManager, "#bkash_payment_dialog")

        // --------bKash End--------
    }

    private fun saveBKaskPayment(response: BKashPaymentResponse) {
        val firstName = userData.first_name ?: ""
        val lastName = userData.last_name ?: ""

        val amount = response.amount.toDouble()
        viewModel.createOrder(
            CreateOrderBody(
                userData.id ?: 0, userData.mobile ?: "",
                amount.toInt(), 0, 0,
                0, "", userData.upazila ?: "", userData.city ?: "",
                userData.UpazilaID ?: 0, userData.CityID ?: 0, invoiceNumber ?: generateInvoiceID(),
                "", response.paymentID, args.book.bookID ?: 0, userData.class_id ?: 0,
                "$firstName $lastName", args.book.bookName ?: "", response.transactionID
            )
        )
    }

    private fun generateInvoiceID(): String {
        val random1 = "${1 + SecureRandom().nextInt(99999)}"
        val random2 = "${1 + SecureRandom().nextInt(99999)}"
        return "${random1}${random2}"
    }
}
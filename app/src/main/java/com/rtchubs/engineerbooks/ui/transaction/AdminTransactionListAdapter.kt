package com.rtchubs.engineerbooks.ui.transaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.PartnerTransactionItemBinding
import com.rtchubs.engineerbooks.models.transactions.PartnerTransaction
import com.rtchubs.engineerbooks.models.transactions.Transaction
import com.rtchubs.engineerbooks.util.DataBoundListAdapter
import java.text.SimpleDateFormat
import java.util.*

class AdminTransactionListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((PartnerTransaction) -> Unit)? = null

) : DataBoundListAdapter<PartnerTransaction, PartnerTransactionItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<PartnerTransaction>() {
        override fun areItemsTheSame(oldItem: PartnerTransaction, newItem: PartnerTransaction): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: PartnerTransaction,
            newItem: PartnerTransaction
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<Transaction>()
    override fun createBinding(parent: ViewGroup): PartnerTransactionItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_transaction_partner, parent, false
        )
    }


    override fun bind(binding: PartnerTransactionItemBinding, position: Int) {
        val item = getItem(position)
        binding.paymentMethod = if (item.payment_method.isNullOrBlank()) "Unknown Payment Method" else item.payment_method
        val amount = item.payamount?.toString()
        binding.paidAmount = if (amount.isNullOrBlank()) "" else "$amount ৳"
        binding.paymentDate = item.payment_date
        binding.remarks = item.remarks

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }

    private fun formatISODate(inputDate: String?): String {
        if (inputDate == null) return ""
        try {
            val simpleDateFormatter = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
            )

            // 'T' is a literal. 'X' is ISO Zone Offset[like +01, -08]; For UTC, it is interpreted as 'Z'(Zero) literal.
            val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX"

            // since no built-in format, we provides pattern directly.
            val dateTimeFormatter = SimpleDateFormat(pattern, Locale.getDefault())

            val myDate: Date = dateTimeFormatter.parse(inputDate)!!

            return simpleDateFormatter.format(myDate)
        } catch (e: Exception) {
            e.printStackTrace()
            return inputDate
        }
    }
}
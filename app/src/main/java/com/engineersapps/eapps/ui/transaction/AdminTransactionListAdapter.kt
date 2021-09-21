package com.engineersapps.eapps.ui.transaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.PartnerTransactionItemBinding
import com.engineersapps.eapps.models.transactions.PartnerTransaction
import com.engineersapps.eapps.models.transactions.Transaction
import com.engineersapps.eapps.util.DataBoundListAdapter
import java.text.SimpleDateFormat

class AdminTransactionListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((PartnerTransaction) -> Unit)? = null
) : DataBoundListAdapter<PartnerTransaction, PartnerTransactionItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<PartnerTransaction>() {
        override fun areItemsTheSame(oldItem: PartnerTransaction, newItem: PartnerTransaction): Boolean {
            return oldItem.udid == newItem.udid
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

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val paymentDate = format.parse(item.PaymentDate ?: "")

        val dateFormat = SimpleDateFormat("dd MMM yyyy")

        paymentDate?.let {
            try {
                binding.paymentDate = dateFormat.format(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.remarks = item.remarks

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
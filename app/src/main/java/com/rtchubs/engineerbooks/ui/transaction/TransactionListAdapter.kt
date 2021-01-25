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
import com.rtchubs.engineerbooks.databinding.TransactionItemBinding
import com.rtchubs.engineerbooks.models.transactions.Transaction
import com.rtchubs.engineerbooks.util.DataBoundListAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Transaction) -> Unit)? = null

) : DataBoundListAdapter<Transaction, TransactionItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Transaction,
            newItem: Transaction
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<Transaction>()
    override fun createBinding(parent: ViewGroup): TransactionItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_transaction, parent, false
        )
    }


    override fun bind(binding: TransactionItemBinding, position: Int) {
        val item = getItem(position)
        binding.serial = (position + 1).toString()
        binding.packageName = if (item.BookName.isNullOrBlank()) "Unknown Package" else item.BookName
        binding.paymentAmount = "${item.GrandTotal} ৳"
        binding.paymentDate = item.Date
        binding.validDate = item.updatedAt

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
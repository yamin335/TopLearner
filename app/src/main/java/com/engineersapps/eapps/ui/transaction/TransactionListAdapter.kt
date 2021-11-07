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
import com.engineersapps.eapps.databinding.TransactionItemBinding
import com.engineersapps.eapps.models.transactions.Transaction
import com.engineersapps.eapps.util.DataBoundListAdapter
import java.text.ParseException
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
        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val paymentDate = format.parse(item.Date ?: "")

        val dateFormat = SimpleDateFormat("dd MMM yyyy")

        paymentDate?.let {
            try {
                binding.paymentDate = dateFormat.format(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val duration = item.duration ?: 0
                val remainDays = item.remaindays ?: 0
                val date = Calendar.getInstance()
                date.time = paymentDate
                date[Calendar.DATE] = date[Calendar.DATE] + duration + remainDays
                binding.validDate = dateFormat.format(date.time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

//    private fun formatISODate(inputDate: String?): String {
//        if (inputDate == null) return ""
//        try {
//            val simpleDateFormatter = SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
//            )
//
//            // 'T' is a literal. 'X' is ISO Zone Offset[like +01, -08]; For UTC, it is interpreted as 'Z'(Zero) literal.
//            val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX"
//
//            // since no built-in format, we provides pattern directly.
//            val dateTimeFormatter = SimpleDateFormat(pattern, Locale.getDefault())
//
//            val myDate: Date = dateTimeFormatter.parse(inputDate)!!
//
//            return simpleDateFormatter.format(myDate)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return inputDate
//        }
//    }
}
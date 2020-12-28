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
import com.rtchubs.engineerbooks.databinding.HistoryListItemBinding
import com.rtchubs.engineerbooks.databinding.TransactionItemBinding
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.models.transactions.Transaction
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

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
        binding.bookName = if (item.BookName.isNullOrBlank()) "Unknown Book" else item.BookName
        binding.grandTotal = item.GrandTotal?.toString()
        binding.paidAmount = item.PaidAmount?.toString()
        binding.dueAmount = item.DueAmount?.toString()

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
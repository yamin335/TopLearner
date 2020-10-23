package com.rtchubs.engineerbooks.ui.history

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
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class HistoryListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((HistoryItem) -> Unit)? = null

) : DataBoundListAdapter<HistoryItem, HistoryListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: HistoryItem,
            newItem: HistoryItem
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<HistoryItem>()
    override fun createBinding(parent: ViewGroup): HistoryListItemBinding {
        return DataBindingUtil.inflate<HistoryListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.list_item_history, parent, false
        )
    }


    override fun bind(binding: HistoryListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
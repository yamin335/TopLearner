package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.HomeClassListItemBinding
import com.rtchubs.engineerbooks.models.Book
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class HomeClassListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Book) -> Unit)? = null
) : DataBoundListAdapter<Book, HomeClassListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    val onClicked = MutableLiveData<Book>()
    override fun createBinding(parent: ViewGroup): HomeClassListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_home_class_list, parent, false
        )
    }

    override fun bind(binding: HomeClassListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
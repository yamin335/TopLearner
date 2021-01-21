package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.HomeClassListItemBinding
import com.rtchubs.engineerbooks.models.Book
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class HomeClassListAdapter(
    private val appExecutors: AppExecutors,
    private val customerTypeID: Int?,
    private val itemCallback: ((ClassWiseBook) -> Unit)? = null
) : DataBoundListAdapter<ClassWiseBook, HomeClassListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<ClassWiseBook>() {
        override fun areItemsTheSame(oldItem: ClassWiseBook, newItem: ClassWiseBook): Boolean {
            return oldItem.udid == newItem.udid
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: ClassWiseBook,
            newItem: ClassWiseBook
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    val onClicked = MutableLiveData<ClassWiseBook>()
    override fun createBinding(parent: ViewGroup): HomeClassListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_home_class_list, parent, false
        )
    }

    override fun bind(binding: HomeClassListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        if (customerTypeID == 2) {
            binding.lockView.visibility = View.GONE
        } else {
            val isPaid = item.isPaid ?: false
            binding.lockView.visibility = if (isPaid) View.GONE else View.VISIBLE
        }

        binding.rootCard.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
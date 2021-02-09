package com.rtchubs.engineerbooks.ui.notice_board

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.NoticeListitemBinding
import com.rtchubs.engineerbooks.models.notice_board.Notice
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class NoticeListAdapter(
    appExecutors: AppExecutors,
    private var itemCallback: ((Notice) -> Unit)? = null
) : DataBoundListAdapter<Notice, NoticeListitemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Notice>() {
        override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Notice,
            newItem: Notice
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup): NoticeListitemBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_notice, parent, false
        )

    override fun bind(binding: NoticeListitemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
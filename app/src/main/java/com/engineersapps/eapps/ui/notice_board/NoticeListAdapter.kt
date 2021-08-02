package com.engineersapps.eapps.ui.notice_board

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.NoticeListitemBinding
import com.engineersapps.eapps.models.notice_board.Notice
import com.engineersapps.eapps.util.DataBoundListAdapter

class NoticeListAdapter(
    appExecutors: AppExecutors,
    private var itemCallback: ((Notice) -> Unit)? = null
) : DataBoundListAdapter<Notice, NoticeListitemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Notice>() {
        override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            return oldItem.udid == newItem.udid
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
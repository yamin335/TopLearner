package com.engineersapps.eapps.ui.live_class_schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.LiveClassScheduleItemBinding
import com.engineersapps.eapps.models.LiveClassSchedule
import com.engineersapps.eapps.util.DataBoundListAdapter

class LiveClassScheduleListAdapter(
    appExecutors: AppExecutors,
    private var itemCallback: ((LiveClassSchedule) -> Unit)? = null
) : DataBoundListAdapter<LiveClassSchedule, LiveClassScheduleItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<LiveClassSchedule>() {
        override fun areItemsTheSame(oldItem: LiveClassSchedule, newItem: LiveClassSchedule): Boolean {
            return oldItem.udid == newItem.udid
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: LiveClassSchedule,
            newItem: LiveClassSchedule
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup): LiveClassScheduleItemBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_live_class_schedule, parent, false
        )

    override fun bind(binding: LiveClassScheduleItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
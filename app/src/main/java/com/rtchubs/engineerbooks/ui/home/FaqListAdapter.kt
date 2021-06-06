package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.FaqListItemBinding
import com.rtchubs.engineerbooks.models.faq.Faq
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class FaqListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Faq) -> Unit)
) : DataBoundListAdapter<Faq, FaqListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Faq>() {
        override fun areItemsTheSame(oldItem: Faq, newItem: Faq): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Faq,
            newItem: Faq
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    
    override fun createBinding(parent: ViewGroup): FaqListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_course_faq, parent, false
        )
    }

    override fun bind(binding: FaqListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        binding.expandableLayout.isExpanded = item.isExpanded

        binding.topBar.setOnClickListener {
            toggleExpanded(item, binding)
            binding.expandableLayout.isExpanded = item.isExpanded
        }
    }

    fun toggleExpanded(item: Faq, binding: FaqListItemBinding) {
        //val toggle: Transition = TransitionInflater.from(binding.root.context).inflateTransition(R.transition.search_bar_toogle)
        item.isExpanded = !item.isExpanded
        //toggle.duration = if (item.isExpanded) 200L else 150L
        //TransitionManager.beginDelayedTransition(binding.root as ViewGroup, toggle)
        //binding.answer.animate().duration = 200
        //binding.answer.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        //binding.arrowIndicator.setImageResource(if (item.isExpanded) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24)
        if (item.isExpanded) {
            binding.arrowIndicator.animate().setDuration(200).rotation(180F)
        } else {
            binding.arrowIndicator.animate().setDuration(200).rotation(0F)
        }
    }
}
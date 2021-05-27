package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseFaqListItemBinding
import com.rtchubs.engineerbooks.models.home.CourseFaq
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class CourseFaqListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((CourseFaq) -> Unit)
) : DataBoundListAdapter<CourseFaq, CourseFaqListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<CourseFaq>() {
        override fun areItemsTheSame(oldItem: CourseFaq, newItem: CourseFaq): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CourseFaq,
            newItem: CourseFaq
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    
    override fun createBinding(parent: ViewGroup): CourseFaqListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_course_faq, parent, false
        )
    }

    override fun bind(binding: CourseFaqListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.arrowIndicator.setOnClickListener {
            toggleExpanded(item, binding)
        }

        binding.title.setOnClickListener {
            toggleExpanded(item, binding)
        }
    }

    fun toggleExpanded(item: CourseFaq, binding: CourseFaqListItemBinding) {
        //val toggle: Transition = TransitionInflater.from(binding.root.context).inflateTransition(R.transition.search_bar_toogle)
        item.isExpanded = !item.isExpanded
        //toggle.duration = if (item.isExpanded) 200L else 150L
        //TransitionManager.beginDelayedTransition(binding.root as ViewGroup, toggle)
        binding.answer.animate().duration = 200
        binding.answer.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        //binding.arrowIndicator.setImageResource(if (item.isExpanded) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24)
        if (item.isExpanded) {
            binding.arrowIndicator.animate().setDuration(200).rotation(180F)
        } else {
            binding.arrowIndicator.animate().setDuration(200).rotation(0F)
        }
    }
}
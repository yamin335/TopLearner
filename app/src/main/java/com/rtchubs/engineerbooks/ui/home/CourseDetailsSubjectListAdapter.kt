package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseDetailsSubjectListItemBinding
import com.rtchubs.engineerbooks.models.home.CourseSubject
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class CourseDetailsSubjectListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((CourseSubject) -> Unit)
) : DataBoundListAdapter<CourseSubject, CourseDetailsSubjectListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<CourseSubject>() {
        override fun areItemsTheSame(oldItem: CourseSubject, newItem: CourseSubject): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CourseSubject,
            newItem: CourseSubject
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    
    override fun createBinding(parent: ViewGroup): CourseDetailsSubjectListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_course_details, parent, false
        )
    }

    override fun bind(binding: CourseDetailsSubjectListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.arrowIndicator.setOnClickListener {
            toggleExpanded(item, binding)
        }

        binding.title.setOnClickListener {
            toggleExpanded(item, binding)
        }
    }

    fun toggleExpanded(item: CourseSubject, binding: CourseDetailsSubjectListItemBinding) {
        //val toggle: Transition = TransitionInflater.from(binding.root.context).inflateTransition(R.transition.search_bar_toogle)
        item.isExpanded = !item.isExpanded
        //toggle.duration = if (item.isExpanded) 200L else 150L
        //TransitionManager.beginDelayedTransition(binding.root as ViewGroup, toggle)
        binding.details.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        binding.details.animate().duration = 200
        //binding.arrowIndicator.setImageResource(if (item.isExpanded) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24)
        if (item.isExpanded) {
            binding.arrowIndicator.animate().setDuration(200).rotation(180F)
        } else {
            binding.arrowIndicator.animate().setDuration(200).rotation(0F)
        }
    }
}
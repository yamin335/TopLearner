package com.engineersapps.eapps.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.CourseContentListItemBinding
import com.engineersapps.eapps.models.home.CourseChapter
import com.engineersapps.eapps.util.DataBoundListAdapter

class CourseChapterListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((CourseChapter) -> Unit)
) : DataBoundListAdapter<CourseChapter, CourseContentListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<CourseChapter>() {
        override fun areItemsTheSame(oldItem: CourseChapter, newItem: CourseChapter): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CourseChapter,
            newItem: CourseChapter
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    
    override fun createBinding(parent: ViewGroup): CourseContentListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_course_content, parent, false
        )
    }

    override fun bind(binding: CourseContentListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        binding.expandableLayout.isExpanded = item.isExpanded

        binding.topBar.setOnClickListener {
            toggleExpanded(item, binding)
            binding.expandableLayout.isExpanded = item.isExpanded
        }

        val animationListAdapter = CourseTopicListAdapter(appExecutors) {

        }

        binding.details.setHasFixedSize(true)

        binding.details.adapter = animationListAdapter
        animationListAdapter.submitList(item.chapter_contents)


    }

    fun toggleExpanded(item: CourseChapter, binding: CourseContentListItemBinding) {
        //val toggle: Transition = TransitionInflater.from(binding.root.context).inflateTransition(R.transition.search_bar_toogle)
        item.isExpanded = !item.isExpanded
        //toggle.duration = if (item.isExpanded) 200L else 150L
        //TransitionManager.beginDelayedTransition(binding.root as ViewGroup, toggle)
        //binding.details.animate().duration = 200
        //binding.details.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        //binding.arrowIndicator.setImageResource(if (item.isExpanded) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24)
        if (item.isExpanded) {
            binding.arrowIndicator.animate().setDuration(200).rotation(180F)
        } else {
            binding.arrowIndicator.animate().setDuration(200).rotation(0F)
        }
    }
}
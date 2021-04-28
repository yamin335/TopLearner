package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseCategoryListItemBinding
import com.rtchubs.engineerbooks.models.home.CourseCategory
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class CourseCategoryListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((CourseCategory) -> Unit)
) : DataBoundListAdapter<CourseCategory, CourseCategoryListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<CourseCategory>() {
        override fun areItemsTheSame(oldItem: CourseCategory, newItem: CourseCategory): Boolean {
            return oldItem.udid == newItem.udid
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CourseCategory,
            newItem: CourseCategory
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    
    override fun createBinding(parent: ViewGroup): CourseCategoryListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_course_category, parent, false
        )
    }

    override fun bind(binding: CourseCategoryListItemBinding, position: Int) {
        val item = getItem(position)
        binding.categoryName = item.name

        val courseAdapter = CourseListAdapter(appExecutors) {
            itemCallback.invoke(item)
        }

        binding.courseRecycler.adapter = courseAdapter
        courseAdapter.submitList(item.books)
    }
}
package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseCategoryListItemBinding
import com.rtchubs.engineerbooks.models.home.Course
import com.rtchubs.engineerbooks.models.home.CourseCategory
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class CourseCategoryListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Course) -> Unit)
) : DataBoundListAdapter<CourseCategory, CourseCategoryListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<CourseCategory>() {
        override fun areItemsTheSame(oldItem: CourseCategory, newItem: CourseCategory): Boolean {
            return oldItem.id == newItem.id
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

        val courseAdapter = AllCourseListAdapter(appExecutors) {
            itemCallback.invoke(it)
        }

        binding.courseRecycler.adapter = courseAdapter
        courseAdapter.submitList(item.courses)
    }
}
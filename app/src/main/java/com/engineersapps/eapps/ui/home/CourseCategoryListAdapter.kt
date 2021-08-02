package com.engineersapps.eapps.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.CourseCategoryListItemBinding
import com.engineersapps.eapps.models.home.Course
import com.engineersapps.eapps.models.home.CourseCategory
import com.engineersapps.eapps.util.DataBoundListAdapter

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
        val courseList = item.courses?.filter {
            val status = it.status ?: 0
            status == 1
        }
        //courseAdapter.submitList(item.courses)
        courseAdapter.submitList(courseList)
    }
}
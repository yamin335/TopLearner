package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.CourseDetailsTeacherListItemBinding
import com.rtchubs.engineerbooks.models.home.CourseTeacher
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class CourseTeachersListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((CourseTeacher) -> Unit)
) : DataBoundListAdapter<CourseTeacher, CourseDetailsTeacherListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<CourseTeacher>() {
        override fun areItemsTheSame(oldItem: CourseTeacher, newItem: CourseTeacher): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CourseTeacher,
            newItem: CourseTeacher
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    
    override fun createBinding(parent: ViewGroup): CourseDetailsTeacherListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_teachers_for_course_details, parent, false
        )
    }

    override fun bind(binding: CourseDetailsTeacherListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.url = ""
        binding.imageRequestListener = object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                //binding.proPic.setImageResource(R.drawable.doctor_1)
                return true
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }
        }
    }
}
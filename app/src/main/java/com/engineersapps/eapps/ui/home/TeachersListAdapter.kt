package com.engineersapps.eapps.ui.home

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
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.Api.ADMIN_API_ROOT_URL
import com.engineersapps.eapps.databinding.CourseDetailsTeacherListItemBinding
import com.engineersapps.eapps.models.home.Teacher
import com.engineersapps.eapps.util.DataBoundListAdapter

class TeachersListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Teacher) -> Unit)
) : DataBoundListAdapter<Teacher, CourseDetailsTeacherListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Teacher>() {
        override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher): Boolean {
            return oldItem.teacher_id == newItem.teacher_id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Teacher,
            newItem: Teacher
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
        binding.url = "$ADMIN_API_ROOT_URL${item.image_url}"
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
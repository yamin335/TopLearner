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
import com.engineersapps.eapps.api.Api
import com.engineersapps.eapps.databinding.CourseListItemBinding
import com.engineersapps.eapps.models.home.Course
import com.engineersapps.eapps.util.DataBoundListAdapter

class AllCourseListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Course) -> Unit)
) : DataBoundListAdapter<Course, CourseListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Course,
            newItem: Course
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup): CourseListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_course, parent, false
        )
    }

    override fun bind(binding: CourseListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.url = "${Api.COURSE_IMAGE_ROOT_URL}${item.logourl}"
        binding.imageRequestListener = object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                binding.thumbnail.setImageResource(R.drawable.engineers)
                return true
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }
        }


        binding.btnVisit.setOnClickListener {
            itemCallback.invoke(item)
        }

        binding.rootCard.setOnClickListener {
            itemCallback.invoke(item)
        }
    }
}
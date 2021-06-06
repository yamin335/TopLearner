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
import com.rtchubs.engineerbooks.databinding.CourseListItemBinding
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class CourseListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((ClassWiseBook) -> Unit)
) : DataBoundListAdapter<ClassWiseBook, CourseListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<ClassWiseBook>() {
        override fun areItemsTheSame(oldItem: ClassWiseBook, newItem: ClassWiseBook): Boolean {
            return oldItem.udid == newItem.udid
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: ClassWiseBook,
            newItem: ClassWiseBook
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
//        binding.item = item
//        binding.url = "${ApiEndPoint.LOGO}/${item.logo}"
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
    }
}
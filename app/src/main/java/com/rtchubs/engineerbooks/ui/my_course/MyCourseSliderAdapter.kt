package com.rtchubs.engineerbooks.ui.my_course

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint
import com.rtchubs.engineerbooks.databinding.MyCourseListItemBinding
import com.rtchubs.engineerbooks.models.home.ClassWiseBook

class MyCourseSliderAdapter: RecyclerView.Adapter<MyCourseSliderAdapter.ViewHolder>() {

    private var slides: ArrayList<ClassWiseBook> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MyCourseListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_my_course, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(slides[position])
    }

    override fun getItemCount(): Int {
        return slides.size
    }

    fun submitList(slides: List<ClassWiseBook>) {
        this.slides = slides as ArrayList<ClassWiseBook>
        notifyDataSetChanged()
    }

    inner class ViewHolder (private val binding: MyCourseListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: ClassWiseBook) {
            binding.item = item

            binding.url = "${ApiEndPoint.LOGO}/${item.logo}"
            binding.imageRequestListener = object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    binding.thumbnail.setImageResource(R.drawable.engineers)
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }

            binding.executePendingBindings()
        }
    }
}

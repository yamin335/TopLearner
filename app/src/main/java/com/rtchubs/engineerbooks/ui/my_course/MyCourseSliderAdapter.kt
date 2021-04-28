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

class MyCourseSliderAdapter(
    private val customerTypeID: Int?,
    private val itemCallback: ((ClassWiseBook) -> Unit)
): RecyclerView.Adapter<MyCourseSliderAdapter.ViewHolder>() {

    private var slides: ArrayList<ClassWiseBook> = ArrayList()
    private var isPaid = false
    private var isTimeChanged = false

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

    fun setPaymentStatus(isPaid: Boolean) {
        this.isPaid = isPaid
        notifyDataSetChanged()
    }

    fun setTimeChangeStatus(isChanged: Boolean) {
        this.isTimeChanged = isChanged
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

            binding.rootCard.setOnClickListener {
                if (customerTypeID == 2) {
                    itemCallback.invoke(item)
                } else {
                    if (item.price ?: 0.0 > 0.0) {
//                var paymentStatus = item.isPaid ?: false
//                if (!paymentStatus) {
//                    if (isPaid) {
//                        paymentStatus = isPaid
//                    }
//                }

                        val paymentStatus = isPaid

                        if (paymentStatus && !isTimeChanged) itemCallback.invoke(item)
                    }
                }
            }

            binding.executePendingBindings()
        }
    }
}

package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint
import com.rtchubs.engineerbooks.databinding.HomeClassListItemBinding
import com.rtchubs.engineerbooks.models.Book
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class HomeClassListAdapter(
    private val appExecutors: AppExecutors,
    private val customerTypeID: Int?,
    private val itemCallback: ((ClassWiseBook) -> Unit)? = null
) : DataBoundListAdapter<ClassWiseBook, HomeClassListItemBinding>(
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

    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    private var isPaid = false
    private var isTimeChanged = false
    fun setPaymentStatus(isPaid: Boolean) {
        this.isPaid = isPaid
        notifyDataSetChanged()
    }

    fun setTimeChangeStatus(isChanged: Boolean) {
        this.isTimeChanged = isChanged
        notifyDataSetChanged()
    }
    val onClicked = MutableLiveData<ClassWiseBook>()
    override fun createBinding(parent: ViewGroup): HomeClassListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_home_class_list, parent, false
        )
    }

    override fun bind(binding: HomeClassListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        if (customerTypeID == 2) {
            binding.lockView.visibility = View.GONE
        } else {
            if (item.price ?: 0.0 > 0.0) {
                var paymentStatus = item.isPaid ?: false
                if (!paymentStatus) {
                    if (isPaid) {
                        paymentStatus = isPaid
                    }
                }

                binding.lockView.visibility = if (paymentStatus && !isTimeChanged) View.GONE else View.VISIBLE
            } else {
                binding.lockView.visibility = View.GONE
            }
        }
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
            itemCallback?.invoke(item)
        }
    }
}
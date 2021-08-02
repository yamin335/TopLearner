package com.engineersapps.eapps.ui.free_book

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
import com.engineersapps.eapps.api.ApiEndPoint
import com.engineersapps.eapps.databinding.FreeBookListItemBinding
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.util.DataBoundListAdapter

class FreeBookListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((ClassWiseBook) -> Unit)? = null
) : DataBoundListAdapter<ClassWiseBook, FreeBookListItemBinding>(
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

    override fun createBinding(parent: ViewGroup): FreeBookListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_free_book, parent, false
        )
    }

    override fun bind(binding: FreeBookListItemBinding, position: Int) {
        val item = getItem(position)
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
            itemCallback?.invoke(item)
        }
    }
}
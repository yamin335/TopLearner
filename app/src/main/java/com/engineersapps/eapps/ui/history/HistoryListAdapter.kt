package com.engineersapps.eapps.ui.history

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiEndPoint
import com.engineersapps.eapps.databinding.HistoryListItemBinding
import com.engineersapps.eapps.local_db.dbo.HistoryItem
import com.engineersapps.eapps.util.DataBoundListAdapter

class HistoryListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((HistoryItem) -> Unit)? = null

) : DataBoundListAdapter<HistoryItem, HistoryListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: HistoryItem,
            newItem: HistoryItem
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<HistoryItem>()
    override fun createBinding(parent: ViewGroup): HistoryListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_history, parent, false
        )
    }


    override fun bind(binding: HistoryListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        binding.url = "${ApiEndPoint.LOGO}/${item.chapter?.logo}"
        binding.imageRequestListener = object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                binding.thumbnail.setImageResource(R.drawable.book_6)
                return true
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }
        }

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
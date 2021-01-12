package com.rtchubs.engineerbooks.ui.home

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
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint
import com.rtchubs.engineerbooks.databinding.VideoListItemBinding
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.models.VideoItem
import com.rtchubs.engineerbooks.models.chapter.ChapterField
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class VideoListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((ChapterField) -> Unit)? = null

) : DataBoundListAdapter<ChapterField, VideoListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<ChapterField>() {
        override fun areItemsTheSame(oldItem: ChapterField, newItem: ChapterField): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: ChapterField,
            newItem: ChapterField
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<ChapterField>()
    override fun createBinding(parent: ViewGroup): VideoListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_videos, parent, false
        )
    }


    override fun bind(binding: VideoListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item
        binding.url = "${ApiEndPoint.LOGO}/${item.logo}"
        binding.imageRequestListener = object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                binding.thumbnail.setImageResource(R.drawable.book_8)
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
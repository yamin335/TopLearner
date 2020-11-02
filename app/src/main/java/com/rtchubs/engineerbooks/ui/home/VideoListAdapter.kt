package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.VideoListItemBinding
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.models.VideoItem
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class VideoListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((VideoItem) -> Unit)? = null

) : DataBoundListAdapter<VideoItem, VideoListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: VideoItem,
            newItem: VideoItem
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<VideoItem>()
    override fun createBinding(parent: ViewGroup): VideoListItemBinding {
        return DataBindingUtil.inflate<VideoListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.list_item_videos, parent, false
        )
    }


    override fun bind(binding: VideoListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
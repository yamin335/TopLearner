package com.rtchubs.engineerbooks.ui.social_media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.models.social_media.SocialMedia
import kotlinx.android.synthetic.main.list_item_social_media.view.*

class SocialMediaListAdapter internal constructor(
    private val onItemClickListener: (SocialMedia) -> Unit
) : RecyclerView.Adapter<SocialMediaListAdapter.ItemViewHolder>() {

    private var mediaList: ArrayList<SocialMedia> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_social_media, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    fun submitList(mediaList: ArrayList<SocialMedia>) {
        this.mediaList = mediaList
        notifyDataSetChanged()
    }

    inner class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = mediaList[position]

            itemView.icon.setImageDrawable(ResourcesCompat.getDrawable(itemView.context.resources, item.icon, null))

            itemView.name.text = item.name

            itemView.socialMedia.setOnClickListener {
                onItemClickListener.invoke(item)
            }
        }
    }
}
package com.rtchubs.engineerbooks.ui.chapter_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.models.Chapter
import com.rtchubs.engineerbooks.databinding.ItemChapterListBinding
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class ChapterListAdapter(
    appExecutors: AppExecutors,
    private var itemCallback: ((Chapter) -> Unit)? = null
) : DataBoundListAdapter<Chapter, ItemChapterListBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem?.id == newItem?.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Chapter,
            newItem: Chapter
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup): ItemChapterListBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_chapter_list, parent, false
        )

    override fun bind(binding: ItemChapterListBinding, position: Int) {
        val item = getItem(position)
        //item.imageUrl?.let { binding.ivChapterImage.setImageResource(it.toInt()) }
        item.title?.let { binding.tvChapterName.text = it }
        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
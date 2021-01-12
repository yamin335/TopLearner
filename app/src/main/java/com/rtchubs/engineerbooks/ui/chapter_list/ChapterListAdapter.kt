package com.rtchubs.engineerbooks.ui.chapter_list

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
import com.rtchubs.engineerbooks.api.ApiEndPoint.LOGO
import com.rtchubs.engineerbooks.models.Chapter
import com.rtchubs.engineerbooks.databinding.ItemChapterListBinding
import com.rtchubs.engineerbooks.models.Book
import com.rtchubs.engineerbooks.models.chapter.BookChapter
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class ChapterListAdapter(
    appExecutors: AppExecutors,
    private var itemCallback: ((BookChapter) -> Unit)? = null
) : DataBoundListAdapter<BookChapter, ItemChapterListBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<BookChapter>() {
        override fun areItemsTheSame(oldItem: BookChapter, newItem: BookChapter): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: BookChapter,
            newItem: BookChapter
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
        binding.url = "$LOGO/${item.logo}"
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
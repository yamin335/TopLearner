package com.engineersapps.eapps.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.CourseContentListItemBinding
import com.engineersapps.eapps.models.home.CourseChapter

class CourseContentListAdapter: RecyclerView.Adapter<CourseContentListAdapter.ViewHolder>() {

    private var contentList: ArrayList<CourseChapter> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CourseContentListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_course_content, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contentList[position])
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    fun addItemToList(item: CourseChapter, position: Int) {
        this.contentList.add(item)
        notifyItemInserted(position)
    }

    fun submitList(contentList: List<CourseChapter>) {
        this.contentList = contentList as ArrayList<CourseChapter>
        notifyDataSetChanged()
    }

    inner class ViewHolder (private val binding: CourseContentListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CourseChapter) {
            binding.item = item

            binding.expandableLayout.isExpanded = item.isExpanded

            binding.topBar.setOnClickListener {
                toggleExpanded(item, binding)
                binding.expandableLayout.isExpanded = item.isExpanded
            }

            val animationListAdapter = CourseContentTopicListAdapter()

            binding.details.adapter = animationListAdapter
            item.chapter_contents?.let {
                animationListAdapter.submitList(it)
            }

            binding.executePendingBindings()
        }

        fun toggleExpanded(item: CourseChapter, binding: CourseContentListItemBinding) {
            item.isExpanded = !item.isExpanded
            if (item.isExpanded) {
                binding.arrowIndicator.animate().setDuration(200).rotation(180F)
            } else {
                binding.arrowIndicator.animate().setDuration(200).rotation(0F)
            }
        }
    }
}

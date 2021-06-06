package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.AnimationListItemBinding
import com.rtchubs.engineerbooks.models.home.Animation
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class AnimationListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Animation) -> Unit)
) : DataBoundListAdapter<Animation, AnimationListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Animation>() {
        override fun areItemsTheSame(oldItem: Animation, newItem: Animation): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Animation,
            newItem: Animation
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    
    override fun createBinding(parent: ViewGroup): AnimationListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_animation, parent, false
        )
    }

    override fun bind(binding: AnimationListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        if (item.type == "Lecture Sheet") {
            binding.icon.setImageResource(R.drawable.ic_twotone_assignment_24)
        } else {
            binding.icon.setImageResource(R.drawable.ic_twotone_play_circle_24)
        }
    }
}
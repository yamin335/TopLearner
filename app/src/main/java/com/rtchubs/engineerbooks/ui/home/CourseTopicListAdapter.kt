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

class CourseTopicListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((Animation) -> Unit)
) : DataBoundListAdapter<Animation, AnimationListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<Animation>() {
        override fun areItemsTheSame(oldItem: Animation, newItem: Animation): Boolean {
            return oldItem.udid == newItem.udid
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

        when (item.type) {
            "3D Class" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_3d)
            }

            "Animation" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_animation)
            }

            "Creative Question" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_creative_questions)
            }

            "Live Class" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_live_class)
            }

            "MCQ" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_mcq)
            }

            "Quiz Test" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_quiz_test)
            }

            "Lecture Sheet" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_solve_sheet)
            }

            "Solve Sheet" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_solve_sheet)
            }

            "Somadhan" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_somadhan)
            }

            "Video" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_video)
            }

            "Chapter Content" -> {
                binding.icon.setImageResource(R.drawable.topic_ic_chapter_content)
            }

            else -> {
                binding.icon.setImageResource(R.drawable.topic_ic_video)
            }
        }
    }
}
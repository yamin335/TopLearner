package com.engineersapps.eapps.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.AnimationListItemBinding
import com.engineersapps.eapps.models.home.Animation

class CourseContentTopicListAdapter: RecyclerView.Adapter<CourseContentTopicListAdapter.ViewHolder>() {

    private var animationList: List<Animation> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AnimationListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_animation, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(animationList[position])
    }

    override fun getItemCount(): Int {
        return animationList.size
    }

    fun submitList(animationList: List<Animation>) {
        this.animationList = animationList
        notifyDataSetChanged()
    }

    inner class ViewHolder (private val binding: AnimationListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Animation) {
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
            binding.executePendingBindings()
        }
    }
}

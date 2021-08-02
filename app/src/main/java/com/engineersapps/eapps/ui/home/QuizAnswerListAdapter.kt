package com.engineersapps.eapps.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.QuizAnswerListItemBinding
import com.engineersapps.eapps.models.QuizAnswerItem
import com.engineersapps.eapps.util.DataBoundListAdapter

class QuizAnswerListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((QuizAnswerItem) -> Unit)? = null

) : DataBoundListAdapter<QuizAnswerItem, QuizAnswerListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<QuizAnswerItem>() {
        override fun areItemsTheSame(oldItem: QuizAnswerItem, newItem: QuizAnswerItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: QuizAnswerItem,
            newItem: QuizAnswerItem
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    val onClicked = MutableLiveData<QuizAnswerItem>()
    private var selectedItemIndex = -1

    override fun createBinding(parent: ViewGroup): QuizAnswerListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_quiz_answer_list, parent, false
        )
    }


    override fun bind(binding: QuizAnswerListItemBinding, position: Int) {
        val item = getItem(position)

        binding.item = item

        binding.isSelected = selectedItemIndex == position

//        if (selectedItemIndex == position) {
//            binding.cardCategory.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.themeColor))
//            binding.categoryName.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
//        } else {
//            binding.cardCategory.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
//            binding.categoryName.setTextColor(ColorStateList.valueOf(Color.parseColor("#555555")))
//        }

        binding.root.setOnClickListener {
            selectedItemIndex = position
            itemCallback?.invoke(item)
            notifyDataSetChanged()
        }
    }
}
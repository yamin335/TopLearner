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
import com.engineersapps.eapps.databinding.QuizListItemBinding
import com.engineersapps.eapps.models.QuizAnswerItem
import com.engineersapps.eapps.models.QuizItem
import com.engineersapps.eapps.util.DataBoundListAdapter

class QuizListAdapter(
    private val appExecutors: AppExecutors,
    private val itemCallback: ((QuizAnswerItem) -> Unit)? = null
) : DataBoundListAdapter<QuizItem, QuizListItemBinding>(
    appExecutors = appExecutors, diffCallback = object : DiffUtil.ItemCallback<QuizItem>() {
        override fun areItemsTheSame(oldItem: QuizItem, newItem: QuizItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: QuizItem,
            newItem: QuizItem
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    val onClicked = MutableLiveData<QuizItem>()
    override fun createBinding(parent: ViewGroup): QuizListItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_quiz, parent, false
        )
    }


    override fun bind(binding: QuizListItemBinding, position: Int) {
        val item = getItem(position)
        binding.item = item

        val quizAnswerListAdapter = QuizAnswerListAdapter(
            appExecutors
        ) { answer ->
            itemCallback?.invoke(answer)
        }

        binding.rvQuizList.isNestedScrollingEnabled = false
        binding.rvQuizList.setHasFixedSize(true)
        binding.rvQuizList.adapter = quizAnswerListAdapter

        quizAnswerListAdapter.submitList(item.answers)
    }
}
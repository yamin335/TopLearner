package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.engineerbooks.AppExecutors
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.ItemDoctorsListBinding
import com.rtchubs.engineerbooks.models.SubBook
import com.rtchubs.engineerbooks.models.chapter.BookChapter
import com.rtchubs.engineerbooks.util.DataBoundListAdapter

class DoctorsListAdapter(
    private val appExecutors: AppExecutors,
    private var itemCallback: ((SubBook) -> Unit)? = null
) : DataBoundListAdapter<BookChapter, ItemDoctorsListBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<BookChapter>() {
        override fun areItemsTheSame(oldItem: BookChapter, newItem: BookChapter): Boolean {
            return oldItem?.id == newItem?.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: BookChapter,
            newItem: BookChapter
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    // Properties
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    lateinit var topSpecialistAdapter: TopSpecialistAdapter
    lateinit var subDoctorsListAdapter: SubDoctorsListAdapter
    lateinit var mergeAdapter: MergeAdapter

    override fun createBinding(parent: ViewGroup): ItemDoctorsListBinding {
        val binding: ItemDoctorsListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_doctors_list, parent, false
        )
        binding.rvDoctorsList.setRecycledViewPool(viewPool)

        topSpecialistAdapter = TopSpecialistAdapter(appExecutors, itemCallback)
        subDoctorsListAdapter = SubDoctorsListAdapter(appExecutors, itemCallback)
        mergeAdapter = MergeAdapter(topSpecialistAdapter, subDoctorsListAdapter)

        //binding.rvDoctorsList.adapter = SubDoctorsListAdapter(appExecutors , itemCallback)
        binding.rvDoctorsList.adapter = mergeAdapter
        return binding
    }


    override fun bind(binding: ItemDoctorsListBinding, position: Int) {
        val item = getItem(position)
        binding.tvTitle.text = item.title
        if (position == 0) {
            //topSpecialistAdapter.submitList(item.listOfSubDoctors)
        } else {
            //subDoctorsListAdapter.submitList(item.listOfSubDoctors)
        }
        //(binding.rvDoctorsList.adapter as SubDoctorsListAdapter).submitList(item.listOfSubDoctors)
    }

    fun getSpecialistAdapter(): TopSpecialistAdapter {
        return topSpecialistAdapter
    }

    fun getDoctorsListAdapter(): SubDoctorsListAdapter {
        return subDoctorsListAdapter
    }

}
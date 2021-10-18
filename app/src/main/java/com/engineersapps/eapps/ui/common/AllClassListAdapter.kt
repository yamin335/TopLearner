package com.engineersapps.eapps.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.CommonListItemBinding
import com.engineersapps.eapps.models.registration.AcademicClass

class AllClassListAdapter internal constructor(
    private val callback: (AcademicClass) -> Unit
) : RecyclerView.Adapter<AllClassListAdapter.ViewHolder>() {

    private var dataList: ArrayList<AcademicClass> = ArrayList()

    private var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CommonListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_common, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun submitList(dataList: List<AcademicClass>) {
        this.dataList = dataList  as ArrayList<AcademicClass>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: CommonListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataList[position]
            binding.label = item.name

            if (selected == position) {
                binding.tickMark.visibility = View.VISIBLE
            } else {
                binding.tickMark.visibility = View.GONE
            }

            itemView.setOnClickListener {
                selected = absoluteAdapterPosition
                callback.invoke(item)
                notifyDataSetChanged()
            }
        }
    }
}
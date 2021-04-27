package com.rtchubs.engineerbooks.ui.my_course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rtchubs.engineerbooks.R
import kotlinx.android.synthetic.main.list_item_slider_indicator.view.*

class MyCourseSliderIndicatorAdapter internal constructor(
    private var noOfSlides: Int
) : RecyclerView.Adapter<MyCourseSliderIndicatorAdapter.SliderIndicatorViewHolder>() {

    private var checkedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderIndicatorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_slider_indicator, parent, false)
        return SliderIndicatorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderIndicatorViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return noOfSlides
    }

    fun setIndicatorAt(position: Int) {
        if (position in 0 until noOfSlides) {
            checkedPosition = position
            notifyDataSetChanged()
        }
    }

    inner class SliderIndicatorViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            if (checkedPosition == adapterPosition) {
                itemView.indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.accent))
            } else {
                itemView.indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.grey_8))
            }
        }
    }
}

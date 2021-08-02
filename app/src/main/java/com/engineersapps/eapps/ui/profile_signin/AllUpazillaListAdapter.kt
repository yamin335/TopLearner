package com.engineersapps.eapps.ui.profile_signin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.engineersapps.eapps.R
import com.engineersapps.eapps.models.registration.Upazilla
import kotlinx.android.synthetic.main.list_item_common.view.*
import java.util.*
import kotlin.collections.ArrayList

class AllUpazillaListAdapter internal constructor(
    private val callback: (Upazilla) -> Unit
) : RecyclerView.Adapter<AllUpazillaListAdapter.ViewHolder>(), Filterable {

    private var dataList: ArrayList<Upazilla> = ArrayList()
    private var filteredDataList: ArrayList<Upazilla> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_common,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredDataList[position])
    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    fun submitList(dataList: ArrayList<Upazilla>) {
        this.dataList = dataList
        this.filteredDataList = dataList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Upazilla) {
            itemView.name.text = item.name

            itemView.setOnClickListener {
                callback.invoke(item)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                var filteredList: MutableList<Upazilla> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList = dataList
                } else {
                    for (row in dataList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                            if (row.name?.toLowerCase(Locale.ROOT)?.contains(charSequence) == true ||
                                row.bnname?.toLowerCase(Locale.ROOT)?.contains(charSequence) == true) {
                                filteredList.add(row)
                            }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filterResults.values?.let {
                    filteredDataList = (it as? ArrayList<*>)?.filterIsInstance<Upazilla>() as ArrayList<Upazilla>? ?: ArrayList()
                    notifyDataSetChanged()
                }
            }
        }
    }
}
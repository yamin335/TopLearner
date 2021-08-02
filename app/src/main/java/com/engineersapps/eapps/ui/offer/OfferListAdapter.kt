package com.engineersapps.eapps.ui.offer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.engineersapps.eapps.AppExecutors
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.OfferListitemBinding
import com.engineersapps.eapps.models.Offer
import com.engineersapps.eapps.util.DataBoundListAdapter

class OfferListAdapter(
    appExecutors: AppExecutors,
    private var itemCallback: ((Offer) -> Unit)? = null
) : DataBoundListAdapter<Offer, OfferListitemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.udid == newItem.udid
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Offer,
            newItem: Offer
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup): OfferListitemBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_offer, parent, false
        )

    override fun bind(binding: OfferListitemBinding, position: Int) {
        val item = getItem(position)
        binding.offerAmount = "Offer Amount: ${item.offer_amount ?: 0}৳"
        val date = item.EndDate?.split("T")?.first() ?: "N/A"
        binding.offerExpiryDate = "Expiry Date: $date"
        binding.root.setOnClickListener {
            itemCallback?.invoke(item)
        }
    }
}
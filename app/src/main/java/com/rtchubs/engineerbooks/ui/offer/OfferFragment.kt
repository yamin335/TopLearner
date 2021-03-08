package com.rtchubs.engineerbooks.ui.offer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.OfferFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class OfferFragment : BaseFragment<OfferFragmentBinding, OfferViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_offer
    override val viewModel: OfferViewModel by viewModels { viewModelFactory }

    lateinit var offerListAdapter: OfferListAdapter

    lateinit var userData: InquiryAccount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)

        userData = preferencesHelper.getUser()

        offerListAdapter = OfferListAdapter(appExecutors) { notice ->
        }

        viewDataBinding.rvOfferList.adapter = offerListAdapter

        viewModel.offers.observe(viewLifecycleOwner, Observer {
            it?.let { offers ->
                offerListAdapter.submitList(offers)
                viewDataBinding.emptyView.visibility = View.GONE
            }
        })

        viewModel.getAllOffers(userData.CityID, userData.UpazilaID)
    }
}
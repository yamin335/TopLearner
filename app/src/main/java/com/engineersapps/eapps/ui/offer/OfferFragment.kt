package com.engineersapps.eapps.ui.offer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.OfferFragmentBinding
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.common.BaseFragment

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
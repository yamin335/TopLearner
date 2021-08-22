package com.engineersapps.eapps.ui.terms_and_conditions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.TermsFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment


class TermsFragment : BaseFragment<TermsFragmentBinding, TermsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_terms
    override val viewModel: TermsViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)
    }
}
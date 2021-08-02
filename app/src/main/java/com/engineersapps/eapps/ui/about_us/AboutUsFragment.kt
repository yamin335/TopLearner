package com.engineersapps.eapps.ui.about_us

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.AboutUsFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment

class AboutUsFragment : BaseFragment<AboutUsFragmentBinding, AboutUsViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_about_us
    override val viewModel: AboutUsViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)
    }
}
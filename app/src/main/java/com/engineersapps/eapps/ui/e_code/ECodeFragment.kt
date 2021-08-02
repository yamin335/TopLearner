package com.engineersapps.eapps.ui.e_code

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.ECodeFragmentBinding
import com.engineersapps.eapps.ui.common.BaseFragment

class ECodeFragment : BaseFragment<ECodeFragmentBinding, ECodeViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_e_code
    override val viewModel: ECodeViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)
    }
}
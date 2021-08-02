package com.engineersapps.eapps.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.engineersapps.eapps.R
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.databinding.SettingsBinding
import com.engineersapps.eapps.ui.common.BaseFragment

class SettingsFragment : BaseFragment<SettingsBinding, SettingsViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_settings
    override val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
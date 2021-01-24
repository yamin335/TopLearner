package com.rtchubs.engineerbooks.ui.e_code

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.ChapterListFragmentBinding
import com.rtchubs.engineerbooks.databinding.ECodeFragmentBinding
import com.rtchubs.engineerbooks.databinding.NoticeboardFragmentBinding
import com.rtchubs.engineerbooks.databinding.OfferFragmentBinding
import com.rtchubs.engineerbooks.services.DownloadService
import com.rtchubs.engineerbooks.ui.common.BaseFragment

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
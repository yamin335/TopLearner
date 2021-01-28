package com.rtchubs.engineerbooks.ui.notice_board

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
import com.rtchubs.engineerbooks.databinding.NoticeboardFragmentBinding
import com.rtchubs.engineerbooks.services.DownloadService
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class NoticeBoardFragment : BaseFragment<NoticeboardFragmentBinding, NoticeBoardViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_notice_board
    override val viewModel: NoticeBoardViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbar(viewDataBinding.toolbar)
    }
}
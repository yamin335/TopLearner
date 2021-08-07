package com.engineersapps.eapps.ui.notice_board

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.NoticeboardFragmentBinding
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.common.BaseFragment

class NoticeBoardFragment : BaseFragment<NoticeboardFragmentBinding, NoticeBoardViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_notice_board
    override val viewModel: NoticeBoardViewModel by viewModels { viewModelFactory }

    lateinit var noticeListAdapter: NoticeListAdapter

    lateinit var userData: InquiryAccount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)



        userData = preferencesHelper.getUser()

        noticeListAdapter = NoticeListAdapter(appExecutors) { notice ->
        }

        viewDataBinding.rvNoticeList.adapter = noticeListAdapter

        viewModel.allNotices.observe(viewLifecycleOwner, Observer {
            it?.let { notices ->
                noticeListAdapter.submitList(notices)
                viewDataBinding.emptyView.visibility = View.GONE
            }
        })

        viewModel.getAllNotices(userData.mobile)
    }
}
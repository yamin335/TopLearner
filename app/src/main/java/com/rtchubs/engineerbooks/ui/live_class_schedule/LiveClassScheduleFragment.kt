package com.rtchubs.engineerbooks.ui.live_class_schedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.LiveClassScheduleFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class LiveClassScheduleFragment : BaseFragment<LiveClassScheduleFragmentBinding, LiveClassScheduleViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_live_class_schedule
    override val viewModel: LiveClassScheduleViewModel by viewModels { viewModelFactory }

    lateinit var liveClassScheduleListAdapter: LiveClassScheduleListAdapter
    
    val args: LiveClassScheduleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar(viewDataBinding.toolbar)

        when(args.liveClassTypeID) {
            0 -> viewDataBinding.toolbar.title = "Next Classes"
            1 -> viewDataBinding.toolbar.title = "Previous Classes"
            2 -> viewDataBinding.toolbar.title = "Class Schedule"
        }

        liveClassScheduleListAdapter = LiveClassScheduleListAdapter(appExecutors) { notice ->
        }

        viewDataBinding.rvClassList.adapter = liveClassScheduleListAdapter

        viewModel.liveClassList.observe(viewLifecycleOwner, Observer {
            it?.let { notices ->
                liveClassScheduleListAdapter.submitList(notices)
                viewDataBinding.emptyView.visibility = View.GONE
            }
        })

        viewModel.getAllLiveClasses(args.liveClassTypeID)
    }
}
package com.rtchubs.engineerbooks.ui.live_video

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.LiveVideoFragmentBinding
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class LiveVideoFragment : BaseFragment<LiveVideoFragmentBinding, LiveVideoViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_live_video

    override val viewModel: LiveVideoViewModel by viewModels { viewModelFactory }

    private var drawerListener: NavDrawerHandlerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavDrawerHandlerCallback) {
            drawerListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        drawerListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewDataBinding.mPreviousClass.setOnClickListener {
            navigateTo(LiveVideoFragmentDirections.actionLiveFragmentToLiveClassScheduleFragment(1))
        }

        viewDataBinding.mClassSchedule.setOnClickListener {
            navigateTo(LiveVideoFragmentDirections.actionLiveFragmentToLiveClassScheduleFragment(2))
        }

        viewDataBinding.mNextClass.setOnClickListener {
            navigateTo(LiveVideoFragmentDirections.actionLiveFragmentToLiveClassScheduleFragment(0))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
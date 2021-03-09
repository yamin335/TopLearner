package com.rtchubs.engineerbooks.ui.live_video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.LiveVideoFragmentBinding
import com.rtchubs.engineerbooks.ui.LiveClassActivity
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.showWarningToast

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

        viewModel.liveClassList.observe(viewLifecycleOwner, Observer {
            it?.let { classes ->
                if (classes.isNotEmpty() && classes.first().link?.isNotBlank() == true) {
                    LiveClassActivity.videoUrl = classes.first().link ?: ""
                    startActivity(Intent(requireActivity(), LiveClassActivity::class.java))
                    viewModel.liveClassList.postValue(null)
                } else {
                    showWarningToast(requireContext(), "No live class ongoing...")
                }
            }
        })

        viewDataBinding.liveClass.setOnClickListener {
            viewModel.getAllLiveClasses(preferencesHelper.getUser().class_id)
        }

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewDataBinding.mPreviousClass.setOnClickListener {
            navigateTo(LiveVideoFragmentDirections.actionLiveFragmentToLiveClassScheduleFragment(0))
        }

        viewDataBinding.mClassSchedule.setOnClickListener {
            navigateTo(LiveVideoFragmentDirections.actionLiveFragmentToLiveClassScheduleFragment(2))
        }

        viewDataBinding.mNextClass.setOnClickListener {
            navigateTo(LiveVideoFragmentDirections.actionLiveFragmentToLiveClassScheduleFragment(1))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
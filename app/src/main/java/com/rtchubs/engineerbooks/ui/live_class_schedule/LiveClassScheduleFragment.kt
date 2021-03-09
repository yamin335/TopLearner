package com.rtchubs.engineerbooks.ui.live_class_schedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.LiveClassScheduleFragmentBinding
import com.rtchubs.engineerbooks.models.LiveClassSchedule
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

        val classType = args.liveClassTypeID

        when(classType) {
            0 -> viewDataBinding.toolbar.title = "আগের ক্লাস"
            1 -> viewDataBinding.toolbar.title = "পরবর্তী ক্লাস"
            2 -> viewDataBinding.toolbar.title = "ক্লাস শিডিউল"
        }

        liveClassScheduleListAdapter = LiveClassScheduleListAdapter(appExecutors) { notice ->
        }

        viewDataBinding.rvClassList.adapter = liveClassScheduleListAdapter

        viewModel.liveClassList.observe(viewLifecycleOwner, Observer {
            it?.let { classes ->
                val classSchedules = ArrayList<LiveClassSchedule>()
                classes.forEach { classSchedule ->
                    when {
                        classType == 0 && classSchedule.archived == true -> {
                            classSchedules.add(classSchedule)
                        }
                        classType == 1 && classSchedule.archived == false -> {
                            classSchedules.add(classSchedule)
                        }
                        classType == 2 && classSchedule.is_live == true -> {
                            classSchedules.add(classSchedule)
                        }
                    }
                }
                liveClassScheduleListAdapter.submitList(classSchedules)
                viewDataBinding.emptyView.visibility = if (classSchedules.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        viewModel.getAllLiveClasses(preferencesHelper.getUser().class_id)
    }
}
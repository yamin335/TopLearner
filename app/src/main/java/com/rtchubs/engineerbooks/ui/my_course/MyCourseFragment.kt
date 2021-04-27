package com.rtchubs.engineerbooks.ui.my_course

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.MyCourseFragmentBinding
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class MyCourseFragment : BaseFragment<MyCourseFragmentBinding, MyCourseViewModel>() {
    companion object {
        var allBookList = ArrayList<ClassWiseBook>()
    }
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_my_course
    override val viewModel: MyCourseViewModel by viewModels {
        viewModelFactory
    }

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var userData: InquiryAccount

    private lateinit var myCourseListAdapter: MyCourseSliderAdapter
    private lateinit var myCourseSliderIndicatorAdapter: MyCourseSliderIndicatorAdapter
    private lateinit var sliderPageChangeCallback: SliderPageChangeCallback

    private var totalCourse = 0
    private var currentCourse = 0

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

        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            requireActivity().finishAffinity()
            requireActivity().finish()
        }

        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userData = preferencesHelper.getUser()

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        myCourseListAdapter = MyCourseSliderAdapter()
        myCourseSliderIndicatorAdapter = MyCourseSliderIndicatorAdapter(totalCourse)

        viewDataBinding.indicatorView.adapter = myCourseSliderIndicatorAdapter

        sliderPageChangeCallback = SliderPageChangeCallback {
            myCourseSliderIndicatorAdapter.setIndicatorAt(it)
            currentCourse = it
            if (currentCourse + 1 == totalCourse) {
                viewDataBinding.btnNext.visibility = View.INVISIBLE
            } else {
                viewDataBinding.btnNext.visibility = View.VISIBLE
            }

            if (currentCourse == 0) {
                viewDataBinding.btnPrevious.visibility = View.INVISIBLE
            } else {
                viewDataBinding.btnPrevious.visibility = View.VISIBLE
            }
        }

        viewDataBinding.sliderView.apply {
            adapter = myCourseListAdapter
            registerOnPageChangeCallback(sliderPageChangeCallback)
            isUserInputEnabled = true
        }
//        {
//            if (userData.customer_type_id == 2) {
//                navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(it))
//            } else {
//                if (!preferencesHelper.isDeviceTimeChanged) {
//
//                    if (it.price ?: 0.0 > 0.0) {
//                        val paidBook = preferencesHelper.getPaidBook()
//                        if (paidBook.isPaid && paidBook.classID == userData.class_id) {
//                            navigateTo(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(it))
//                        } else {
//                            val book = PaidBook(it.id, it.name, userData.class_id, userData.ClassName, false, it.price ?: 0.0)
//                            navigateTo(Home2FragmentDirections.actionHome2FragmentToPaymentFragment(book))
//                        }
//                    } else {
//                        navigateTo(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(it))
//                    }
//                } else {
//                    if (isTimeAndZoneAutomatic(context)) {
//                        if (checkNetworkStatus(true)) {
//                            preferencesHelper.isDeviceTimeChanged = false
//                            homeClassListAdapter?.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)
//
//                            if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
//                                preferencesHelper.isDeviceTimeChanged = true
//                                homeClassListAdapter?.setTimeChangeStatus(true)
//                            }
//                            viewModel.getAcademicBooks(userData.mobile ?: "", userData.class_id ?: 0)
//                        }
//                    } else {
//                        showWarningToast(requireContext(), "Please auto adjust your device time!")
//                    }
//                }
//            }
//        }

        viewModel.allBooksFromDB.observe(viewLifecycleOwner, Observer { books ->
            books?.let {
                val temp = it.filter { item -> item.price ?: 0.0 > 0.0  }
                allBookList = temp as ArrayList<ClassWiseBook>
                totalCourse = allBookList.size
                myCourseSliderIndicatorAdapter = MyCourseSliderIndicatorAdapter(totalCourse)
                viewDataBinding.indicatorView.adapter = myCourseSliderIndicatorAdapter
                myCourseListAdapter.submitList(allBookList)
            }
        })

        viewDataBinding.btnNext.setOnClickListener {
            viewDataBinding.btnPrevious.visibility = View.VISIBLE
            if (++currentCourse < totalCourse) {
                viewDataBinding.sliderView.setCurrentItem(currentCourse, true)
                if (currentCourse + 1 == totalCourse) {
                    viewDataBinding.btnNext.visibility = View.INVISIBLE
                }
            }
        }

        viewDataBinding.btnPrevious.setOnClickListener {
            viewDataBinding.btnNext.visibility = View.VISIBLE
            if (--currentCourse >= 0) {
                viewDataBinding.sliderView.setCurrentItem(currentCourse, true)
                if (currentCourse == 0) {
                    viewDataBinding.btnPrevious.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.allBooks.observe(viewLifecycleOwner, Observer { books ->
            books?.let {
                if (it.isNotEmpty()) {
                    viewModel.saveBooksInDB(it)
                }
            }
        })

        if (userData.customer_type_id == 2) {
            viewModel.getAdminPanelBooks()
        } else {
            viewModel.getAcademicBooks(userData.mobile ?: "", userData.class_id ?: 0)
        }
    }

    inner class SliderPageChangeCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            listener.invoke(position)
        }
    }
}
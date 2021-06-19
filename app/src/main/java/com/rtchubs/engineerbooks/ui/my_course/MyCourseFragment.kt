package com.rtchubs.engineerbooks.ui.my_course

import android.content.Context
import android.content.SharedPreferences
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
import com.rtchubs.engineerbooks.models.home.Course
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.prefs.AppPreferencesHelper
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.isTimeAndZoneAutomatic
import com.rtchubs.engineerbooks.util.showWarningToast

class MyCourseFragment : BaseFragment<MyCourseFragmentBinding, MyCourseViewModel>() {
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
    var allCourseList = HashMap<Int?, Course?>()
    var allMyCourseBookIds = ArrayList<Int>()

    var timeChangeListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            AppPreferencesHelper.KEY_DEVICE_TIME_CHANGED -> {
                myCourseListAdapter.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)

                if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
                    //preferencesHelper.isDeviceTimeChanged = true
                    myCourseListAdapter.setTimeChangeStatus(true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)

        myCourseListAdapter.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
            myCourseListAdapter.setTimeChangeStatus(true)
        }
        viewModel.getMyCourses(userData.mobile)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavDrawerHandlerCallback) {
            drawerListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }

        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
        }
    }

    override fun onDetach() {
        super.onDetach()
        drawerListener = null
        preferencesHelper.preference.unregisterOnSharedPreferenceChangeListener(timeChangeListener)
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

        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)

        userData = preferencesHelper.getUser()

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        myCourseListAdapter = MyCourseSliderAdapter(userData.customer_type_id) {
            val book = ClassWiseBook(it.id, it.udid,
                it.name, it.title, it.author, it.isPaid,
                it.book_type_id, it.price, it.status, it.logo)
            if (userData.customer_type_id == 2) {
                navController.navigate(MyCourseFragmentDirections.actionMyCourseFragmentToChapterNav(book))
            } else {
                if (!preferencesHelper.isDeviceTimeChanged) {
                    navigateTo(MyCourseFragmentDirections.actionMyCourseFragmentToChapterNav(book))
//                    if (it.price ?: 0.0 > 0.0) {
//                        val paidBook = preferencesHelper.getPaidBook()
//                        if (paidBook.isPaid && paidBook.classID == userData.class_id) {
//                            navigateTo(MyCourseFragmentDirections.actionMyCourseFragmentToChapterNav(it))
//                        } else {
////                            val book = PaidBook(it.id, it.name, userData.class_id, userData.ClassName, false, it.price ?: 0.0)
////                            navigateTo(MyCourseFragmentDirections.actionMyCourseFragmentToChapterNav(it))
//                            showWarningToast(requireContext(), "Please pay first!")
//                        }
//                    } else {
//                        navigateTo(MyCourseFragmentDirections.actionMyCourseFragmentToChapterNav(it))
//                    }
                } else {
                    if (isTimeAndZoneAutomatic(context)) {
                        if (checkNetworkStatus(true)) {
                            preferencesHelper.isDeviceTimeChanged = false
                            myCourseListAdapter.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)
                            if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
                                preferencesHelper.isDeviceTimeChanged = true
                                myCourseListAdapter.setTimeChangeStatus(true)
                            }
                            viewModel.getMyCourses(userData.mobile)
                        }
                    } else {
                        showWarningToast(requireContext(), "Please auto adjust your device time!")
                    }
                }
            }
        }
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

        myCourseListAdapter.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
            myCourseListAdapter.setTimeChangeStatus(true)
        }

//        val paidBook = preferencesHelper.getPaidBook()
//        if (paidBook.isPaid && paidBook.classID == userData.class_id) {
//            myCourseListAdapter.setPaymentStatus(paidBook.isPaid)
//        } else {
//            myCourseListAdapter.setPaymentStatus(false)
//        }

        viewDataBinding.sliderView.apply {
            adapter = myCourseListAdapter
            registerOnPageChangeCallback(sliderPageChangeCallback)
            isUserInputEnabled = true
        }

//        viewModel.allBooksFromDB.observe(viewLifecycleOwner, Observer { books ->
//            books?.let {
//
//                myCourseListAdapter.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)
//
//                if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
//                    preferencesHelper.isDeviceTimeChanged = true
//                    myCourseListAdapter.setTimeChangeStatus(true)
//                }
//
//                var isBookPaid = false
//                val book = preferencesHelper.getPaidBook()
////                if (book.isPaid && book.classID == userData.class_id) {
////                    myCourseListAdapter.setPaymentStatus(book.isPaid)
////                    isBookPaid = book.isPaid
////                } else {
////                    myCourseListAdapter.setPaymentStatus(false)
////                }
//
//                if (book.isPaid) {
//                    myCourseListAdapter.setPaymentStatus(book.isPaid)
//                    isBookPaid = book.isPaid
//                } else {
//                    myCourseListAdapter.setPaymentStatus(false)
//                }
//
//                val temp = it.filter { item -> item.price ?: 0.0 > 0.0 && isBookPaid }
//                allBookList = temp as ArrayList<ClassWiseBook>
//                totalCourse = allBookList.size
//                myCourseSliderIndicatorAdapter = MyCourseSliderIndicatorAdapter(totalCourse)
//                viewDataBinding.indicatorView.adapter = myCourseSliderIndicatorAdapter
//                myCourseListAdapter.submitList(allBookList)
//            }
//            viewDataBinding.emptyView.visibility = if (allBookList.isEmpty()) View.VISIBLE else View.GONE
//            viewDataBinding.footer.visibility = if (allBookList.isNotEmpty()) View.VISIBLE else View.GONE
//        })

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

        viewModel.allCourseCategoriesFromDB.observe(viewLifecycleOwner, Observer {
            val courses = HashMap<Int?, Course?>()
            CourseFilteringForLoop@ for (courseCategory in it) {
                val courseList = courseCategory.courses ?: continue@CourseFilteringForLoop
                for (course in courseList) {
                    courses[course.id] = course
                }
            }
            allCourseList = courses
        })

        viewModel.allMyCourseBooksFromDB.observe(viewLifecycleOwner, Observer { books ->
            books?.let {
                if (it.isNotEmpty()) {
                    totalCourse = it.size
                    myCourseSliderIndicatorAdapter = MyCourseSliderIndicatorAdapter(totalCourse)
                    viewDataBinding.indicatorView.adapter = myCourseSliderIndicatorAdapter
                    myCourseListAdapter.submitList(it)

                    allMyCourseBookIds.clear()
                    for (book in it) {
                        allMyCourseBookIds.add(book.id)
                    }
                }
            }
            viewDataBinding.emptyView.visibility = if (books == null || books.isEmpty()) View.VISIBLE else View.GONE
            viewDataBinding.footer.visibility = if (books != null && books.isNotEmpty()) View.VISIBLE else View.GONE
        })

        viewModel.myCourses.observe(viewLifecycleOwner, Observer {
            it?.let { courses ->
                myCourseListAdapter.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)

                if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
                    preferencesHelper.isDeviceTimeChanged = true
                    myCourseListAdapter.setTimeChangeStatus(true)
                }

//                var isBookPaid = false
//                val book = preferencesHelper.getPaidBook()
//                if (book.isPaid && book.classID == userData.class_id) {
//                    myCourseListAdapter.setPaymentStatus(book.isPaid)
//                    isBookPaid = book.isPaid
//                } else {
//                    myCourseListAdapter.setPaymentStatus(false)
//                }
                myCourseListAdapter.setPaymentStatus(true)
//                if (book.isPaid) {
//                    myCourseListAdapter.setPaymentStatus(book.isPaid)
//                    isBookPaid = book.isPaid
//                } else {
//                    myCourseListAdapter.setPaymentStatus(false)
//                }

//                val temp = it.filter { item -> item.price ?: 0.0 > 0.0 && isBookPaid }
//                allBookList = temp as ArrayList<ClassWiseBook>

                val allPaidBooksIds = ArrayList<Int?>()
                for (paidCourse in courses) {
                    if (allCourseList.containsKey(paidCourse.course_id)) {
                        allPaidBooksIds.add(allCourseList[paidCourse.course_id]?.book_id)
                    }
                }

                for (id in allPaidBooksIds) {
                    if (!allMyCourseBookIds.contains(id)) viewModel.getMyCourseBook(id)
                }
            }
        })
    }

    inner class SliderPageChangeCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            listener.invoke(position)
        }
    }
}
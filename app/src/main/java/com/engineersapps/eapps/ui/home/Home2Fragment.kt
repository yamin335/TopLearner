package com.engineersapps.eapps.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.databinding.HomeFragment2Binding
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.models.home.Course
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.prefs.AppPreferencesHelper
import com.engineersapps.eapps.ui.LogoutHandlerCallback
import com.engineersapps.eapps.ui.NavDrawerHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.util.isTimeAndZoneAutomatic

class Home2Fragment : BaseFragment<HomeFragment2Binding, HomeViewModel>() {
    companion object {
        var allBookList = ArrayList<ClassWiseBook>()
        var allCourseList = HashMap<Int?, Course?>()
    }
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_main2
    override val viewModel: HomeViewModel by viewModels {
        viewModelFactory
    }

    private var listener: LogoutHandlerCallback? = null

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var userData: InquiryAccount

    private var homeClassListAdapter: HomeClassListAdapter? = null

    private lateinit var courseCategoryListAdapter: CourseCategoryListAdapter

    var timeChangeListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            AppPreferencesHelper.KEY_DEVICE_TIME_CHANGED -> {
                homeClassListAdapter?.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)

                if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
                    //preferencesHelper.isDeviceTimeChanged = true
                    homeClassListAdapter?.setTimeChangeStatus(true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)

        homeClassListAdapter?.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
            homeClassListAdapter?.setTimeChangeStatus(true)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }

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
        listener = null
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

        viewModel.getAllCourses()
        viewModel.getAllFreeBooks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)

        userData = preferencesHelper.getUser()

        //registerToolbar(viewDataBinding.toolbar)

//        viewDataBinding.cardTopUp.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToTopUpMobileFragment(
//                TopUpHelper()
//            ))
//        }
//
//        val token = preferencesHelper.getAccessTokenHeader()
//
//        paymentListAdapter = PaymentMethodListAdapter(appExecutors) {
//            //navController.navigate(HomeFragmentDirections.actionBooksToChapterList(it))
//        }
//
//
//

        courseCategoryListAdapter = CourseCategoryListAdapter(appExecutors) {
            CourseDetailsFragment.course = it
            navigateTo(Home2FragmentDirections.actionHome2FragmentToCourseDetailsFragment())
        }

        viewDataBinding.courseRecycler.adapter = courseCategoryListAdapter

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer {
            viewDataBinding.swipeRefresh.isRefreshing = it == ApiCallStatus.LOADING
        })

        viewModel.allCourseCategoriesFromDB.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
            }
            courseCategoryListAdapter.submitList(it)
            val courses = HashMap<Int?, Course?>()
            CourseFilteringForLoop@ for (courseCategory in it) {
                val courseList = courseCategory.courses ?: continue@CourseFilteringForLoop
                for (course in courseList) {
                    courses[course.id] = course
                }
            }
            allCourseList = courses
        })

        viewDataBinding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllCourses()
            viewModel.getAllFreeBooks()
            viewModel.getAcademicClass()
        }

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

//        homeClassListAdapter = HomeClassListAdapter(appExecutors, userData.customer_type_id) {
//            if (userData.customer_type_id == 2) {
//                navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterNav(it))
//            } else {
//                if (!preferencesHelper.isDeviceTimeChanged) {
//
//                    if (it.price ?: 0.0 > 0.0) {
//                        val paidBook = preferencesHelper.getPaidBook()
//                        if (paidBook.isPaid && paidBook.classID == userData.class_id) {
//                            navigateTo(Home2FragmentDirections.actionHome2FragmentToChapterNav(it))
//                        } else {
//                            val book = PaidBook(it.id, it.name, userData.class_id, userData.ClassName, false, it.price ?: 0.0)
//                            navigateTo(Home2FragmentDirections.actionHome2FragmentToPaymentFragment(book))
//                        }
//                    } else {
//                        navigateTo(Home2FragmentDirections.actionHome2FragmentToChapterNav(it))
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
//
//        viewModel.allBooksFromDB.observe(viewLifecycleOwner, Observer { books ->
//            books?.let {
////                val tempList = ArrayList<ClassWiseBook>()
////                var i = 1
////                it.forEach { book ->
////                    book.id = i++
////                    tempList.add(book)
////                }
////                allBookList = tempList
//                allBookList = it as ArrayList<ClassWiseBook>
//                homeClassListAdapter?.submitList(allBookList)
//
//                homeClassListAdapter?.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)
//
//                if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
//                    preferencesHelper.isDeviceTimeChanged = true
//                    homeClassListAdapter?.setTimeChangeStatus(true)
//                }
//
//                val book = preferencesHelper.getPaidBook()
//                if (book.isPaid && book.classID == userData.class_id) {
//                    homeClassListAdapter?.setPaymentStatus(book.isPaid)
//                } else {
//                    homeClassListAdapter?.setPaymentStatus(false)
//                }
////                homeClassListAdapter.submitList(tempList)
//
//                val list = arrayListOf(CourseCategory(1, "সপ্তম শ্রেণী", allBookList), CourseCategory(2, "অষ্টম শ্রেণী",allBookList), CourseCategory(3, "নবম শ্রেণী",allBookList))
//                courseCategoryListAdapter.submitList(list)
//            }
//            viewDataBinding.emptyView.visibility = if (allBookList.isEmpty()) View.VISIBLE else View.GONE
//        })
//
//        homeClassListAdapter?.setTimeChangeStatus(preferencesHelper.isDeviceTimeChanged)
//
//        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
//            preferencesHelper.isDeviceTimeChanged = true
//            homeClassListAdapter?.setTimeChangeStatus(true)
//        }

//        val paidBook = preferencesHelper.getPaidBook()
//        if (paidBook.isPaid && paidBook.classID == userData.class_id) {
//            homeClassListAdapter?.setPaymentStatus(paidBook.isPaid)
//        } else {
//            homeClassListAdapter?.setPaymentStatus(false)
//        }

//        viewDataBinding.homeClassListRecycler.adapter = homeClassListAdapter
//        viewDataBinding.homeClassListRecycler.adapter = courseCategoryListAdapter
//
//        viewModel.allBooks.observe(viewLifecycleOwner, Observer { books ->
//            books?.let {
//                if (it.isNotEmpty()) {
//                    viewModel.saveBooksInDB(it)
//                }
//            }
//        })
//        viewModel.getAcademicBooks(userData.mobile ?: "", userData.class_id ?: 0)

//        if (userData.customer_type_id == 2) {
//            viewModel.getAdminPanelBooks()
//        } else {
//            viewModel.getAcademicBooks(userData.mobile ?: "", userData.class_id ?: 0)
//        }
//
//        viewModel.getAds()

//        viewModel.slideDataList.forEach { slideData ->
//            val slide = SliderView(requireContext())
//            slide.sliderTextTitle = slideData.textTitle
//            slide.sliderTextDescription = slideData.descText
//            slide.sliderImage(slideData.slideImage)
//            viewDataBinding.sliderLayout.addSlider(slide)
//        }
//
//        viewDataBinding.item1.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book1))
//        }
//
//        viewDataBinding.item2.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book2))
//        }
//
//        viewDataBinding.item3.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book3))
//        }
//
//        viewDataBinding.item4.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book4))
//        }
//
//        viewDataBinding.item5.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book5))
//        }
//
//        viewDataBinding.item6.setOnClickListener {
//            navController.navigate(Home2FragmentDirections.actionHome2FragmentToMoreBookListFragment())
//        }
//
//        Log.e("res", preferencesHelper.getAccessTokenHeader())
//        paymentListAdapter.submitList(viewModel.paymentMethodList)
//        viewDataBinding.recyclerPaymentMethods.adapter = paymentListAdapter
//
//
//
//        paymentListAdapter.onClicked.observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//                if (it.id == "-1") {
//                    /**
//                     * add payment method
//                     */
//                    val action = Home2FragmentDirections.actionHome2FragmentToAddPaymentMethodsFragment()
//                    navController.navigate(action)
//                }
//            }
//        })
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.toolbar_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
}
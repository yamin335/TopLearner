package com.rtchubs.engineerbooks.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiCallStatus
import com.rtchubs.engineerbooks.databinding.HomeFragment2Binding
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.prefs.AppPreferencesHelper
import com.rtchubs.engineerbooks.ui.LogoutHandlerCallback
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.isTimeAndZoneAutomatic

class Home2Fragment : BaseFragment<HomeFragment2Binding, HomeViewModel>() {
    companion object {
        var allBookList = ArrayList<ClassWiseBook>()
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

        viewModel.allCourseCategoryList.observe(viewLifecycleOwner, Observer { courseCategories ->
            viewModel.saveCourseCategoriesInDB(courseCategories ?: ArrayList())
        })

        viewModel.getAllCourses()
        viewModel.getAllFreeBooks()
        viewModel.getAcademicClass()

        viewModel.allCourseCategoriesFromDB.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
            }
            courseCategoryListAdapter.submitList(it)
        })


//        courseCategoryListAdapter = CourseCategoryListAdapter(appExecutors) {
//            navigateTo(Home2FragmentDirections.actionHome2FragmentToCourseDetailsFragment())
//        }
//
//        viewModel.slideDataList.observe(viewLifecycleOwner, Observer {
//            it?.let { ads ->
//                ads.forEach { slideData ->
//                    val slide = SliderView(slideData, requireContext())
//                    viewDataBinding.sliderLayout.addSlider(slide)
//                }
//            }
//        })
//
//        // set Slider Transition Animation
//        viewDataBinding.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default)
//        viewDataBinding.sliderLayout.startAutoCycle()

//        val chapterList = listOf(
//            Chapter(1, "Chapter One", null, null),
//            Chapter(2, "Chapter Two", null, null),
//            Chapter(3, "Chapter Three", null, null),
//            Chapter(4, "Chapter Four", null, null),
//            Chapter(5, "Chapter Five", null, null),
//            Chapter(6, "Chapter Six", null, null),
//            Chapter(7, "Chapter Seven", null, null),
//            Chapter(8, "Chapter Eight", null, null),
//            Chapter(9, "Chapter Nine", null, null),
//            Chapter(10, "Chapter Ten", null, null)
//        )
//
//        val bookList = listOf(
//            Book(1, "Class One", "", chapterList),
//            Book(1, "Class Two", "", chapterList),
//            Book(1, "Class Three", "", chapterList),
//            Book(1, "Class Four", "", chapterList),
//            Book(1, "Class Five", "", chapterList),
//            Book(1, "Class Six", "", chapterList),
//            Book(1, "Class Seven", "", chapterList),
//            Book(1, "Class Eight", "", chapterList),
//            Book(1, "Class Nine", "", chapterList),
//            Book(1, "Class Ten", "", chapterList),
//            Book(1, "HSC 1st Year", "", chapterList),
//            Book(1, "HSC 2nd Year", "", chapterList)
//        )

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
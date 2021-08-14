package com.engineersapps.eapps.ui.my_course

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.MyCourseFragmentBinding
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.models.my_course.MyCourse
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.prefs.AppPreferencesHelper
import com.engineersapps.eapps.ui.NavDrawerHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.home.Home2Fragment.Companion.allCourseList
import com.engineersapps.eapps.ui.payment.PaymentFragment
import com.engineersapps.eapps.util.isTimeAndZoneAutomatic
import com.engineersapps.eapps.util.showErrorToast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

        myCourseListAdapter = MyCourseSliderAdapter(userData.customer_type_id, itemCallback = { myCourse ->
            // First checks if the course is outdated or not
            var expireDate = Date()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            try {
                expireDate = dateFormat.parse(myCourse.expiredate ?: "") ?: return@MyCourseSliderAdapter
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val date = Calendar.getInstance()
            date.time = expireDate
            date[Calendar.DATE] = date[Calendar.DATE] + 1
            expireDate = date.time
            val isCourseOutDated = Date().before(expireDate)
            //val isCourseOutDated = expireDate.before(Date())

            if (!isCourseOutDated) {
                showErrorToast(requireContext(), "কোর্সের মেয়াদ শেষ!, কোর্সটি পুনরায় চালু করতে পেমেন্ট করুন")
            } else {
                val bookId = myCourse.book_id ?: return@MyCourseSliderAdapter
                viewModel.getMyCourseBookFromDB(bookId).observe(viewLifecycleOwner, Observer {
                    val book = ClassWiseBook(it.id, it.udid,
                        it.name, it.title, it.author, it.isPaid,
                        it.book_type_id, it.price, it.status, it.logo)
                    navigateTo(MyCourseFragmentDirections.actionMyCourseFragmentToChapterNav(book.id, book.title))
                })
            }
        }, paymentCallback = { myCourse ->
            val course = allCourseList[myCourse.course_id]
            PaymentFragment.firstPackageTitle = course?.first_payment_title ?: ""
            PaymentFragment.secondPackageTitle = course?.second_payment_title ?: ""
            PaymentFragment.thirdPackageTitle = course?.third_payment_title ?: ""

            PaymentFragment.firstPackagePrice = course?.first_payment_amount ?: 0
            PaymentFragment.secondPackagePrice = course?.second_payment_amount ?: 0
            PaymentFragment.thirdPackagePrice = course?.third_payment_amount ?: 0

            val firstDuration = course?.first_duration ?: "0"
            val secondDuration = course?.second_duration ?: "0"
            val thirdDuration = course?.third_duration ?: "0"
            PaymentFragment.first_duration = firstDuration.toInt()
            PaymentFragment.second_duration = secondDuration.toInt()
            PaymentFragment.third_duration = thirdDuration.toInt()

            val totalPrice = course?.price ?: 0
            val totalDiscount = course?.discount_price ?: 0

            val price = if (totalDiscount != 0) {
                val discountedPrice = totalPrice - totalDiscount
                discountedPrice.toString()
            } else {
                totalPrice.toString()
            }

            navigateTo(
                MyCourseFragmentDirections.actionMyCourseFragmentToPaymentNav(
                    course?.book_id ?: 0, course?.title, course?.id ?:0, price, course?.title ?: "", ""))
        })
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

        viewDataBinding.sliderView.apply {
            adapter = myCourseListAdapter
            registerOnPageChangeCallback(sliderPageChangeCallback)
            isUserInputEnabled = true
        }

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

        viewModel.allMyCourseBooksFromDB.observe(viewLifecycleOwner, Observer { books ->
            books?.let {
                if (it.isNotEmpty()) {
                    allMyCourseBookIds.clear()
                    for (book in it) {
                        allMyCourseBookIds.add(book.id)
                    }
                }
            }
        })

        viewModel.allMyCoursesFromDB.observe(viewLifecycleOwner, Observer {
            it?.let { myCourses ->
                val courses = ArrayList<MyCourse>()
                for (course in myCourses) {
                    if (allCourseList.containsKey(course.course_id)) {
                        val tempCourse = allCourseList[course.course_id]
                        course.title = tempCourse?.title
                        course.logo = tempCourse?.logourl
                        course.book_id = tempCourse?.book_id
                        //course.endtime = tempCourse?.endtime
                        courses.add(course)
                    }
                }

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

                totalCourse = courses.size
                myCourseSliderIndicatorAdapter = MyCourseSliderIndicatorAdapter(totalCourse)
                viewDataBinding.indicatorView.adapter = myCourseSliderIndicatorAdapter
                myCourseListAdapter.submitList(courses)

                viewDataBinding.emptyView.visibility = if (courses.isEmpty()) View.VISIBLE else View.GONE
                viewDataBinding.footer.visibility = if (courses.isNotEmpty()) View.VISIBLE else View.GONE
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
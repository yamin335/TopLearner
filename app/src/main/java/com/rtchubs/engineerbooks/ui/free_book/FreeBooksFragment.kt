package com.rtchubs.engineerbooks.ui.free_book

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.daimajia.slider.library.SliderLayout
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.FreeBooksFragmentBinding
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.login.SliderView

class FreeBooksFragment : BaseFragment<FreeBooksFragmentBinding, FreeBooksViewModel>() {
    companion object {
        var allBookList = ArrayList<ClassWiseBook>()
    }
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_free_books
    override val viewModel: FreeBooksViewModel by viewModels {
        viewModelFactory
    }

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var userData: InquiryAccount

    private var freeBookListAdapter: FreeBookListAdapter? = null

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
        viewModel.slideDataList.observe(viewLifecycleOwner, Observer {
            it?.let { ads ->
                ads.forEach { slideData ->
                    val slide = SliderView(slideData, requireContext())
                    viewDataBinding.sliderLayout.addSlider(slide)
                }
            }
        })

        // set Slider Transition Animation
        viewDataBinding.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default)
        viewDataBinding.sliderLayout.startAutoCycle()

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        freeBookListAdapter = FreeBookListAdapter(appExecutors) {
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
        }

        viewModel.allBooksFromDB.observe(viewLifecycleOwner, Observer { books ->
            books?.let {
                val temp = it.filter { item -> item.price ?: 0.0 <= 0.0 }
                allBookList = temp as ArrayList<ClassWiseBook>
                freeBookListAdapter?.submitList(allBookList)
            }
        })

        viewDataBinding.homeClassListRecycler.adapter = freeBookListAdapter

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

        viewModel.getAds()

    }
}
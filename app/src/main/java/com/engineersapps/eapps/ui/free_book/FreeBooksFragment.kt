package com.engineersapps.eapps.ui.free_book

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.daimajia.slider.library.SliderLayout
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.databinding.FreeBooksFragmentBinding
import com.engineersapps.eapps.models.AdSlider
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.NavDrawerHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.login.SliderView

class FreeBooksFragment : BaseFragment<FreeBooksFragmentBinding, FreeBooksViewModel>() {
    companion object {
        private var allClass = ArrayList<AcademicClass>()
        private var adBanners = ArrayList<AdSlider>()
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

    private var titleClassList = arrayOf("--Select Class--")
    lateinit var classAdapter: ArrayAdapter<String>

    override fun onResume() {
        super.onResume()
//        if (userData.customer_type_id == 2) {
//            viewDataBinding.linearClass.visibility = View.GONE
//            viewModel.getAdminPanelBooks()
//        } else {
//            viewDataBinding.linearClass.visibility = View.VISIBLE
//            viewModel.getAcademicBooks(userData.mobile ?: "", viewModel.selectedClassId?.toInt() ?: 0)
//        }
        viewModel.getAcademicClass()
        //viewModel.getAcademicBooks(userData.mobile ?: "", viewModel.selectedClassId?.toInt() ?: 0)
    }

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
        userData = preferencesHelper.getUser()
        viewModel.selectedClassId = userData.class_id ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.slideDataList.observe(viewLifecycleOwner, Observer {
            adBanners = it as ArrayList<AdSlider>? ?: ArrayList()
            viewDataBinding.sliderLayout.removeAllSliders()
            adBanners.forEach { slideData ->
                val slide = SliderView(slideData, requireContext())
                viewDataBinding.sliderLayout.addSlider(slide)
            }
        })

        // set Slider Transition Animation
        viewDataBinding.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default)
        viewDataBinding.sliderLayout.startAutoCycle()

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        freeBookListAdapter = FreeBookListAdapter(appExecutors) {
            navController.navigate(FreeBooksFragmentDirections.actionFreeBooksFragmentToChapterNav(it.id, it.title))
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
        viewDataBinding.homeClassListRecycler.adapter = freeBookListAdapter

        viewModel.allFreeBooksFromDB.observe(viewLifecycleOwner, Observer { books ->
            if (books.isNullOrEmpty()) {
                freeBookListAdapter?.submitList(books as ArrayList<ClassWiseBook>)
            } else {
                val temp = books.filter { item -> item.price ?: 0.0 <= 0.0 }
                freeBookListAdapter?.submitList(temp as ArrayList<ClassWiseBook>)
                viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
            }
        })

        viewModel.allBooks.observe(viewLifecycleOwner, Observer { books ->
            viewModel.saveBooksInDB(books ?: ArrayList())
//            lifecycleScope.launch {
//                delay()
//            }
        })

        
        val tempClass = Array(allClass.size + 1) {""}
        tempClass[0] = "--Select Class--"
        allClass.forEachIndexed { index, academicClass ->
            tempClass[index + 1] = academicClass.name ?: "Unknown"
        }
        titleClassList = tempClass
        classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleClassList)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spClass.adapter = classAdapter

        viewDataBinding.spClass.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    try {
                        if (allClass.isNotEmpty()) {
                            viewModel.selectedClassId = allClass[position - 1].id?.toInt() ?: 0
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    viewModel.selectedClassId = userData.class_id ?: 0
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        var academicClassIndex = 0
        for (index in allClass.indices) {
            val academicClass = allClass[index]
            if (academicClass.id != null && academicClass.id == viewModel.selectedClassId.toString()) {
                academicClassIndex = index + 1
            }
        }
        viewDataBinding.spClass.setSelection(academicClassIndex, true)

        viewModel.allAcademicClass.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                val temp = Array(it.size + 1) {""}
                temp[0] = "--Select Class--"
                it.forEachIndexed { index, academicClass ->
                    temp[index + 1] = academicClass.name ?: "Unknown"
                }
                titleClassList = temp
                classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleClassList)
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                viewDataBinding.spClass.adapter = classAdapter
                allClass = it as ArrayList<AcademicClass>

                academicClassIndex = 0
                for (index in allClass.indices) {
                    val academicClass = allClass[index]
                    if (academicClass.id != null && academicClass.id == viewModel.selectedClassId.toString()) {
                        academicClassIndex = index + 1
                    }
                }
                viewDataBinding.spClass.setSelection(academicClassIndex, true)
                viewModel.selectedClassId = viewModel.selectedClassId

//                lifecycleScope.launch {
//
//                }

//                for (cls in allClass) {
//                    val id = cls.id
//                    if (id != null && !alreadyLoadedClass.contains(id.toInt())) {
//                        viewModel.getAcademicBooks(userData.mobile ?: "", id.toInt())
//                    }
//                }
            }
        })

        if (adBanners.isEmpty()) {
            viewModel.getAds()
        }
        viewModel.getAllFreeBooks()
    }
}
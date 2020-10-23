package com.rtchubs.engineerbooks.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.Home2Binding
import com.rtchubs.engineerbooks.models.Book
import com.rtchubs.engineerbooks.models.Chapter
import com.rtchubs.engineerbooks.ui.LogoutHandlerCallback
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.login.SliderView

class Home2Fragment : BaseFragment<Home2Binding, HomeViewModel>() {
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_main2
    override val viewModel: HomeViewModel by viewModels {
        viewModelFactory
    }

    lateinit var paymentListAdapter: PaymentMethodListAdapter

    private var listener: LogoutHandlerCallback? = null

    private var drawerListener: NavDrawerHandlerCallback? = null

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
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        drawerListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val chapterList = listOf(
            Chapter(1, "Chapter One", null, null),
            Chapter(2, "Chapter Two", null, null),
            Chapter(3, "Chapter Three", null, null),
            Chapter(4, "Chapter Four", null, null),
            Chapter(5, "Chapter Five", null, null),
            Chapter(6, "Chapter Six", null, null),
            Chapter(7, "Chapter Seven", null, null),
            Chapter(8, "Chapter Eight", null, null),
            Chapter(9, "Chapter Nine", null, null),
            Chapter(10, "Chapter Ten", null, null)
        )

        val book1 = Book(1, "Bangla", "", chapterList)
        val book2 = Book(2, "English", "",chapterList)
        val book3 = Book(3, "General Mathematics", "", chapterList)
        val book4 = Book(4, "Biology", "", chapterList)
        val book5 = Book(5, "Physics", "", chapterList)

        viewDataBinding.book1 = book1
        viewDataBinding.book2 = book2
        viewDataBinding.book3 = book3
        viewDataBinding.book4 = book4
        viewDataBinding.book5 = book5

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewModel.slideDataList.forEach { slideData ->
            val slide = SliderView(requireContext())
            slide.sliderTextTitle = slideData.textTitle
            slide.sliderTextDescription = slideData.descText
            slide.sliderImage(slideData.slideImage)
            viewDataBinding.sliderLayout.addSlider(slide)
        }

        viewDataBinding.item1.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book1))
        }

        viewDataBinding.item2.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book2))
        }

        viewDataBinding.item3.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book3))
        }

        viewDataBinding.item4.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book4))
        }

        viewDataBinding.item5.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToChapterListFragment(book5))
        }

        viewDataBinding.item6.setOnClickListener {
            navController.navigate(Home2FragmentDirections.actionHome2FragmentToMoreBookListFragment())
        }
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
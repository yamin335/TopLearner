package com.rtchubs.engineerbooks.ui.more

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint
import com.rtchubs.engineerbooks.databinding.MoreFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.LogoutHandlerCallback
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.profile_signin.ClassEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.DistrictEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.UpazillaEditFragment
import com.rtchubs.engineerbooks.ui.splash.SplashFragment
import com.rtchubs.engineerbooks.util.goToFacebook
import com.rtchubs.engineerbooks.util.goToYoutube

class MoreFragment : BaseFragment<MoreFragmentBinding, MoreViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_more

    override val viewModel: MoreViewModel by viewModels { viewModelFactory }

    private var listener: LogoutHandlerCallback? = null

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var userData: InquiryAccount

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userData = preferencesHelper.getUser()

        init(userData)

        viewDataBinding.mProfile.setOnClickListener {
            ClassEditFragment.selectedClass = null
            DistrictEditFragment.selectedCity = null
            UpazillaEditFragment.selectedUpazilla = null
            navigateTo(MoreFragmentDirections.actionMoreFragmentToProfileSettingsFragment())
        }

        viewDataBinding.mPayment.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToPaymentFragment2())
        }

        viewDataBinding.logout.setOnClickListener {
            SplashFragment.fromLogout = true
            preferencesHelper.isLoggedIn = false
            preferencesHelper.isBookPaid = false
            listener?.onLoggedOut()
        }

        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }

        viewDataBinding.mNoticeBoard.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToNoticeBoardFragment())
        }

        viewDataBinding.mOffer.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToOfferFragment())
        }

        viewDataBinding.mECode.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToEcodeFragment())
        }

        viewDataBinding.mYoutube.setOnClickListener {
            goToYoutube(requireContext(), "engineersapps")
        }

        viewDataBinding.mFacebook.setOnClickListener {
            goToFacebook(requireContext(), "engineersapps")
        }

        viewDataBinding.mShare.setOnClickListener {

        }

        viewDataBinding.mAboutUs.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToAboutUsFragment())
        }
    }

    private fun init(user: InquiryAccount) {
        Glide.with(requireContext())
            .load("${ApiEndPoint.PROFILE_IMAGES}/${user.Folder}/${user.profilePic}")
            .placeholder(R.drawable.doctor_1)
            .circleCrop()
            .into(viewDataBinding.rivProfileImage)

        viewDataBinding.tvName.text = "${user.firstName} ${user.lastName}"
        viewDataBinding.tvClass.text = user.ClassName
        viewDataBinding.tvSID.text = user.mobile
        viewDataBinding.tvValidDate.text = "N/A"
        viewDataBinding.tvPackage.text = "N/A"
    }

}
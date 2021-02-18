package com.rtchubs.engineerbooks.ui.more

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.BuildConfig
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint
import com.rtchubs.engineerbooks.databinding.MoreFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.social_media.SocialMedia
import com.rtchubs.engineerbooks.ui.LogoutHandlerCallback
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.profile_signin.ClassEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.DistrictEditFragment
import com.rtchubs.engineerbooks.ui.profile_signin.UpazillaEditFragment
import com.rtchubs.engineerbooks.ui.social_media.SocialMediaBottomSheetDialog
import com.rtchubs.engineerbooks.ui.splash.SplashFragment
import com.rtchubs.engineerbooks.util.BitmapUtilss
import com.rtchubs.engineerbooks.util.goToFacebook
import com.rtchubs.engineerbooks.util.goToYoutube
import com.rtchubs.engineerbooks.util.showWarningToast
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class MoreFragment : BaseFragment<MoreFragmentBinding, MoreViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_more

    override val viewModel: MoreViewModel by viewModels { viewModelFactory }

    private var listener: LogoutHandlerCallback? = null

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var userData: InquiryAccount
    var placeholder: BitmapDrawable? = null

    lateinit var socialMediaBottomSheetDialog: SocialMediaBottomSheetDialog

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

        placeholder =
            BitmapUtilss.transformDrawable( // has white background because it's not transparent, so rounding will be visible
                requireContext(),
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.doctor_1
                ),  // transformation to be applied
                RoundedCornersTransformation(
                    128,
                    0,
                    RoundedCornersTransformation.CornerType.ALL
                ),  // size of the target in pixels
                256
            )

        Glide.with(requireContext())
            .load(placeholder)
            .into(viewDataBinding.rivProfileImage)

        init(userData)

        socialMediaBottomSheetDialog = SocialMediaBottomSheetDialog(object : SocialMediaBottomSheetDialog.SocialMediaClickListener {
            override fun onSocialMediaClick(socialMedia: SocialMedia) {
                when(socialMedia.name) {
                    "Facebook" -> goToFacebook(requireContext(), "engineersapps")
                    "Youtube" -> goToYoutube(requireContext(), "engineersapps")
                }
                socialMediaBottomSheetDialog.dismiss()
            }
        })

        viewDataBinding.mProfile.setOnClickListener {
            if (userData.customer_type_id == 2) {
                navigateTo(MoreFragmentDirections.actionMoreFragmentToPartnerProfileFragment())
            } else {
                ClassEditFragment.selectedClass = null
                DistrictEditFragment.selectedCity = null
                UpazillaEditFragment.selectedUpazilla = null
                navigateTo(MoreFragmentDirections.actionMoreFragmentToProfileSettingsFragment())
            }
        }

        viewDataBinding.mPayment.setOnClickListener {
            val paidBook = preferencesHelper.getPaidBook()
            if (paidBook.isPaid) {
                navigateTo(MoreFragmentDirections.actionMoreFragmentToPaymentFragmentMore(paidBook))
            } else {
                showWarningToast(requireContext(), "Please go to book list and choose your desired book!")
            }

        }

        viewDataBinding.rtchubs.setOnClickListener {
            val url = "https://rtchubs.com"
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(myIntent)
        }

        viewDataBinding.logout.setOnClickListener {
            SplashFragment.fromLogout = true
            preferencesHelper.isLoggedIn = false
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

        viewDataBinding.mTestPaper.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToEcodeFragment())
        }

        viewDataBinding.mSocialMedia.setOnClickListener {
            socialMediaBottomSheetDialog.show(childFragmentManager, "#Social_Media_Bottom_Modal")
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
            .circleCrop()
            .placeholder(placeholder)
            .into(viewDataBinding.rivProfileImage)

        viewDataBinding.tvName.text = "${user.firstName} ${user.lastName}"
        viewDataBinding.tvClass.text = user.ClassName
        viewDataBinding.tvSID.text = user.mobile
        viewDataBinding.version.text = "Version ${BuildConfig.VERSION_NAME}"
    }
}
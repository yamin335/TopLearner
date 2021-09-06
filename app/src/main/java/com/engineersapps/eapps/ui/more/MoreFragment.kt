package com.engineersapps.eapps.ui.more

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.BuildConfig
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiEndPoint
import com.engineersapps.eapps.databinding.MoreFragmentBinding
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.social_media.SocialMedia
import com.engineersapps.eapps.ui.NavDrawerHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.common.CommonMessageBottomSheetDialog
import com.engineersapps.eapps.ui.profile_signin.ClassEditFragment
import com.engineersapps.eapps.ui.profile_signin.DistrictEditFragment
import com.engineersapps.eapps.ui.profile_signin.UpazillaEditFragment
import com.engineersapps.eapps.ui.social_media.SocialMediaBottomSheetDialog
import com.engineersapps.eapps.util.BitmapUtilss
import com.engineersapps.eapps.util.goToFacebook
import com.engineersapps.eapps.util.goToYoutube
import com.engineersapps.eapps.util.showWarningToast
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

private const val REQUEST_PHONE_CALL = 101
private const val HELPLINE = "09678271271"
private const val APP_LINK = "https://play.google.com/store/apps/details?id=com.engineersapps.eapps"
class MoreFragment : BaseFragment<MoreFragmentBinding, MoreViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_more

    override val viewModel: MoreViewModel by viewModels { viewModelFactory }

    private var drawerListener: NavDrawerHandlerCallback? = null

    lateinit var userData: InquiryAccount
    var placeholder: BitmapDrawable? = null

    lateinit var socialMediaBottomSheetDialog: SocialMediaBottomSheetDialog
    lateinit var commonMessageBottomSheetDialog: CommonMessageBottomSheetDialog

    companion object {
        var promoDiscountValue: Int = 0
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonMessageBottomSheetDialog = CommonMessageBottomSheetDialog(null)

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

        if (userData.customer_type_id == 2) {
            if (userData.partner_type == "promo") {
                viewDataBinding.tvUserType.text = "Promoter"
            } else {
                viewDataBinding.tvUserType.text = "Partner"
            }
            viewDataBinding.mSubscription.visibility = View.VISIBLE
        } else {
            viewDataBinding.tvUserType.text = "Student"
            viewDataBinding.mSubscription.visibility = View.GONE
        }

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
                navigateTo(MoreFragmentDirections.actionMoreFragmentToPaymentNav(paidBook))
            } else {
                showWarningToast(requireContext(), "Please go to book list and choose your desired book!")
            }

        }

        viewDataBinding.rtchubs.setOnClickListener {
            val url = "https://rtchubs.com"
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(myIntent)
        }

        viewModel.promoCode.observe(viewLifecycleOwner, Observer { promoPair ->
            promoPair?.let {
                if (promoPair.first) {
                    if (promoPair.second?.discount == null) {
                        showWarningToast(requireContext(), "No valid promo found!")
                    } else {
                        promoDiscountValue = promoPair.second?.discount ?: 0
                        sharePromoCode()
                    }
                } else {
                    showWarningToast(requireContext(), "No valid promo found!")
                }
                viewModel.promoCode.postValue(null)
            }
        })

        viewDataBinding.logout.setOnClickListener {
            logoutListener?.onLoggedOut()
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

        viewDataBinding.mTransactions.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToTransactionFragment())
        }

//        viewDataBinding.mSocialMedia.setOnClickListener {
//            socialMediaBottomSheetDialog.show(childFragmentManager, "#Social_Media_Bottom_Modal")
//        }

        viewDataBinding.mFacebook.setOnClickListener {
            goToFacebook(requireContext(), "engineersapps")
        }

        viewDataBinding.mYoutube.setOnClickListener {
            goToYoutube(requireContext(), "engineersapps")
        }

        viewDataBinding.mShare.setOnClickListener {
            shareApp()
        }

        viewDataBinding.mHelpline.setOnClickListener {
            callHelpLine()
        }

        viewDataBinding.mAboutUs.setOnClickListener {
            navigateTo(MoreFragmentDirections.actionMoreFragmentToAboutUsFragment())
        }

        viewDataBinding.mSubscription.setOnClickListener {
            if (promoDiscountValue == 0) {
                viewModel.verifyPromoCode(userData.promo_code ?: "")
            } else {
                sharePromoCode()
            }
        }
    }

    private fun sharePromoCode() {
        try {
            val value = "${userData.promo_code} কোড ব্যাবহার করলে $promoDiscountValue% ডিস্কাউন্ট পাবেন।"
            val title = "${userData.promo_code} কোড ব্যাবহারে $promoDiscountValue% ডিস্কাউন্ট পাবেন।"

            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, value)
            mContext.startActivity(
                Intent.createChooser(
                    share,
                    title
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareApp() {
        try {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, APP_LINK)
            mContext.startActivity(
                Intent.createChooser(
                    share,
                    "Share Via"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callHelpLine() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$HELPLINE"))
        if (isCallPermissionGranted) {
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                showWarningToast(requireContext(), "No Application found")
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
        }
    }

    private val isCallPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callHelpLine()
            }
        }
    }

    private fun init(user: InquiryAccount) {

        if (user.customer_type_id == 2) {
            Glide.with(requireContext())
                .load("${ApiEndPoint.PARTNER_PROFILE_IMAGE}/${user.profilePic}")
                .circleCrop()
                .placeholder(placeholder)
                .into(viewDataBinding.rivProfileImage)
        } else {
            Glide.with(requireContext())
                .load("${ApiEndPoint.PROFILE_IMAGES}/${user.Folder}/${user.profilePic}")
                .circleCrop()
                .placeholder(placeholder)
                .into(viewDataBinding.rivProfileImage)
        }

        viewDataBinding.tvName.text = "${user.first_name}"
        viewDataBinding.tvClass.text = user.ClassName
        viewDataBinding.tvSID.text = user.mobile
        viewDataBinding.version.text = "Version ${BuildConfig.VERSION_NAME}"
    }
}
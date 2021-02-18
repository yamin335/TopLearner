package com.rtchubs.engineerbooks.ui.profiles

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint.PROFILE_IMAGES
import com.rtchubs.engineerbooks.databinding.PartnerProfileFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.BitmapUtilss.transformDrawable
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class PartnerProfileFragment : BaseFragment<PartnerProfileFragmentBinding, ProfileSettingsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile_partner

    override val viewModel: ProfileSettingsViewModel by viewModels { viewModelFactory }

    lateinit var userData: InquiryAccount
    var placeholder: BitmapDrawable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        userData = preferencesHelper.getUser()

        placeholder =
            transformDrawable( // has white background because it's not transparent, so rounding will be visible
                requireContext(),
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.doctor_1
                ),  // transformation to be applied
                RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL),  // size of the target in pixels
                256
            )

        Glide.with(requireContext())
            .load(placeholder)
            .into(viewDataBinding.rivProfileImage)

        viewModel.userProfileInfo.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { userInfo ->
                userInfo?.let {
                    userData = it
                    preferencesHelper.saveUser(it)
                    prepareUserData(it)
                }

                if (userInfo == null) {
                    prepareUserData(userData)
                }
            })

        if (checkNetworkStatus()) {
            viewModel.getUserProfileInfo(userData.mobile ?: "")
        } else {
            prepareUserData(userData)
        }
    }

    private fun prepareUserData(user: InquiryAccount) {
        Glide.with(requireContext())
            .load("${PROFILE_IMAGES}/${user.Folder}/${user.profilePic}")
            .circleCrop()
            .placeholder(placeholder)
            .into(viewDataBinding.rivProfileImage)

        viewDataBinding.name.text = "${user.firstName} ${user.lastName}"
        viewDataBinding.fatherName.text = "N/A"
        viewDataBinding.motherName.text = "N/A"
        viewDataBinding.mobileNo.text = user.mobile
        viewDataBinding.homeContact.text = "N/A"
        viewDataBinding.email.text = user.email
        viewDataBinding.nidNo.text = user.nidnumber
        viewDataBinding.birthDate.text = "N/A"
        viewDataBinding.bloodGroup.text = "N/A"
        viewDataBinding.presentAddress.text = user.address
        viewDataBinding.permanentAddress.text = user.address
        viewDataBinding.officialId.text = "N/A"
        viewDataBinding.partnerType.text = "N/A"
        viewDataBinding.responsibleArea.text = "N/A"
        viewDataBinding.designation.text = "N/A"
        viewDataBinding.sharePercent.text = "N/A"
        viewDataBinding.earnedMoney.text = "N/A"
        viewDataBinding.subTotal.text = "N/A"
        viewDataBinding.paymentAmount.text = "N/A"
        viewDataBinding.payableAmount.text = "N/A"
    }

}
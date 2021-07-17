package com.rtchubs.engineerbooks.ui.profiles

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint.PARTNER_PROFILE_IMAGE
import com.rtchubs.engineerbooks.databinding.PartnerProfileFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.BitmapUtilss.transformDrawable
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import okio.ArrayIndexOutOfBoundsException


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

        viewModel.partnerPaymentStatus.observe(viewLifecycleOwner, Observer {
            it?.let { data ->
                viewDataBinding.earnedMoney.text = "${data.totalamountearns ?: 0} ৳"
                viewDataBinding.payableAmount.text = "${data.totalamountdue ?: 0} ৳"
                viewDataBinding.paymentAmount.text = "${data.totalamountpaid ?: 0} ৳"
                viewDataBinding.noOfStudents.text = "${data.student_numbers ?: 0}"
            }
        })

        if (checkNetworkStatus(true)) {
            viewModel.getUserProfileInfo(userData.mobile ?: "")
            viewModel.getPartnerPaymentStatus(userData.mobile, userData.CityID, userData.UpazilaID)
        } else {
            prepareUserData(userData)
        }
    }

    private fun prepareUserData(user: InquiryAccount) {
        Glide.with(requireContext())
            .load("${PARTNER_PROFILE_IMAGE}/${user.profilePic}")
            .circleCrop()
            .placeholder(placeholder)
            .into(viewDataBinding.rivProfileImage)

        viewDataBinding.name.text = "${user.first_name}"
        viewDataBinding.fatherName.text = if (user.father_name.isNullOrBlank()) "N/A" else user.father_name
        viewDataBinding.motherName.text = if (user.mother_name.isNullOrBlank()) "N/A" else user.mother_name
        viewDataBinding.mobileNo.text = if (user.mobile.isNullOrBlank()) "N/A" else user.mobile
        viewDataBinding.homeContact.text = if (user.home_mobile.isNullOrBlank()) "N/A" else user.home_mobile
        viewDataBinding.email.text = if (user.email.isNullOrBlank()) "N/A" else user.email
        viewDataBinding.nidNo.text = if (user.nidnumber.isNullOrBlank()) "N/A" else user.nidnumber
        viewDataBinding.bloodGroup.text = if (user.blood.isNullOrBlank()) "N/A" else user.blood
        viewDataBinding.presentAddress.text = if (user.present_address.isNullOrBlank()) "N/A" else user.present_address
        viewDataBinding.permanentAddress.text = if (user.parmanent_address.isNullOrBlank()) "N/A" else user.parmanent_address
        viewDataBinding.officialId.text = if (user.official_id.isNullOrBlank()) "N/A" else user.official_id
        viewDataBinding.partnerType.text = if (user.partner_designation.isNullOrBlank()) "N/A" else user.partner_designation
        viewDataBinding.responsibleArea.text = if (user.upazila.isNullOrBlank()) "N/A" else user.upazila
        viewDataBinding.designation.text = if (user.designation.isNullOrBlank()) "N/A" else user.designation
        viewDataBinding.sharePercent.text = "${user.discount_amount ?: 0}%"
        try {
            viewDataBinding.birthDate.text = if (user.BirthDate.isNullOrBlank()) "N/A" else user.BirthDate?.split("T")?.get(0)
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}
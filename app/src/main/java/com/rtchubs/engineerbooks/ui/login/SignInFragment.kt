package com.rtchubs.engineerbooks.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiCallStatus
import com.rtchubs.engineerbooks.databinding.LayoutOperatorSelectionBinding
import com.rtchubs.engineerbooks.databinding.SignInBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.prefs.AppPreferencesHelper
import com.rtchubs.engineerbooks.ui.OTPHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.otp_signin.OtpSignInFragment
import com.rtchubs.engineerbooks.util.AppConstants.commonErrorMessage
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.isTimeAndZoneAutomatic
import com.rtchubs.engineerbooks.util.showErrorToast

class SignInFragment : BaseFragment<SignInBinding, SignInViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_sign_in
    override val viewModel: SignInViewModel by viewModels {
        viewModelFactory
    }

    private var startOTPListenerCallback: OTPHandlerCallback? = null

    var timeChangeListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            AppPreferencesHelper.KEY_DEVICE_TIME_CHANGED -> {
                OtpSignInFragment.isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged
                OtpSignInFragment.isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())
                if (!OtpSignInFragment.isDeviceTimeChanged) preferencesHelper.falseOTPCounter = 0
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferencesHelper.isDeviceTimeChanged = !isTimeAndZoneAutomatic(requireContext())
        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)
        OtpSignInFragment.isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
            OtpSignInFragment.isDeviceTimeChanged = true
        } else {
            preferencesHelper.isDeviceTimeChanged = false
            OtpSignInFragment.isDeviceTimeChanged = false
        }
        if (!OtpSignInFragment.isDeviceTimeChanged) preferencesHelper.falseOTPCounter = 0
    }

    override fun onPause() {
        super.onPause()
        preferencesHelper.preference.unregisterOnSharedPreferenceChangeListener(timeChangeListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OTPHandlerCallback) {
            startOTPListenerCallback = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        startOTPListenerCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")

        viewModel.mobileNo.observe(viewLifecycleOwner, Observer {  mobileNo ->
            mobileNo?.let {
                viewDataBinding.btnProceed.isEnabled = (it.length == 11) && (it[0] == '0') && (it[1] == '1') && viewModel.apiCallStatus.value != ApiCallStatus.LOADING
            }
        })

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer {
            viewDataBinding.btnProceed.isEnabled = it != ApiCallStatus.LOADING
        })

        viewDataBinding.btnProceed.setOnClickListener {
            if (OtpSignInFragment.isDeviceTimeChanged && preferencesHelper.falseOTPCounter > 0) {
                showErrorToast(requireContext(), "Please make device time automatic and try again!")
                return@setOnClickListener
            }
            hideKeyboard()
            openOperatorSelectionDialog()
        }
    }

//    private fun tempopenOperatorSelectionDialog() {
//        val bottomSheetDialog = BottomSheetDialog(mActivity)
//        val binding = DataBindingUtil.inflate<LayoutOperatorSelectionBinding>(
//            layoutInflater,
//            R.layout.layout_operator_selection,
//            null,
//            false
//        )
//        bottomSheetDialog.setContentView(binding.root)
//
//
//        binding.btnBanglalink.setOnClickListener {
//            bottomSheetDialog.dismiss()
//            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
//            navController.navigate(action)
//        }
//
//        binding.btnGrameenphone.setOnClickListener {
//            bottomSheetDialog.dismiss()
//            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
//            navController.navigate(action)
//        }
//
//        binding.btnRobi.setOnClickListener {
//            bottomSheetDialog.dismiss()
//            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
//            navController.navigate(action)
//        }
//
//        binding.btnTeletalk.setOnClickListener {
//            bottomSheetDialog.dismiss()
//            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
//            navController.navigate(action)
//        }
//        bottomSheetDialog.show()
//    }

    private fun openOperatorSelectionDialog() {
        val bottomSheetDialog = BottomSheetDialog(mActivity)
        val binding = DataBindingUtil.inflate<LayoutOperatorSelectionBinding>(
            layoutInflater,
            R.layout.layout_operator_selection,
            null,
            false
        )
        bottomSheetDialog.setContentView(binding.root)


        binding.btnBanglalink.setOnClickListener {
            goForRegistration(bottomSheetDialog, "Banglalink")
        }

        binding.btnGrameenphone.setOnClickListener {
            goForRegistration(bottomSheetDialog, "Grameenphone")
        }

        binding.btnRobi.setOnClickListener {
            goForRegistration(bottomSheetDialog, "Robi")
        }

        binding.btnTeletalk.setOnClickListener {
            goForRegistration(bottomSheetDialog, "Teletalk")
        }
        bottomSheetDialog.show()
    }

    private fun goForRegistration(dialog: BottomSheetDialog, operator: String) {
        dialog.dismiss()
        inquireAccount(operator)
    }

    private fun inquireAccount(operator: String) {
        startOTPListenerCallback?.onStartOTPListener()
        viewModel.inquireAccount().observe(viewLifecycleOwner, Observer { response ->
            response?.data?.Account?.let {
                it.mobile_operator = operator
                if (it.isRegistered == false) {
                    if (it.isAcceptedTandC == true) {
                        requestOTPCode(it, operator)
                    } else {
                        navigateTo(SignInFragmentDirections.actionSignInFragmentToTermsFragment(it))
                    }
                } else if (it.isRegistered == true && it.isMobileVerified == true) {
                    navigateTo(SignInFragmentDirections.actionSignInFragmentToOtpSignInFragment(it))
                } else {
                    showErrorToast(mContext, response.msg ?: commonErrorMessage)
                }
            }
        })
    }

    private fun requestOTPCode(registrationHelper: InquiryAccount, operator: String) {
        viewModel.requestOTPCode(registrationHelper).observe(viewLifecycleOwner, Observer { response ->
            response?.data?.Account?.let {
                it.mobile_operator = operator
                navigateTo(SignInFragmentDirections.actionSignInFragmentToOtpSignInFragment(it))

//                if (it.isRegistered == false) {
//                    if (it.isAcceptedTandC == true) {
//                        navigateTo(SignInFragmentDirections.actionSignInFragmentToOtpSignInFragment(it))
//                    } else {
//                        navigateTo(SignInFragmentDirections.actionSignInFragmentToTermsFragment(it))
//                    }
//                } else if (it.isRegistered == true && it.isMobileVerified == true) {
//                    navigateTo(SignInFragmentDirections.actionSignInFragmentToOtpSignInFragment(it))
////                    if (it.isAcceptedTandC == true) {
////
////                    } else {
////                        navigateTo(SignInFragmentDirections.actionSignInFragmentToTermsFragment(it))
////                    }
//                } else {
//                    showErrorToast(mContext, response.msg ?: commonErrorMessage)
//                }
            }
        })
    }
}
package com.rtchubs.engineerbooks.ui.otp_signin

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.OtpSignInBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.registration.RegistrationHelperModel
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.AppConstants.START_TIME_IN_MILLI_SECONDS
import com.rtchubs.engineerbooks.util.AppConstants.otpWaitMessage
import com.rtchubs.engineerbooks.util.showWarningToast

class OtpSignInFragment : BaseFragment<OtpSignInBinding, OtpSignInViewModel>(), PermissionListener {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_otp_sign_in

    override val viewModel: OtpSignInViewModel by viewModels { viewModelFactory }

    val args: OtpSignInFragmentArgs by navArgs()
    lateinit var helper: InquiryAccount

    private var countdownTimer: CountDownTimer? = null
    var repeater = 0

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    override fun onPause() {
        super.onPause()
        resetTimer()
    }

    override fun onPermissionGranted() {
        navigateTo(OtpSignInFragmentDirections.actionOtpSignInFragmentToPinNumberFragment(helper))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        helper = args.registrationHelper
        startTimer()

        viewDataBinding.btnSubmit.setOnClickListener {
            viewDataBinding.etOtpCode.isEnabled = false
            viewDataBinding.btnSubmit.isEnabled = false
            viewModel.verifyOTPCode(helper)
        }

        viewModel.registeredOTP.observe(viewLifecycleOwner, Observer { inquiryResponse ->
            inquiryResponse?.data?.Account?.let {
                helper = it
                viewDataBinding.tvOtpTextDescription.text = "An OTP Code has been sent to your mobile +88${helper.mobile}"
                viewDataBinding.etOtpCode.isEnabled = true
                viewDataBinding.btnSubmit.isEnabled = true
            }
        })

        viewDataBinding.btnResend.setOnClickListener {
            startTimer()
            viewModel.requestOTPCode(helper)
            viewDataBinding.tvOtpTextDescription.text = otpWaitMessage
            viewDataBinding.etOtpCode.setText("")
            viewDataBinding.etOtpCode.isEnabled = false
            viewDataBinding.btnSubmit.isEnabled = false
            //showWarningToast(mContext, "Please wait 5 minutes before you request a new OTP!")
        }

        viewModel.otp.observe(viewLifecycleOwner, Observer { otp ->
            otp?.let {
                viewDataBinding.btnSubmit.isEnabled = it.length == 3
            }
        })

        viewModel.verifiedOTP.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.Account?.let {
                if (!it.otp.isNullOrBlank() && it.otp == viewModel.otp.value) {
                    helper = it
                    TedPermission.with(requireContext())
                        .setPermissionListener(this)
                        .setDeniedMessage(getString(R.string.if_you_reject_these_permission_the_app_wont_work_perfectly))
                        .setPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS
                        ).check()
                } else {
                    viewDataBinding.tvOtpTextDescription.text = "You entered an invalid OTP code! please request a new code"
                    viewDataBinding.etOtpCode.setText("")
                    viewDataBinding.etOtpCode.isEnabled = false
                    viewDataBinding.btnSubmit.isEnabled = false
                }
            }
        })

        viewModel.registeredOTP.observe(viewLifecycleOwner, Observer { response ->
//            response?.let {
//                when {
//                    it.isSuccess == true -> {
//                        viewDataBinding.tvOtpTextDescription.text = "An OTP Code has been sent to your mobile +88${args.registrationHelper.mobile}"
//                        viewDataBinding.etOtpCode.isEnabled = true
//                    }
//                    it.isSuccess == false && it.errorMessage != null -> {
//                        viewDataBinding.tvOtpTextDescription.text = it.errorMessage
//                        showWarningToast(mContext, it.errorMessage)
//                        viewDataBinding.etOtpCode.isEnabled = false
//                    }
//                    else -> {
//                        showWarningToast(mContext, "Please wait 5 minutes before you request a new OTP!")
//                    }
//                }
//            }
        })

//        if (helper.isRegistered) {
//            viewDataBinding.etOtpCode.isEnabled = true
//        } else {
//            viewModel.requestOTP(args.registrationHelper.mobile, args.registrationHelper.isTermsAccepted.toString())
//        }
    }

//    private fun pauseTimer() {
//
//        button.text = "Start"
//        countdown_timer.cancel()
//        isRunning = false
//        reset.visibility = View.VISIBLE
//    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(START_TIME_IN_MILLI_SECONDS, 1000) {
            override fun onFinish() {
                viewDataBinding.btnResend.isEnabled = ++repeater < 3
            }

            override fun onTick(time_in_milli_seconds: Long) {
                updateTextUI(time_in_milli_seconds)
            }
        }
        countdownTimer?.start()

        viewDataBinding.btnResend.isEnabled = false
    }

    private fun resetTimer() {
        countdownTimer?.cancel()
        updateTextUI(START_TIME_IN_MILLI_SECONDS)
    }

    private fun updateTextUI(time_in_milli_seconds: Long) {
        val minute = (time_in_milli_seconds / 1000) / 60
         if(minute.toString().trim().length==1){
             viewDataBinding.minuteView.text = "0$minute"
         } else {
             viewDataBinding.minuteView.text = "$minute"
         }
        val seconds = (time_in_milli_seconds / 1000) % 60
        viewDataBinding.secondView.text = "$seconds"
    }

}
package com.rtchubs.engineerbooks.ui.otp_signin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiCallStatus
import com.rtchubs.engineerbooks.databinding.OtpSignInBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.prefs.AppPreferencesHelper
import com.rtchubs.engineerbooks.ui.OTPHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.AppConstants.START_TIME_IN_MILLI_SECONDS
import com.rtchubs.engineerbooks.util.isTimeAndZoneAutomatic
import com.rtchubs.engineerbooks.util.showErrorToast


class OtpSignInFragment : BaseFragment<OtpSignInBinding, OtpSignInViewModel>() {

    companion object {
        var isDeviceTimeChanged = false
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_otp_sign_in

    override val viewModel: OtpSignInViewModel by viewModels { viewModelFactory }

    val args: OtpSignInFragmentArgs by navArgs()
    lateinit var registrationLocalHelper: InquiryAccount
    lateinit var registrationRemoteHelper: InquiryAccount

    private var countdownTimer: CountDownTimer? = null
    var repeater = 0

    private var startOTPListenerCallback: OTPHandlerCallback? = null

    var timeChangeListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            AppPreferencesHelper.KEY_DEVICE_TIME_CHANGED -> {
                isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged
                isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())
                if (!isDeviceTimeChanged) preferencesHelper.falseOTPCounter = 0
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferencesHelper.isDeviceTimeChanged = !isTimeAndZoneAutomatic(requireContext())
        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)
        isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
            isDeviceTimeChanged = true
        } else {
            preferencesHelper.isDeviceTimeChanged = false
            isDeviceTimeChanged = false
        }
        if (!isDeviceTimeChanged) preferencesHelper.falseOTPCounter = 0
    }

    override fun onPause() {
        super.onPause()
        preferencesHelper.preference.unregisterOnSharedPreferenceChangeListener(timeChangeListener)
        resetTimer()
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        registrationLocalHelper = args.registrationHelper
        registrationRemoteHelper = args.registrationHelper
        startTimer()

        viewDataBinding.btnSubmit.setOnClickListener {
//            viewDataBinding.etOtpCode.isEnabled = false
//            viewDataBinding.btnSubmit.isEnabled = false
            viewModel.verifyOTPCode(registrationRemoteHelper)
        }

        viewModel.registeredOTP.observe(viewLifecycleOwner, Observer { inquiryResponse ->
            inquiryResponse?.data?.Account?.let {
                registrationRemoteHelper = it
//                viewDataBinding.tvOtpTextDescription.text =
//                    "An OTP Code has been sent to your mobile +88${registrationRemoteHelper.mobile}"
                viewDataBinding.etOtpCode.isEnabled = true
                viewDataBinding.btnSubmit.isEnabled = true
            }
        })

        viewDataBinding.btnResend.setOnClickListener {
            if (isDeviceTimeChanged && preferencesHelper.falseOTPCounter > 0) {
                showErrorToast(requireContext(), "Please make device time automatic and try again!")
                return@setOnClickListener
            }
            startOTPListenerCallback?.onStartOTPListener()
            startTimer()
            viewModel.requestOTPCode(registrationRemoteHelper)
            //viewDataBinding.tvOtpTextDescription.text = otpWaitMessage
            viewDataBinding.etOtpCode.setText("")
//            viewDataBinding.etOtpCode.isEnabled = false
//            viewDataBinding.btnSubmit.isEnabled = false
            //showWarningToast(mContext, "Please wait 5 minutes before you request a new OTP!")
        }

        viewModel.otp.observe(viewLifecycleOwner, Observer { otp ->
            otp?.let {
                viewDataBinding.btnSubmit.isEnabled = it.length == 4 && viewModel.apiCallStatus.value != ApiCallStatus.LOADING
            }
        })

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer {
            viewDataBinding.btnSubmit.isEnabled = it != ApiCallStatus.LOADING
        })

        viewModel.verifiedOTP.observe(viewLifecycleOwner, Observer { response ->
            val account = response?.data?.Account
            if (account == null || response.data.Token == null) {
                preferencesHelper.falseOTPCounter++
            } else {
                preferencesHelper.accessToken = response.data.Token.AccessToken
                if (!account.otp.isNullOrBlank() && account.otp == viewModel.otp.value) {
                    registrationRemoteHelper = account
                    registrationRemoteHelper.mobile_operator = registrationLocalHelper.mobile_operator
                    navigateTo(
                        OtpSignInFragmentDirections.actionOtpSignInFragmentToPinNumberFragment(
                            registrationRemoteHelper
                        )
                    )
                    viewModel.verifiedOTP.postValue(null)
                } else {
                    preferencesHelper.falseOTPCounter++
//                    viewDataBinding.tvOtpTextDescription.text =
//                        "You entered an invalid OTP code! please request a new code"
                    viewDataBinding.etOtpCode.setText("")
//                    viewDataBinding.etOtpCode.isEnabled = false
//                    viewDataBinding.btnSubmit.isEnabled = false
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
                viewDataBinding.btnResend.isEnabled = ++repeater < 4
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

    fun updateOTP(otp: String?) {
        viewDataBinding.etOtpCode.setText(otp)
        viewModel.verifyOTPCode(registrationRemoteHelper)
    }
}
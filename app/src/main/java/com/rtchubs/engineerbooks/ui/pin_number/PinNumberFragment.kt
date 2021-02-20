package com.rtchubs.engineerbooks.ui.pin_number

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.PinNumberBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.LoginHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showSuccessToast

class PinNumberFragment : BaseFragment<PinNumberBinding, PinNumberViewModel>(), PermissionListener {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_pin_number

    override val viewModel: PinNumberViewModel by viewModels { viewModelFactory }

    lateinit var registrationLocalHelper: InquiryAccount
    lateinit var registrationRemoteHelper: InquiryAccount
    private var listener: LoginHandlerCallback? = null
    lateinit var resetPinDialogFragment: ResetPinDialogFragment

    val args: PinNumberFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onPermissionGranted() {

    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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

        resetPinDialogFragment = ResetPinDialogFragment(object : ResetPinDialogFragment.PinResetCallback {
            override fun onPinChanged(pin: String) {
                resetPinDialogFragment.dismiss()
                viewModel.resetPin(registrationRemoteHelper.mobile ?: "", pin)
            }

        })

        viewDataBinding.forgotPassword.setOnClickListener {
            viewModel.verifyOTPCode(registrationRemoteHelper)
        }

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

        registrationLocalHelper = args.registrationHelper
        registrationRemoteHelper = args.registrationHelper

//        registrationLocalHelper.pin = "123456"
//        registrationLocalHelper.retypePin = "123456"
//
//        registrationRemoteHelper.pin = "123456"
//        registrationRemoteHelper.retypePin = "123456"

        if (registrationRemoteHelper.isRegistered == true) {
            viewDataBinding.linearReTypePin.visibility = View.GONE
            viewDataBinding.forgotPassword.visibility = View.VISIBLE
        } else {
            viewDataBinding.forgotPassword.visibility = View.GONE
        }

//        viewModel.defaultResponse.observe(viewLifecycleOwner, Observer { response ->
//            response?.let {
//                when {
//                    it.isSuccess == true -> {
//                        val data = JSONObject(response.body.toString())
//                        val tokenInfo = TokenInformation(
//                            data.getString("access_token"), data.getString("expires_in").toLong()
//                            , data.getString("refresh_token"), data.getString("token_type")
//                        )
//                        preferencesHelper.saveToken(tokenInfo)
//                        //val action = PinNumberFragmentDirections.actionPinNumberFragmentToHome()
//                        //navController.navigate(action)
//                    }
//                    it.isSuccess == false && it.errorMessage != null -> {
//                        showWarningToast(mContext, it.errorMessage)
//                    }
//                    else -> {
//                        showWarningToast(mContext, getString(R.string.something_went_wrong))
//                    }
//                }
//            }
//        })

        viewModel.pin.observe(viewLifecycleOwner, Observer { pin ->
            pin?.let {
                viewDataBinding.invalidPin.visibility = View.GONE
                if (registrationRemoteHelper.isRegistered == true) {
                    viewDataBinding.btnSubmit.isEnabled = pin.length == 6
                } else {
                    viewDataBinding.btnSubmit.isEnabled = pin.length == 6 && viewModel.rePin.value?.length == 6 && viewModel.rePin.value == pin
                }
            }
        })

        viewModel.rePin.observe(viewLifecycleOwner, Observer { rePin ->
            rePin?.let {
                viewDataBinding.btnSubmit.isEnabled = viewModel.pin.value?.length == 6 && rePin.length == 6 && viewModel.pin.value == rePin
            }
        })

        viewModel.verifiedOTP.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.let { data ->
                if (data.Account == null) {
                    showErrorToast(requireContext(), "You entered an invalid OTP code! please request a new code")
                } else {
                    resetPinDialogFragment.show(childFragmentManager, "#reset_pin_dialog")
                }
                viewModel.verifiedOTP.postValue(null)
            }
            response?.data?.Token?.let { token ->
                preferencesHelper.accessToken = token.AccessToken
            }
        })

        viewModel.resetPinResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.let { data ->
                if (data.Account == null) {
                    showErrorToast(requireContext(), "Pin reset failed!")
                } else {
                    showSuccessToast(requireContext(), "Pin reset successful!")
                }
                viewModel.resetPinResponse.postValue(null)
            }
        })

        viewModel.invalidPin.observe(viewLifecycleOwner, Observer { isInvalid ->
            isInvalid?.let {
                if (it) {
                    viewDataBinding.invalidPin.visibility = View.VISIBLE
                } else {
                    viewDataBinding.invalidPin.visibility = View.GONE
                }
                viewModel.invalidPin.postValue(null)
            }
        })

        viewModel.loginResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let { data ->
                data.Account?.let { account ->
                    if (account.isRegistered == true) {
                        preferencesHelper.accessToken = data.Token?.AccessToken
                        preferencesHelper.accessTokenExpiresIn = data.Token?.AtExpires ?: 0
                        preferencesHelper.isLoggedIn = true
                        preferencesHelper.saveUser(account)
                        listener?.onLoggedIn()
                    }
                }
            }
        })

        viewDataBinding.btnSubmit.setOnClickListener {
            hideKeyboard()
            registrationRemoteHelper.pin = viewModel.pin.value
            registrationRemoteHelper.retype_pin = viewModel.rePin.value
            if (registrationRemoteHelper.isRegistered == false) {
                navigateTo(PinNumberFragmentDirections.actionPinNumberFragmentToProfileSignInFragment(
                    registrationRemoteHelper
                ))
            } else {
                viewModel.loginUser(registrationRemoteHelper)
            }

//            viewModel.pin.value?.let {
//                pin = it
//            }
//
//            viewModel.rePin.value?.let {
//                rePin = it
//            }
//
//            when {
//                helper.isRegistered == true && pin.isNotBlank() && rePin.isBlank() -> {
//                    viewModel.connectToken(
//                        helper.mobile ?: "",
//                        pin,
//                        "password",
//                        "offline_access",
//                        Build.ID,
//                        Build.MANUFACTURER,
//                        Build.MODEL,
//                        "qpayandroid",
//                        "07A96yr@!1t8r",
//                        helper.otp ?: ""
//                    )
//                }
//                pin != rePin && !helper.isRegistered!! -> {
//                    viewDataBinding.etRepin.requestFocus()
//                    showErrorToast(requireContext(), "Both pin number does not match")
//                }
//                pin.isNotBlank() && rePin.isNotBlank() && pin == rePin && !helper.isRegistered!! -> {
//                    helper.pin = pin
//                    val action =
//                        PinNumberFragmentDirections.actionPinNumberFragmentToPermissionsFragment(
//                            helper
//                        )
//                    navController.navigate(action)
//                }
//            }
        }
    }
}
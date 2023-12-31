package com.engineersapps.eapps.ui.pin_number

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.databinding.PinNumberBinding
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.LoginHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.ui.terms_and_conditions.TermsAndConditionsFragment
import com.engineersapps.eapps.util.hideKeyboard
import com.engineersapps.eapps.util.showErrorToast
import com.engineersapps.eapps.util.showSuccessToast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

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

    override fun onResume() {
        super.onResume()
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
                viewModel.resetPin(registrationRemoteHelper.mobile ?: "", pin, registrationRemoteHelper.otp ?: "")
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).check()

        registrationLocalHelper = args.registrationHelper
        registrationRemoteHelper = args.registrationHelper

//        registrationLocalHelper.pin = "123456"
//        registrationLocalHelper.retypePin = "123456"
//
//        registrationRemoteHelper.pin = "123456"
//        registrationRemoteHelper.retypePin = "123456"

        if (TermsAndConditionsFragment.isTermsAccepted == null) {
            TermsAndConditionsFragment.isTermsAccepted = registrationRemoteHelper.isAcceptedTandC ?: false
        }

        viewDataBinding.cbTerms.isChecked = TermsAndConditionsFragment.isTermsAccepted ?: false

        if (registrationRemoteHelper.isRegistered == true) {
            viewDataBinding.linearReTypePin.visibility = View.GONE
            viewDataBinding.forgotPassword.visibility = View.VISIBLE
            viewDataBinding.resetPinLabel.visibility = View.VISIBLE
            viewDataBinding.termsLayout.visibility = View.GONE
        } else {
            viewDataBinding.termsLayout.visibility = View.VISIBLE
            viewDataBinding.forgotPassword.visibility = View.GONE
            viewDataBinding.resetPinLabel.visibility = View.GONE
        }

        viewDataBinding.termsAndConditions.setOnClickListener {
            navigateTo(PinNumberFragmentDirections.actionPinNumberFragmentToTermsAndConditions())
        }

        viewModel.registrationResponse.observe(viewLifecycleOwner, Observer {
            it?.let { data ->
                data.Account?.let { account ->
                    if (account.isRegistered == true) {
                        preferencesHelper.accessToken = data.Token?.AccessToken
                        preferencesHelper.accessTokenExpiresIn = data.Token?.AtExpires ?: 0
                        preferencesHelper.isLoggedIn = true
                        preferencesHelper.saveUser(account)
                        showSuccessToast(requireContext(), "Registration successful!")
                        listener?.onLoggedIn()
                    }
                }
            }
        })

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

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer {
            viewDataBinding.btnSubmit.isEnabled = it != ApiCallStatus.LOADING
        })

        viewModel.pin.observe(viewLifecycleOwner, Observer { pin ->
            pin?.let {
                viewDataBinding.invalidPin.visibility = View.GONE
                if (registrationRemoteHelper.isRegistered == true) {
                    viewDataBinding.btnSubmit.isEnabled = pin.length == 6 && viewModel.apiCallStatus.value != ApiCallStatus.LOADING
                } else {
                    viewDataBinding.btnSubmit.isEnabled = pin.length == 6 && viewModel.rePin.value?.length == 6 && viewModel.rePin.value == pin && viewModel.apiCallStatus.value != ApiCallStatus.LOADING && TermsAndConditionsFragment.isTermsAccepted == true
                }
            }
        })

        viewModel.rePin.observe(viewLifecycleOwner, Observer { rePin ->
            rePin?.let {
                viewDataBinding.btnSubmit.isEnabled = viewModel.pin.value?.length == 6 && rePin.length == 6 && viewModel.pin.value == rePin && viewModel.apiCallStatus.value != ApiCallStatus.LOADING && TermsAndConditionsFragment.isTermsAccepted == true
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

        viewDataBinding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            TermsAndConditionsFragment.isTermsAccepted = isChecked
            viewDataBinding.btnSubmit.isEnabled = viewModel.pin.value?.length == 6 && viewModel.rePin.value?.length == 6 && viewModel.pin.value == viewModel.rePin.value && viewModel.apiCallStatus.value != ApiCallStatus.LOADING && TermsAndConditionsFragment.isTermsAccepted == true
        }

        viewDataBinding.btnSubmit.setOnClickListener {
            hideKeyboard()
            registrationRemoteHelper.pin = viewModel.pin.value
            registrationRemoteHelper.retype_pin = viewModel.rePin.value
            registrationRemoteHelper.isAcceptedTandC = TermsAndConditionsFragment.isTermsAccepted
            if (registrationRemoteHelper.isRegistered == false) {
//                navigateTo(PinNumberFragmentDirections.actionPinNumberFragmentToProfileSignInFragment(
//                    registrationRemoteHelper
//                ))
                viewModel.registerNewUser(registrationRemoteHelper)
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
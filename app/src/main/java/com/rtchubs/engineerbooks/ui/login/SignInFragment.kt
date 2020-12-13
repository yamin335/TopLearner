package com.rtchubs.engineerbooks.ui.login

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.databinding.LayoutOperatorSelectionBinding
import com.rtchubs.engineerbooks.databinding.SignInBinding
import com.rtchubs.engineerbooks.models.registration.RegistrationHelperModel
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.AppConstants.commonErrorMessage
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast

class SignInFragment : BaseFragment<SignInBinding, SignInViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_sign_in
    override val viewModel: SignInViewModel by viewModels {
        viewModelFactory
    }

    val registrationHelper = RegistrationHelperModel()

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

        viewModel.mobileNo.observe(viewLifecycleOwner, Observer {  mobileNo ->
            mobileNo?.let {
                viewDataBinding.btnProceed.isEnabled = (it.length == 11) && (it[0] == '0')
            }
        })

        viewDataBinding.btnProceed.setOnClickListener {
            hideKeyboard()
            openOperatorSelectionDialog()
        }
    }

    private fun tempopenOperatorSelectionDialog() {
        val bottomSheetDialog = BottomSheetDialog(mActivity)
        val binding = DataBindingUtil.inflate<LayoutOperatorSelectionBinding>(
            layoutInflater,
            R.layout.layout_operator_selection,
            null,
            false
        )
        bottomSheetDialog.setContentView(binding.root)


        binding.btnBanglalink.setOnClickListener {
            bottomSheetDialog.dismiss()
            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
            navController.navigate(action)
        }

        binding.btnGrameenphone.setOnClickListener {
            bottomSheetDialog.dismiss()
            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
            navController.navigate(action)
        }

        binding.btnRobi.setOnClickListener {
            bottomSheetDialog.dismiss()
            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
            navController.navigate(action)
        }

        binding.btnTeletalk.setOnClickListener {
            bottomSheetDialog.dismiss()
            val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
            navController.navigate(action)
        }
        bottomSheetDialog.show()
    }

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
        registrationHelper.isRegistered = false
        registrationHelper.operator = operator
        inquireAccount()
    }

    private fun inquireAccount() {
        viewModel.inquireAccount().observe(viewLifecycleOwner, Observer { response ->
            response?.data?.Account?.let {
                registrationHelper.mobile = it.mobile ?: return@Observer
                if (it.isRegistered == false) {
                    val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
                    navController.navigate(action)
                    if (it.isAcceptedTandC == true) {

                    } else {

                    }
                } else if (it.isRegistered == true && it.isMobileVerified == true) {
                    registrationHelper.isRegistered = true
                    registrationHelper.isTermsAccepted = true
//                    val action = SignInFragmentDirections.actionSignInFragmentToTermsFragment(registrationHelper)
//                    navController.navigate(action)
                    val action = SignInFragmentDirections.actionSignInFragmentToOtpSignInFragment(registrationHelper)
                    navController.navigate(action)
                } else {
                    showErrorToast(mContext, response.msg ?: commonErrorMessage)
                }
            }
        })
    }
}
package com.rtchubs.engineerbooks.ui.pin_number

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.binding.FragmentDataBindingComponent
import com.rtchubs.engineerbooks.databinding.ResetPinDialogFragmentBinding
import com.rtchubs.engineerbooks.util.autoCleared
import dagger.android.support.DaggerDialogFragment

class ResetPinDialogFragment internal constructor(
    private val callBack: PinResetCallback
): DaggerDialogFragment() {

    private var binding by autoCleared<ResetPinDialogFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_reset_pin,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.etNewPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnSubmit.isEnabled = s.toString().length == 6 && binding.etNewRepin.text.toString().length == 6 && binding.etNewRepin.text.toString() == s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                
            }
        })

        binding.etNewPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnSubmit.isEnabled = binding.etNewPin.text.toString().length == 6 && s.toString().length == 6 && binding.etNewPin.text.toString() == s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                
            }
        })

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            callBack.onPinChanged(binding.etNewPin.text.toString())
        }
 
    }

    interface PinResetCallback {
        fun onPinChanged(pin: String)
    }
}
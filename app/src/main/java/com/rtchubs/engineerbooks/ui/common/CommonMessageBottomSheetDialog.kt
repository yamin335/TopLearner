package com.rtchubs.engineerbooks.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.binding.FragmentDataBindingComponent
import com.rtchubs.engineerbooks.databinding.CommonMessageBottomSheetDialogBinding
import com.rtchubs.engineerbooks.util.autoCleared

class CommonMessageBottomSheetDialog constructor(private val callback: CommonMessageClickListener?) : BottomSheetDialogFragment() {

    public var message: String = ""
    private var binding by autoCleared<CommonMessageBottomSheetDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_bottom_common_message,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.message = message
        binding.btnOk.setOnClickListener {
            dismiss()
        }
    }

    interface CommonMessageClickListener {
        fun onOkClicked()
    }
}
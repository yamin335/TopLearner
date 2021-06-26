package com.rtchubs.engineerbooks.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.binding.FragmentDataBindingComponent
import com.rtchubs.engineerbooks.databinding.DownloadOrEraseMessageBottomDialogBinding
import com.rtchubs.engineerbooks.util.autoCleared

class DownloadOrEraseMessageBottomSheetDialog constructor(
    private val downloadOrEraseCallback: ((Boolean) -> Unit),
    private val shouldDownload: Boolean = false) : BottomSheetDialogFragment() {

    private var binding by autoCleared<DownloadOrEraseMessageBottomDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_bottom_download_or_erase_message,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (shouldDownload) {
            binding.message = "ইন্টারনেট ছাড়া চলবে না"
            binding.btnText = "ডাউনলোড করুন"
            binding.tvMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.themeColorRed))
        } else {
            binding.message = "ইন্টারনেট ছাড়া চলবে"
            binding.btnText = "মেমোরি ফাঁকা করুন"
            binding.tvMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.toast_success))
        }
        binding.btnOk.setOnClickListener {
            downloadOrEraseCallback.invoke(shouldDownload)
            dismiss()
        }
    }
}
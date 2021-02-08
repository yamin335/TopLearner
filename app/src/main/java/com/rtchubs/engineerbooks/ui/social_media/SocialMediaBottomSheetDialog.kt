package com.rtchubs.engineerbooks.ui.social_media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.binding.FragmentDataBindingComponent
import com.rtchubs.engineerbooks.databinding.SocialMediaBottomSheetDialogBinding
import com.rtchubs.engineerbooks.models.social_media.SocialMedia
import com.rtchubs.engineerbooks.util.autoCleared

class SocialMediaBottomSheetDialog constructor(private val callback: SocialMediaClickListener) : BottomSheetDialogFragment() {

    private var binding by autoCleared<SocialMediaBottomSheetDialogBinding>()
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
            R.layout.dialog_bottom_social_media,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val socialMediaList = arrayListOf(SocialMedia(R.drawable.ic_facebook, "Facebook"), SocialMedia(R.drawable.ic_youtube, "Youtube"))
        val socialMediaAdapter = SocialMediaListAdapter {
            callback.onSocialMediaClick(it)
        }

        binding.socialRecycler.adapter = socialMediaAdapter
        socialMediaAdapter.submitList(socialMediaList)
    }

    interface SocialMediaClickListener {
        fun onSocialMediaClick(socialMedia: SocialMedia)
    }
}
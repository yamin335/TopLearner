package com.rtchubs.engineerbooks.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.daimajia.slider.library.SliderLayout
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.ViewPagerBinding
import com.rtchubs.engineerbooks.prefs.AppPreferencesHelper
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.isTimeAndZoneAutomatic
import com.rtchubs.engineerbooks.util.showErrorToast

class ViewPagerFragment : BaseFragment<ViewPagerBinding, ViewPagerViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_view_pager
    override val viewModel: ViewPagerViewModel by viewModels {
        viewModelFactory
    }

    var isDeviceTimeChanged = false

    var timeChangeListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            AppPreferencesHelper.KEY_DEVICE_TIME_CHANGED -> {
                isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged
                if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
                    //preferencesHelper.isDeviceTimeChanged = true
                    isDeviceTimeChanged = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferencesHelper.preference.registerOnSharedPreferenceChangeListener(timeChangeListener)
        isDeviceTimeChanged = preferencesHelper.isDeviceTimeChanged

        if (preferencesHelper.isDeviceTimeChanged || !isTimeAndZoneAutomatic(requireContext())) {
            preferencesHelper.isDeviceTimeChanged = true
            isDeviceTimeChanged = true
        }
    }

    override fun onPause() {
        super.onPause()
        preferencesHelper.preference.unregisterOnSharedPreferenceChangeListener(timeChangeListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.slideDataList.observe(viewLifecycleOwner, Observer {
            it?.let { ads ->
                viewDataBinding.sliderLayout.removeAllSliders()
                ads.forEach { slideData ->
                    val slide = SliderView(slideData, requireContext())
                    viewDataBinding.sliderLayout.addSlider(slide)
                }
            }
        })

        // set Slider Transition Animation
        viewDataBinding.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default)
        viewDataBinding.sliderLayout.startAutoCycle()

        // set Slider Transition Animation
        //viewDataBinding.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default)
        //viewDataBinding.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion)
        //viewDataBinding.sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)


        viewDataBinding.btnLogin.setOnClickListener {
            if (isDeviceTimeChanged) {
                showErrorToast(requireContext(), "Please fix the device time and try again!")
            } else {
                navController.navigate(ViewPagerFragmentDirections.actionViewPagerFragmentToSignInFragment())
            }
        }

        updateStatusBarBackgroundColor("#1E4356")

        viewModel.getAds()
    }
}
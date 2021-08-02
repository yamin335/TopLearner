package com.engineersapps.eapps.ui.login

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiEndPoint
import com.engineersapps.eapps.models.AdSlider

class SliderView(private val adSlider: AdSlider, mContext: Context) : BaseSliderView(mContext) {

    override fun getView(): View {
        val v: View = LayoutInflater.from(context).inflate(R.layout.custom_slide_view, null)
        val target: ImageView = v.findViewById(R.id.slideImage)
//        val titleText: TextView = v.findViewById(R.id.slideTitleText)

//        if (adSlider.title.isNullOrBlank()) {
//            titleText.visibility = View.GONE
//        } else {
//            titleText.visibility = View.VISIBLE
//            titleText.text = adSlider.title
//        }

        //val temp = "${ApiEndPoint.SLIDER_IMAGE}/${adSlider.logo}"
        Glide.with(context)
            .load("${ApiEndPoint.SLIDER_IMAGE}/${adSlider.logo}")
            .placeholder(R.drawable.slider_image_1)
            .into(target)

        return v
    }
}
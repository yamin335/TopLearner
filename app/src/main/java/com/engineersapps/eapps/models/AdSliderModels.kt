package com.engineersapps.eapps.models

data class AdSlider(val uuid: String?, val name: String?, val title: String?, val status: String?, val link: String?, val logo: String?, val is_archived: Int?)

data class AdSliderResponse(val code: Int?, val data: AdSliderData?, val msg: String?)

data class AdSliderData(val ads: List<AdSlider>?)
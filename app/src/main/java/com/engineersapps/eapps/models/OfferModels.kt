package com.engineersapps.eapps.models

data class OfferResponse(val code: Int?, val data: OfferResponseData?, val msg: String?)

data class OfferResponseData(val offers: List<Offer>?)

data class Offer(val udid: String?, val createdAt: String?, val updatedAt: String?,
                 val upazila_id: Int?, val city_id: Int?, val class_id: Int?,
                 val EndDate: String?, val FromDate: String?, val archived: Boolean?,
                 val offer_amount: Int?, val Table: String?)
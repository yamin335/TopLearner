package com.engineersapps.eapps.models.payment

data class PromoCodeResponse(val code: Int?, val data: PromoCodeResponseData?, val msg: String?)

data class PromoCodeResponseData(val isvalid: Boolean?, val promocode: PromoCode?)

data class PromoCode(val name: String?, val code: String?, val partner_id: Int?,
                     val discount: Int?, val status: String?, val price: Int?, val ExpiredDate: String?)
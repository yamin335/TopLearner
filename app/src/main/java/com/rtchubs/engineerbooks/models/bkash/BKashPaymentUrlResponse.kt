package com.rtchubs.engineerbooks.models.bkash

data class BKashPaymentUrlResponse(val code: Int?, val data: BKashPaymentUrlData?, val msg: String?)

data class BKashCreateResponse(val statusCode: String?, val mobile: String?, val invoicenumber: String?,
                          val statusMessage: String?, val paymentID: String?, val bkashURL: String?,
                          val callbackURL: String?, val successCallbackURL: String?,
                          val failureCallbackURL: String?, val payerReference: String?,
                          val agreementStatus: String?, val agreementCreateTime: String?, val isPaid: Boolean?)

data class BKashPaymentUrlData(val createresponse: BKashCreateResponse?)
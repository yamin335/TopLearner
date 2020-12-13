package com.rtchubs.engineerbooks.models.registration

data class InquiryAccount(val udid: String?, val createdAt: String?, val updatedAt: String?,
                          val firstName: String?, val lastName: String?, val mobile: String?,
                          val nidnumber: String?, val mobileOperator: String?, val displayName: String?,
                          val institute: String?, val upazila: String?, val city: String?, val nidFrontPic: String?,
                          val nidBackPic: String?, val altContactPerson: String?, val customer_type_id: Int?,
                          val email: String?, val pin: String?, val retypePin: String?, val gender: String?,
                          val profilePic: String?, val otp: String?, val otpExpire: String?, val status: String?,
                          val role: String?, val action: String?, val isRegistered: Boolean?,
                          val isMobileVerified: Boolean?, val isAcceptedTandC: Boolean?, val Table: String?)

data class InquiryResponse(val code: Int?, val data: InquiryData?, val msg: String?)

data class InquiryData(val Account: InquiryAccount?)
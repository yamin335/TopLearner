package com.rtchubs.engineerbooks.models.registration

import java.io.Serializable

data class InquiryAccount(var udid: String?, var createdAt: String?, var updatedAt: String?,
                          var firstName: String?, var lastName: String?, var mobile: String?,
                          var nidnumber: String?, var mobileOperator: String?, var displayName: String?,
                          var institute: String?, var upazila: String?, var city: String?, var nidFrontPic: String?,
                          var nidBackPic: String?, var altContactPerson: String?, var customer_type_id: Int?,
                          var email: String?, var pin: String?, var retypePin: String?, var gender: String?,
                          var profilePic: String?, var otp: String?, var otpExpire: String?, var status: String?,
                          var role: String?, var action: String?, var isRegistered: Boolean?,
                          var isMobileVerified: Boolean?, var isAcceptedTandC: Boolean?, var Table: String?): Serializable

data class InquiryResponse(val code: Int?, val data: InquiryData?, val msg: String?)

data class InquiryData(val Account: InquiryAccount?)
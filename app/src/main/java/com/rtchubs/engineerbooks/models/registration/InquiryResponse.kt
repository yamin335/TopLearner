package com.rtchubs.engineerbooks.models.registration

import java.io.Serializable

data class InquiryAccount(var udid: String?, var createdAt: String?, var updatedAt: String?, var id: Int?,
                          var first_name: String?, var last_name: String?, var mobile: String?,
                          var nidnumber: String?, var mobile_operator: String?, var class_id: Int?, var display_name: String?,
                          var institute: String?, var upazila: String?, var city: String?, var CityID: Int?,
                          var UpazilaID: Int?, var ClassName:String?, var rollnumber: String?, var nidFrontPic: String?,
                          var nidBackPic: String?, var altContactPerson: String?, var customer_type_id: Int?,
                          var email: String?, var pin: String?, var retype_pin: String?, var gender: String?,
                          var profilePic: String?, var otp: String?, var otpExpire: String?, var status: String?,
                          var role: String?, var action: String?, var isRegistered: Boolean?,
                          var isMobileVerified: Boolean?, var isAcceptedTandC: Boolean?,
                          var Table: String?, var address: String?, var Folder: String?,
                          var isSubscribed: Boolean? = true, var marital_status: String?,
                          var parmanent_address: String?, var present_address: String?,
                          var contact_person: String?, var StartDate: String?,
                          var BirthDate: String?, var EndDate: String?,
                          var occupation: String?, var spouse: String?,
                          var official_id: String?, var partner_type: String?,
                          var education: String?, var designation: String?,
                          var home_mobile: String?, var contact_personal: String?,
                          var mother_name: String?, var father_name: String?): Serializable

data class InquiryResponse(val code: Int?, val data: UserRegistrationData?, val msg: String?)
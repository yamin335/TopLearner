package com.rtchubs.engineerbooks.repos

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.ApiService
import com.rtchubs.engineerbooks.models.registration.*
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRepository @Inject constructor(private val apiService: ApiService, private val preferencesHelper: PreferencesHelper) {

    suspend fun inquireRepo(mobileNumber: String): Response<InquiryResponse> {
        return withContext(Dispatchers.IO) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("mobile", mobileNumber)
            val body = jsonObject.toString()
            apiService.inquire(body)
        }
    }

    suspend fun requestOTPCodeRepo(mobileNumber: String, isAcceptedTAndC: Boolean): Response<InquiryResponse> {
        return withContext(Dispatchers.IO) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("mobile", mobileNumber)
            jsonObject.addProperty("IsAcceptedTandC", isAcceptedTAndC)
            val body = jsonObject.toString()
            apiService.requestOTPCode(body)
        }
    }

    suspend fun verifyOTPCodeRepo(mobileNumber: String, isAcceptedTAndC: Boolean, otp: String): Response<InquiryResponse> {
        return withContext(Dispatchers.IO) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("mobile", mobileNumber)
            jsonObject.addProperty("IsAcceptedTandC", isAcceptedTAndC)
            jsonObject.addProperty("otp", otp)
            val body = jsonObject.toString()
            apiService.verifyOTPCode(body)
        }
    }

    suspend fun uploadProfilePhotosRepo(requestBody: RequestBody): Response<ProfileImageUploadResponse> {
        return withContext(Dispatchers.IO) {
            apiService.uploadProfilePhotos(requestBody)
        }
    }

    suspend fun registerUserRepo(inquiryAccount: InquiryAccount): Response<UserRegistrationResponse> {
        //val jsonObject = Gson().toJson(inquiryAccount) ?: ""
        val jsonObject = JsonObject().apply {
            addProperty("mobile", inquiryAccount.mobile)
            addProperty("mobileOperator", preferencesHelper.operator)
            addProperty("IsAcceptedTandC", inquiryAccount.isAcceptedTandC)
            addProperty("OTP", inquiryAccount.otp)
            //addProperty("OTP", "123")
            addProperty("FirstName", inquiryAccount.firstName)
            addProperty("LastName", inquiryAccount.lastName)
//            addProperty("Pin", "123456")
//            addProperty("RetypePin", "123456")
            addProperty("Pin", inquiryAccount.pin)
            addProperty("RetypePin", inquiryAccount.retypePin)
            addProperty("Gender", inquiryAccount.gender)
            //addProperty("Customer_type_id", inquiryAccount.customer_type_id)
            addProperty("Customer_type_id", 1)
            addProperty("address1", inquiryAccount.address)
            addProperty("profilepic", inquiryAccount.profilePic)
            addProperty("nidfront", inquiryAccount.nidFrontPic)
            addProperty("nidback", inquiryAccount.nidBackPic)
        }
        return withContext(Dispatchers.IO) {
            apiService.registerUser(jsonObject.toString())
        }
    }

    suspend fun updateUserProfileRepo(inquiryAccount: InquiryAccount, token: String): Response<UserRegistrationResponse> {
        //val jsonObject = Gson().toJson(inquiryAccount) ?: ""
        val jsonObject = JsonObject().apply {
            addProperty("mobile", inquiryAccount.mobile)
            addProperty("mobileOperator", preferencesHelper.operator)
            addProperty("IsAcceptedTandC", inquiryAccount.isAcceptedTandC)
            addProperty("OTP", inquiryAccount.otp)
            //addProperty("OTP", "123")
            addProperty("FirstName", inquiryAccount.firstName)
            addProperty("LastName", inquiryAccount.lastName)
//            addProperty("Pin", "123456")
//            addProperty("RetypePin", "123456")
            addProperty("Pin", inquiryAccount.pin)
            addProperty("RetypePin", inquiryAccount.retypePin)
            addProperty("Gender", inquiryAccount.gender)
            //addProperty("Customer_type_id", inquiryAccount.customer_type_id)
            addProperty("Customer_type_id", 1)
            addProperty("address1", inquiryAccount.address)
            addProperty("profilepic", inquiryAccount.profilePic)
            addProperty("nidfront", inquiryAccount.nidFrontPic)
            addProperty("nidback", inquiryAccount.nidBackPic)
            addProperty("token", token)
        }
        return withContext(Dispatchers.IO) {
            apiService.updateUserProfile(jsonObject.toString())
        }
    }

    suspend fun getDistrictRepo(): Response<DistrictResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getDistrict()
        }
    }

    suspend fun getUpazillaRepo(districtID: String): Response<UpazillaResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getUpazilla(districtID)
        }
    }

    suspend fun getAcademicClassRepo(): Response<AcademicClassResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getAcademicClasses()
        }
    }







    suspend fun requestOTPRepo(mobileNumber: String, hasGivenConsent: String): Response<DefaultResponse> {
        return withContext(Dispatchers.IO) {
            apiService.requestOTP(
                mobileNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                hasGivenConsent.toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }
    }

    suspend fun registrationRepo(mobileNumber: String, otp: String,
                                 password: String, fullName: String,
                                 mobileOperator: String, deviceId: String,
                                 deviceName: String, deviceModel: String,
                                 nidNumber: String,
                                 nidFrontImage: MultipartBody.Part?,
                                 nidBackImage: MultipartBody.Part?,
                                 userImage: MultipartBody.Part?): Response<DefaultResponse> {

        return withContext(Dispatchers.IO) {
            apiService.registration(
                mobileNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                otp.toRequestBody("text/plain".toMediaTypeOrNull()),
                password.toRequestBody("text/plain".toMediaTypeOrNull()),
                fullName.toRequestBody("text/plain".toMediaTypeOrNull()),
                mobileOperator.toRequestBody("text/plain".toMediaTypeOrNull()),
                deviceId.toRequestBody("text/plain".toMediaTypeOrNull()),
                deviceName.toRequestBody("text/plain".toMediaTypeOrNull()),
                deviceModel.toRequestBody("text/plain".toMediaTypeOrNull()),
                userImage,
                nidNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                nidFrontImage, nidBackImage
            )
        }
    }
}
package com.rtchubs.engineerbooks.repos

import com.google.gson.JsonObject
import com.rtchubs.engineerbooks.api.ApiService
import com.rtchubs.engineerbooks.models.registration.InquiryResponse
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
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
class RegistrationRepository @Inject constructor(private val apiService: ApiService) {

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

    suspend fun uploadProfilePhotosRepo(requestBody: RequestBody): Response<String> {
        return withContext(Dispatchers.IO) {
            apiService.uploadProfilePhotos(requestBody)
        }
    }

    suspend fun registerUserRepo(body: String): Response<String> {
        return withContext(Dispatchers.IO) {
            apiService.registerUser(body)
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
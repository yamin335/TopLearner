package com.rtchubs.engineerbooks.repos

import com.rtchubs.engineerbooks.api.ApiService
import com.rtchubs.engineerbooks.models.registration.InquiryResponse
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun requestOTPRepo(
        mobileNumber: String,
        hasGivenConsent: String
    ): Response<DefaultResponse> {
        return withContext(Dispatchers.IO) {
            apiService.requestOTP(
                mobileNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                hasGivenConsent.toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }
    }

    suspend fun loginRepo(
        userName: String,
        password: String,
        grantType: String,
        scope: String,
        deviceID: String,
        deviceName: String,
        deviceModel: String,
        clientID: String,
        clientSecret: String,
        otp: String
    ): Response<String> {

        return withContext(Dispatchers.IO) {
            apiService.connectToken(
                userName,
                password,
                grantType,
                scope,
                deviceID,
                deviceName,
                deviceModel,
                clientID,
                clientSecret,
                otp
            )
        }
    }
}
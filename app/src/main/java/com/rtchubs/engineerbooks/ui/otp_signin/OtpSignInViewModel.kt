package com.rtchubs.engineerbooks.ui.otp_signin

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.registration.InquiryResponse
import com.rtchubs.engineerbooks.repos.RegistrationRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class OtpSignInViewModel @Inject constructor(private val application: Application, private val repository: RegistrationRepository) : BaseViewModel(application) {

    val otp: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val registeredOTP: MutableLiveData<InquiryResponse> by lazy {
        MutableLiveData<InquiryResponse>()
    }

    val verifiedOTP: MutableLiveData<InquiryResponse> by lazy {
        MutableLiveData<InquiryResponse>()
    }

    fun requestOTPCode(registrationHelper: InquiryAccount) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
                registeredOTP.postValue(null)
            }

            val mobile = registrationHelper.mobile ?: "0"
            val isAcceptedTandC = registrationHelper.isAcceptedTandC ?: false

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.requestOTPCodeRepo(mobile, isAcceptedTandC))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        registeredOTP.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        registeredOTP.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        registeredOTP.postValue(null)
                    }
                }
            }
        }
    }

    fun verifyOTPCode(registrationHelper: InquiryAccount) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
                verifiedOTP.postValue(null)
            }

            val mobile = registrationHelper.mobile ?: "0"
            val isAcceptedTandC = registrationHelper.isAcceptedTandC ?: false
            val otpCode = otp.value ?: ""

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.verifyOTPCodeRepo(mobile, isAcceptedTandC, otpCode))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        verifiedOTP.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        verifiedOTP.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        verifiedOTP.postValue(null)
                    }
                }
            }
        }
    }

//    val defaultResponse: MutableLiveData<DefaultResponse> = MutableLiveData()
//
//    fun requestOTP(mobileNumber: String, hasGivenConsent: String) {
//        if (checkNetworkStatus()) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(repository.requestOTPRepo(mobileNumber, hasGivenConsent))) {
//                    is ApiSuccessResponse -> {
//                        Log.e("ress",apiResponse.body.body.toString())
//                        defaultResponse.postValue(apiResponse.body)
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        defaultResponse.postValue(Gson().fromJson(apiResponse.errorMessage, DefaultResponse::class.java))
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        }
//    }
//
//    fun requestOTPForRegisteredUser(mobileNumber: String, deviceId: String) {
//        if (checkNetworkStatus()) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
////            viewModelScope.launch(handler) {
////                when (val apiResponse = ApiResponse.create(repository.inquireRepo(mobileNumber, deviceId))) {
////                    is ApiSuccessResponse -> {
////                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
////                        registeredOTP.postValue(apiResponse.body)
////                    }
////                    is ApiEmptyResponse -> {
////                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
////                    }
////                    is ApiErrorResponse -> {
////                        registeredOTP.postValue(Gson().fromJson(apiResponse.errorMessage, InquiryResponse::class.java))
////                        apiCallStatus.postValue(ApiCallStatus.ERROR)
////                    }
////                }
////            }
//        }
//    }
}
package com.rtchubs.engineerbooks.ui.pin_number

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.registration.InquiryResponse
import com.rtchubs.engineerbooks.models.registration.UserRegistrationData
import com.rtchubs.engineerbooks.repos.RegistrationRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PinNumberViewModel @Inject constructor(
    private val application: Application,
    private val repository: RegistrationRepository
) : BaseViewModel(application) {
    val pin: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val rePin: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val loginResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
    }

    val verifiedOTP: MutableLiveData<InquiryResponse> by lazy {
        MutableLiveData<InquiryResponse>()
    }

    val resetPinResponse: MutableLiveData<InquiryResponse> by lazy {
        MutableLiveData<InquiryResponse>()
    }

    val invalidPin: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun loginUser(inquiryAccount: InquiryAccount) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.loginUserRepo(inquiryAccount))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        loginResponse.postValue(apiResponse.body.data)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        if (apiResponse.errorMessage.contains("Incorrect Pin")) {
                            invalidPin.postValue(true)
                        }
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
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
                verifiedOTP.postValue(null)
            }

            val mobile = registrationHelper.mobile ?: "0"
            val isAcceptedTandC = registrationHelper.isAcceptedTandC ?: false
            val otpCode = registrationHelper.otp ?: ""

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

    fun resetPin(mobileNumber: String, pin: String) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
                resetPinResponse.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.resetPinRepo(mobileNumber, pin, pin))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        resetPinResponse.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        resetPinResponse.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        resetPinResponse.postValue(null)
                    }
                }
            }
        }
    }

//    fun connectToken(
//        userName: String,
//        password: String,
//        grantType: String,
//        scope: String,
//        deviceID: String,
//        deviceName: String,
//        deviceModel: String,
//        clientID: String,
//        clientSecret: String,
//        otp: String
//    ) {
//        if (checkNetworkStatus()) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse =
//                    ApiResponse.create(
//                        repository.loginRepo(
//                            userName,
//                            password,
//                            grantType,
//                            scope,
//                            deviceID,
//                            deviceName,
//                            deviceModel,
//                            clientID,
//                            clientSecret,
//                            otp
//                        )
//                    )) {
//                    is ApiSuccessResponse -> {
//                        defaultResponse.postValue(
//                            DefaultResponse(
//                                apiResponse.body.toString(),
//                                "",
//                                "",
//                                true
//                            )
//                        )
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        defaultResponse.postValue(
//                            Gson().fromJson(
//                                apiResponse.errorMessage,
//                                DefaultResponse::class.java
//                            )
//                        )
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        }
//    }
}
package com.rtchubs.engineerbooks.ui.pin_number

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.registration.UserRegistrationData
import com.rtchubs.engineerbooks.repos.LoginRepository
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
    val defaultResponse: MutableLiveData<DefaultResponse> = MutableLiveData()

    val loginResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
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
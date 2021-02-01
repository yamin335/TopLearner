package com.rtchubs.engineerbooks.ui.bkash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.registration.UserRegistrationData
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.models.transactions.Salesinvoice
import com.rtchubs.engineerbooks.repos.LoginRepository
import com.rtchubs.engineerbooks.repos.RegistrationRepository
import com.rtchubs.engineerbooks.repos.TransactionRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class BKashViewModel @Inject constructor(
    private val application: Application,
    private val repository: TransactionRepository
) : BaseViewModel(application) {

    val salesInvoice: MutableLiveData<Salesinvoice> by lazy {
        MutableLiveData<Salesinvoice>()
    }

    fun createOrder(createOrderBody: CreateOrderBody) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.createOrderRepo(createOrderBody))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        salesInvoice.postValue(apiResponse.body.data?.salesinvoice)
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
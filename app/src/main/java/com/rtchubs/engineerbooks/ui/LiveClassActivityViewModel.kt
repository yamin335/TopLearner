package com.rtchubs.engineerbooks.ui

import android.app.Application
import com.rtchubs.engineerbooks.repos.RegistrationRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import javax.inject.Inject

class LiveClassActivityViewModel @Inject constructor(private val application: Application, private val repository: RegistrationRepository) : BaseViewModel(application) {
//    val userProfileInfo: MutableLiveData<InquiryAccount> by lazy {
//        MutableLiveData<InquiryAccount>()
//    }

//    fun getUserProfileInfo(mobileNumber: String) {
//        if (checkNetworkStatus(false)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//                userProfileInfo.postValue(null)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(repository.getUserInfo(mobileNumber))) {
//                    is ApiSuccessResponse -> {
//                        userProfileInfo.postValue(apiResponse.body.data?.Account)
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                        userProfileInfo.postValue(null)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                        userProfileInfo.postValue(null)
//                    }
//                }
//            }
//        }
//    }
}
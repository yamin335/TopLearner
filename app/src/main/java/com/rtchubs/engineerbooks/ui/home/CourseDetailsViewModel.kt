package com.rtchubs.engineerbooks.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.faq.Faq
import com.rtchubs.engineerbooks.repos.HomeRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class CourseDetailsViewModel @Inject constructor(
    private val application: Application,
    private val homeRepository: HomeRepository
    ) : BaseViewModel(application) {

    val allFaqList: MutableLiveData<List<Faq>> by lazy {
        MutableLiveData<List<Faq>>()
    }

    fun getAllFaqs() {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.allFaqRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        if (apiResponse.body.code != ResponseCodes.CODE_SUCCESS) {
                            toastError.postValue(apiResponse.body.message ?: AppConstants.serverConnectionErrorMessage)
                            return@launch
                        }
                        if (apiResponse.body.data?.faqs.isNullOrEmpty()) {
                            toastError.postValue(apiResponse.body.message ?: AppConstants.noCourseFoundMessage)
                            return@launch
                        }
                        allFaqList.postValue(apiResponse.body.data?.faqs)
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
}
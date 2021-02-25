package com.rtchubs.engineerbooks.ui.live_class_schedule

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.LiveClassSchedule
import com.rtchubs.engineerbooks.repos.AdminRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class LiveClassScheduleViewModel @Inject constructor(
    private val application: Application,
    private val adminRepository: AdminRepository
    ) : BaseViewModel(application) {

    val liveClassList: MutableLiveData<List<LiveClassSchedule>> by lazy {
        MutableLiveData<List<LiveClassSchedule>>()
    }

    fun getAllLiveClasses(classTypeID: Int) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(adminRepository.liveClassScheduleRepo(classTypeID))) {
                    is ApiSuccessResponse -> {
                        liveClassList.postValue(apiResponse.body.data?.liveclassschedule)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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
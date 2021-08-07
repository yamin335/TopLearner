package com.engineersapps.eapps.ui.live_video

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.LiveClassSchedule
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class LiveVideoViewModel @Inject constructor(
    private val application: Application,
    private val homeRepository: HomeRepository
) : BaseViewModel(application) {
    val liveClassList: MutableLiveData<List<LiveClassSchedule>> by lazy {
        MutableLiveData<List<LiveClassSchedule>>()
    }

    fun getAllLiveClasses(classID: Int?) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.liveClassScheduleRepo(classID))) {
                    is ApiSuccessResponse -> {
                        liveClassList.postValue(apiResponse.body.data?.live_class_schedules)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }
}
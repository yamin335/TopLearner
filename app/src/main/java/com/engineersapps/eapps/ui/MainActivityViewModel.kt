package com.engineersapps.eapps.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.MyCourseDao
import com.engineersapps.eapps.models.my_course.MyCourse
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.registration.UserRegistrationData
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val application: Application,
    private val repository: RegistrationRepository,
    private val myCourseDao: MyCourseDao
) : BaseViewModel(application) {

    val allMyCoursesFromDB: LiveData<List<MyCourse>> = liveData {
        myCourseDao.getAllMyCourses().collect { list ->
            emit(list)
        }
    }

    val loginResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
    }

    fun loginUser(inquiryAccount: InquiryAccount) {
        if (checkNetworkStatus(true)) {
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
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }
}
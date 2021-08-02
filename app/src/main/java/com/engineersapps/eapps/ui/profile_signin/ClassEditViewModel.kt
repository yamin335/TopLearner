package com.engineersapps.eapps.ui.profile_signin

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClassEditViewModel @Inject constructor(private val application: Application, private val repository: RegistrationRepository) : BaseViewModel(application) {
    val allAcademicClass: MutableLiveData<List<AcademicClass>> by lazy {
        MutableLiveData<List<AcademicClass>>()
    }

    fun getAcademicClass() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.getAcademicClassRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allAcademicClass.postValue(apiResponse.body.data?.classes)
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
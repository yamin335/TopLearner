package com.engineersapps.eapps.ui.profile_signin

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.registration.Upazilla
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpazillaEditViewModel @Inject constructor(private val application: Application, private val repository: RegistrationRepository) : BaseViewModel(application) {
    val allUpazilla: MutableLiveData<List<Upazilla>> by lazy {
        MutableLiveData<List<Upazilla>>()
    }

    fun getUpazilla(districtID: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.getUpazillaRepo(districtID))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allUpazilla.postValue(apiResponse.body.data?.upazilas)
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
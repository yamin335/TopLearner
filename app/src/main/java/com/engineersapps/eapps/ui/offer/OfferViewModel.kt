package com.engineersapps.eapps.ui.offer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.Offer
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class OfferViewModel @Inject constructor(
    private val application: Application,
    private val homeRepository: HomeRepository
) : BaseViewModel(application) {

    val offers: MutableLiveData<List<Offer>> by lazy {
        MutableLiveData<List<Offer>>()
    }

    fun getAllOffers(cityId: Int?, upazillaId: Int?) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.offerRepo(cityId, upazillaId))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        offers.postValue(apiResponse.body.data?.offers)
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
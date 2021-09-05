package com.engineersapps.eapps.ui.more

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.payment.PromoCode
import com.engineersapps.eapps.repos.TransactionRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class MoreViewModel @Inject constructor(
    private val application: Application,
    private val repository: TransactionRepository
    ): BaseViewModel(application) {

    val promoCode: MutableLiveData<Pair<Boolean, PromoCode?>> by lazy {
        MutableLiveData<Pair<Boolean, PromoCode?>>()
    }

    fun verifyPromoCode(promoCodeText: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.promoCodeRepo(promoCodeText))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        if (apiResponse.body.data?.isvalid == true) {
                            promoCode.postValue(Pair(true, apiResponse.body.data.promocode))
                        } else {
                            promoCode.postValue(Pair(false, null))
                        }
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
package com.rtchubs.engineerbooks.ui.payment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.Offer
import com.rtchubs.engineerbooks.models.bkash.BKashCreateResponse
import com.rtchubs.engineerbooks.models.payment.CoursePaymentRequest
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.models.transactions.Salesinvoice
import com.rtchubs.engineerbooks.repos.HomeRepository
import com.rtchubs.engineerbooks.repos.TransactionRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentViewModel @Inject constructor(private val application: Application,
                                           private val repository: TransactionRepository,
                                           private val homeRepository: HomeRepository) : BaseViewModel(application) {

    val amount: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val salesInvoice: MutableLiveData<Salesinvoice> by lazy {
        MutableLiveData<Salesinvoice>()
    }

    val offers: MutableLiveData<List<Offer>> by lazy {
        MutableLiveData<List<Offer>>()
    }

    val bkashUrl: MutableLiveData<BKashCreateResponse> by lazy {
        MutableLiveData<BKashCreateResponse>()
    }

    fun getBkashPaymentUrl(mobile: String?, amount: String?, invoiceNumber: String?): MutableLiveData<BKashCreateResponse> {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                offers.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.bkashPaymentUrlRepo(mobile, amount, invoiceNumber))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        bkashUrl.postValue(apiResponse.body.data?.createresponse)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        offers.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        offers.postValue(null)
                    }
                }
            }
        }
        return bkashUrl
    }

    fun getAllOffers(cityId: Int?, upazillaId: Int?) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                offers.postValue(null)
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
                        offers.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        offers.postValue(null)
                    }
                }
            }
        }
    }

    fun createOrder(createOrderBody: CreateOrderBody) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
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

    fun purchaseCourse(createOrderBody: CreateOrderBody, coursePaymentRequest: CoursePaymentRequest) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.purchaseCourseRepo(coursePaymentRequest))) {
                    is ApiSuccessResponse -> {
                        createOrder(createOrderBody)
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
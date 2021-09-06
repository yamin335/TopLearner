package com.engineersapps.eapps.ui.payment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.Offer
import com.engineersapps.eapps.models.bkash.BKashCreateResponse
import com.engineersapps.eapps.models.payment.CoursePaymentRequest
import com.engineersapps.eapps.models.payment.PromoCode
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.transactions.CreateOrderBody
import com.engineersapps.eapps.models.transactions.Salesinvoice
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.repos.TransactionRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentViewModel @Inject constructor(private val application: Application,
                                           private val repository: TransactionRepository,
                                           private val homeRepository: HomeRepository) : BaseViewModel(application) {

    val isValidPromoCode: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val promoCodeText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var promoCodeDiscount: Double = 0.0

    val promoPartner: MutableLiveData<InquiryAccount> by lazy {
        MutableLiveData<InquiryAccount>()
    }

    val promoCode: MutableLiveData<PromoCode> by lazy {
        MutableLiveData<PromoCode>()
    }

    val packagePrice: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val discount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val amount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
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

    val coursePurchaseSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
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
                        checkForValidSession(apiResponse.errorMessage)
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
                        checkForValidSession(apiResponse.errorMessage)
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
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }

    fun purchaseCourse(createOrderBody: CreateOrderBody?, coursePaymentRequest: CoursePaymentRequest) {
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
                        if (createOrderBody == null) {
                            apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                            coursePurchaseSuccess.postValue(true)
                        }
                        createOrderBody?.let {
                            createOrder(it)
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

    fun verifyPromoCode() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.promoCodeRepo(promoCodeText.value))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        if (apiResponse.body.data?.isvalid == true) {
                            isValidPromoCode.postValue(true)
                            promoPartner.postValue(apiResponse.body.data.partner)
                            promoCode.postValue(apiResponse.body.data.promocode)
                        } else {
                            isValidPromoCode.postValue(false)
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
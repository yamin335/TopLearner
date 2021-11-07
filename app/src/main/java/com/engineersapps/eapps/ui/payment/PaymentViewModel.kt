package com.engineersapps.eapps.ui.payment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.payment.PromoCode
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.transactions.CreateOrderBody
import com.engineersapps.eapps.models.transactions.SalesInvoice
import com.engineersapps.eapps.prefs.PreferencesHelper
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.repos.TransactionRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentViewModel @Inject constructor(private val application: Application,
                                           private val repository: TransactionRepository,
                                           private val regRepository: RegistrationRepository
) : BaseViewModel(application) {

    var promoDiscountPercentage = 0
    var profileDiscountPercentage = 0

    val isValidPromoCode: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val promoCodeText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    //var promoCodeDiscount: Int = 0

    val promoPartner: MutableLiveData<InquiryAccount> by lazy {
        MutableLiveData<InquiryAccount>()
    }

    val promoCode: MutableLiveData<PromoCode> by lazy {
        MutableLiveData<PromoCode>()
    }

    val packagePrice: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val profileDiscount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

//    val cityDiscount: MutableLiveData<Int> by lazy {
//        MutableLiveData<Int>()
//    }

    val promoDiscount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val totalDiscount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val amount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val salesInvoice: MutableLiveData<SalesInvoice> by lazy {
        MutableLiveData<SalesInvoice>()
    }

    val userProfileInfo: MutableLiveData<InquiryAccount> by lazy {
        MutableLiveData<InquiryAccount>()
    }

//    val offers: MutableLiveData<List<Offer>> by lazy {
//        MutableLiveData<List<Offer>>()
//    }

//    val bkashUrl: MutableLiveData<BKashCreateResponse> by lazy {
//        MutableLiveData<BKashCreateResponse>()
//    }

    val coursePurchaseSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

//    fun getBkashPaymentUrl(mobile: String?, amount: String?, invoiceNumber: String?): MutableLiveData<BKashCreateResponse> {
//        if (checkNetworkStatus(true)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                offers.postValue(null)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(repository.bkashPaymentUrlRepo(mobile, amount, invoiceNumber))) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        bkashUrl.postValue(apiResponse.body.data?.createresponse)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                        offers.postValue(null)
//                    }
//                    is ApiErrorResponse -> {
//                        checkForValidSession(apiResponse.errorMessage)
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                        offers.postValue(null)
//                    }
//                }
//            }
//        }
//        return bkashUrl
//    }

//    fun getAllOffers(cityId: Int?, upazillaId: Int?) {
//        if (checkNetworkStatus(true)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                offers.postValue(null)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(homeRepository.offerRepo(cityId, upazillaId))) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        offers.postValue(apiResponse.body.data?.offers)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                        offers.postValue(null)
//                    }
//                    is ApiErrorResponse -> {
//                        checkForValidSession(apiResponse.errorMessage)
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                        offers.postValue(null)
//                    }
//                }
//            }
//        }
//    }

    fun createOrder(preferencesHelper: PreferencesHelper, createOrderBody: CreateOrderBody) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
                preferencesHelper.pendingCoursePurchase = createOrderBody
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
                        preferencesHelper.pendingCoursePurchase = createOrderBody
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        preferencesHelper.pendingCoursePurchase = createOrderBody
                    }
                }
            }
        }
    }

//    fun purchaseCourse(preferencesHelper: PreferencesHelper, createOrderBody: CreateOrderBody?, coursePaymentRequest: CoursePaymentRequest) {
//        if (checkNetworkStatus(true)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(repository.purchaseCourseRepo(coursePaymentRequest))) {
//                    is ApiSuccessResponse -> {
//                        if (createOrderBody == null) {
//                            apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                            coursePurchaseSuccess.postValue(true)
//                        }
//                        createOrderBody?.let {
//                            preferencesHelper.pendingCoursePurchase?.let { pendingPurchase ->
//                                preferencesHelper.pendingCoursePurchase = MyCoursePurchasePayload(pendingPurchase.createOrderBody, null)
//                            }
//                            createOrder(it)
//                        }
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        checkForValidSession(apiResponse.errorMessage)
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        }
//    }

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

    fun getUserProfileInfo(mobileNumber: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
                userProfileInfo.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(regRepository.getUserInfo(mobileNumber))) {
                    is ApiSuccessResponse -> {
                        userProfileInfo.postValue(apiResponse.body.data?.Account)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        userProfileInfo.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        userProfileInfo.postValue(null)
                    }
                }
            }
        }
    }

}
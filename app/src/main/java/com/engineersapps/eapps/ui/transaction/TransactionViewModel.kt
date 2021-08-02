package com.engineersapps.eapps.ui.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.models.transactions.PartnerTransaction
import com.engineersapps.eapps.models.transactions.PaymentStatusData
import com.engineersapps.eapps.models.transactions.Transaction
import com.engineersapps.eapps.repos.TransactionRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransactionViewModel @Inject constructor(
    private val application: Application,
    private val repository: TransactionRepository
) : BaseViewModel(application) {

    val salesInvoice: MutableLiveData<List<Transaction>> by lazy {
        MutableLiveData<List<Transaction>>()
    }

    val adminTransactionResponse: MutableLiveData<List<PartnerTransaction>> by lazy {
        MutableLiveData<List<PartnerTransaction>>()
    }

    val partnerPaymentStatus: MutableLiveData<PaymentStatusData> by lazy {
        MutableLiveData<PaymentStatusData>()
    }

    fun getAllTransaction(studentId: Int) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.transactionsRepo(studentId))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        salesInvoice.postValue(apiResponse.body.data?.transactions)
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

    fun getAdminTransactions(mobile: String) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.partnerTransactionsRepo(mobile))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        adminTransactionResponse.postValue(apiResponse.body.data?.payments)
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

    fun getPartnerPaymentStatus(mobile: String?, cityID: Int?, upazillaID: Int?) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.partnerPaymentStatusRepo(mobile, cityID, upazillaID))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        partnerPaymentStatus.postValue(apiResponse.body.data)
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
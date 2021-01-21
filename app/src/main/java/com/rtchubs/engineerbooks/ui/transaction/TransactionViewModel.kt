package com.rtchubs.engineerbooks.ui.transaction

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.registration.InquiryResponse
import com.rtchubs.engineerbooks.models.transactions.AdminPayHistory
import com.rtchubs.engineerbooks.models.transactions.CreateOrderBody
import com.rtchubs.engineerbooks.models.transactions.Salesinvoice
import com.rtchubs.engineerbooks.models.transactions.Transaction
import com.rtchubs.engineerbooks.repos.RegistrationRepository
import com.rtchubs.engineerbooks.repos.TransactionRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import com.rtchubs.engineerbooks.util.AppConstants.serverConnectionErrorMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransactionViewModel @Inject constructor(private val application: Application, private val repository: TransactionRepository) : BaseViewModel(application) {

    val salesInvoice: MutableLiveData<List<Transaction>> by lazy {
        MutableLiveData<List<Transaction>>()
    }

    val adminTransactionResponse: MutableLiveData<List<Transaction>> by lazy {
        MutableLiveData<List<Transaction>>()
    }

    fun getAllTransaction(studentId: Int) {
        if (checkNetworkStatus()) {
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

    fun getAdminTransactions(city_id: Int, upazila_id: Int) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.adminTransactionsRepo(city_id, upazila_id))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        adminTransactionResponse.postValue(apiResponse.body.data?.transactions)
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
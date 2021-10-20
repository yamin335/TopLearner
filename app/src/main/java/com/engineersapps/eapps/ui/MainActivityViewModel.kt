package com.engineersapps.eapps.ui

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.*
import com.engineersapps.eapps.models.payment.CoursePaymentRequest
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.registration.UserRegistrationData
import com.engineersapps.eapps.models.transactions.CreateOrderBody
import com.engineersapps.eapps.models.transactions.MyCoursePurchasePayload
import com.engineersapps.eapps.prefs.PreferencesHelper
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.repos.TransactionRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import com.engineersapps.eapps.util.NetworkUtils.ConnectivityLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val application: Application,
    private val repository: RegistrationRepository,
    private val transactionRepository: TransactionRepository,
    private val registrationRepository: RegistrationRepository,
    private val myCourseDao: MyCourseDao,
    private val bookChapterDao: BookChapterDao,
    private val courseDao: CourseDao,
    private val academicClassDao: AcademicClassDao,
    private val historyDao: HistoryDao
) : BaseViewModel(application) {

    val isPendingCoursePurchaseSuccess: MutableLiveData<Boolean?> by lazy {
        MutableLiveData<Boolean?>()
    }

    val internetStatus: ConnectivityLiveData by lazy {
        ConnectivityLiveData(application)
    }


    val loginResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
    }

    val profileUpdateResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
    }

    val allAcademicClass: LiveData<List<AcademicClass>> = liveData {
        academicClassDao.getAllClasses().collect { list ->
            emit(list)
        }
    }

    fun getMyCourseItemCount(): LiveData<Int> {
        val count = MutableLiveData<Int>()
        viewModelScope.launch {
            count.postValue(myCourseDao.getMyCourseItemsCount())
        }
        return count
    }

    private fun updateClassesInDB(classes: List<AcademicClass>) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                academicClassDao.updateAllAcademicClasses(classes)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun getAllAcademicClassesFromDB(): LiveData<List<AcademicClass>> {
        val classList = MutableLiveData<List<AcademicClass>>()
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                classList.postValue(academicClassDao.getAllAcademicClasses())
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return classList
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

    fun getAcademicClass() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(registrationRepository.getAcademicClassRepo())) {
                    is ApiSuccessResponse -> {
                        updateClassesInDB(apiResponse.body.data?.classes ?: ArrayList())
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                    }
                }
            }
        }
    }

    fun updateUserProfile(inquiryAccount: InquiryAccount) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                profileUpdateResponse.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(registrationRepository.updateUserProfileRepo(inquiryAccount))) {
                    is ApiSuccessResponse -> {
                        profileUpdateResponse.postValue(apiResponse.body.data)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        profileUpdateResponse.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        profileUpdateResponse.postValue(null)
                    }
                }
            }
        }
    }

    @Transaction
    fun clearAllData(): LiveData<Boolean> {
        val task: MutableLiveData<Boolean> = MutableLiveData()
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
            apiCallStatus.postValue(ApiCallStatus.ERROR)
            toastError.postValue(AppConstants.serverConnectionErrorMessage)
        }
        viewModelScope.launch(handler) {
            courseDao.deleteAllCourseCategories()
            bookChapterDao.deleteAllBooks()
            bookChapterDao.deleteAllChapters()
            myCourseDao.deleteAllMyCourses()
            myCourseDao.deleteAllMyCoursePaidBooks()
            academicClassDao.deleteAllClasses()
            historyDao.deleteAllHistory()
            task.postValue(true)
        }
        return task
    }

    fun createOrder(createOrderBody: CreateOrderBody) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(transactionRepository.createOrderRepo(createOrderBody))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        isPendingCoursePurchaseSuccess.postValue(true)
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

    fun purchaseCourse(preferencesHelper: PreferencesHelper, createOrderBody: CreateOrderBody?,
                       coursePaymentRequest: CoursePaymentRequest) {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(transactionRepository.purchaseCourseRepo(coursePaymentRequest))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        createOrderBody?.let {
                            preferencesHelper.pendingCoursePurchase?.let { pendingPurchase ->
                                preferencesHelper.pendingCoursePurchase = MyCoursePurchasePayload(pendingPurchase.createOrderBody, null)
                            }
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
}
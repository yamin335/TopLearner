package com.engineersapps.eapps.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.*
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.models.registration.UserRegistrationData
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val application: Application,
    private val repository: RegistrationRepository,
    private val myCourseDao: MyCourseDao,
    private val bookChapterDao: BookChapterDao,
    private val courseDao: CourseDao,
    private val academicClassDao: AcademicClassDao,
    private val historyDao: HistoryDao
) : BaseViewModel(application) {

    val loginResponse: MutableLiveData<UserRegistrationData> by lazy {
        MutableLiveData<UserRegistrationData>()
    }

    fun getMyCourseItemCount(): LiveData<Int> {
        val count = MutableLiveData<Int>()
        viewModelScope.launch {
            count.postValue(myCourseDao.getMyCourseItemsCount())
        }
        return count
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
}
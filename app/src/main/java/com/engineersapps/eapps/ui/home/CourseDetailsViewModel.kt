package com.engineersapps.eapps.ui.home

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.BookChapterDao
import com.engineersapps.eapps.models.faq.Faq
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class CourseDetailsViewModel @Inject constructor(
    private val application: Application,
    private val homeRepository: HomeRepository,
    private val bookChapterDao: BookChapterDao
    ) : BaseViewModel(application) {

    val allFaqList: MutableLiveData<List<Faq>> by lazy {
        MutableLiveData<List<Faq>>()
    }

    fun getCourseFreeBookFromDB(bookId: Int): LiveData<ClassWiseBook> {
        val book = MutableLiveData<ClassWiseBook>()
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                book.postValue(bookChapterDao.getCourseFreeBook(bookId))
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return book
    }

    fun getAllFaqs() {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.allFaqRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        if (apiResponse.body.data?.coursefaqs.isNullOrEmpty()) {
                            toastError.postValue(apiResponse.body.msg ?: AppConstants.noCourseFoundMessage)
                            return@launch
                        }
                        allFaqList.postValue(apiResponse.body.data?.coursefaqs)
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
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
        }
    }
}
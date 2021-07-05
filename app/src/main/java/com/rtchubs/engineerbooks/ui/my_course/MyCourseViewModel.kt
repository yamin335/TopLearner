package com.rtchubs.engineerbooks.ui.my_course

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.local_db.dao.BookChapterDao
import com.rtchubs.engineerbooks.local_db.dao.CourseDao
import com.rtchubs.engineerbooks.local_db.dao.MyCourseDao
import com.rtchubs.engineerbooks.models.home.CourseCategory
import com.rtchubs.engineerbooks.models.my_course.MyCourse
import com.rtchubs.engineerbooks.models.my_course.MyCourseBook
import com.rtchubs.engineerbooks.models.my_course.MyCourseListRequest
import com.rtchubs.engineerbooks.repos.HomeRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyCourseViewModel @Inject constructor(
    private val application: Application,
    private val repository: HomeRepository,
    private val bookChapterDao: BookChapterDao,
    private val myCourseDao: MyCourseDao,
    private val courseDao: CourseDao
) : BaseViewModel(application) {

    val myCourses: MutableLiveData<List<MyCourse>> by lazy {
        MutableLiveData<List<MyCourse>>()
    }

    val allMyCourseBooksFromDB: LiveData<List<MyCourseBook>> = liveData {
        myCourseDao.getAllMyCourseBooks().collect { list ->
            emit(list)
        }
    }

    val allMyCoursesFromDB: LiveData<List<MyCourse>> = liveData {
        myCourseDao.getAllMyCourses().collect { list ->
            emit(list)
        }
    }

    val allCourseCategoriesFromDB: LiveData<List<CourseCategory>> = liveData {
        courseDao.getAllCourseCategories().collect { list ->
            emit(list)
        }
    }

    fun saveMyCoursesInDB(myCourses: List<MyCourse>) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                myCourseDao.updateAllMyCourses(myCourses)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun getMyCourseBookFromDB(bookId: Int): LiveData<MyCourseBook> {
        val book = MutableLiveData<MyCourseBook>()
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                book.postValue(myCourseDao.getMyCourseBook(bookId))
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return book
    }

    private fun saveMyPaidBook(myCourseBook: MyCourseBook) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                myCourseDao.addItemToMyCourseBooks(myCourseBook)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }


    fun getMyCourses(mobile: String?) {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.myCoursesRepo(
                    MyCourseListRequest(mobile)
                ))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        myCourses.postValue(apiResponse.body.data?.courses)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
        }
    }

    fun getMyCourseBook(bookId: Int?) {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.singleBookRepo(bookId))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        apiResponse.body.data?.book?.let { book ->
                            saveMyPaidBook(book)
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
        }
    }
}
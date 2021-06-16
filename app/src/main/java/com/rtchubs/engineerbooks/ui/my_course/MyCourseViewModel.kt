package com.rtchubs.engineerbooks.ui.my_course

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.local_db.dao.BookChapterDao
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.my_course.MyCourse
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
    private val bookChapterDao: BookChapterDao
) : BaseViewModel(application) {

    val allBooks: MutableLiveData<List<ClassWiseBook>> by lazy {
        MutableLiveData<List<ClassWiseBook>>()
    }

    val myCourses: MutableLiveData<List<MyCourse>> by lazy {
        MutableLiveData<List<MyCourse>>()
    }

    val allBooksFromDB: LiveData<List<ClassWiseBook>> = liveData {
        bookChapterDao.getAllBooks().collect { list ->
            emit(list)
        }
    }

    fun getMyCourses(mobile: String) {
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
        }
    }

    fun saveBooksInDB(books: List<ClassWiseBook>) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                bookChapterDao.updateBooks(books)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun getAcademicBooks(mobile: String, class_id: Int) {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.allBookRepo(mobile, class_id))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allBooks.postValue(apiResponse.body.data?.books)
                        getMyCourses(mobile)
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

    fun getAdminPanelBooks() {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.adminPanelBookRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val totalBooks = ArrayList<ClassWiseBook>()
                        val books = apiResponse.body.data?.books
                        books?.let {
                            it.forEach { book ->
                                totalBooks.add(ClassWiseBook(book.id ?: 0, book.uuid, book.name, book.title, book.authors,
                                    book.is_paid == 1, book.book_type_id, book.price, book.status,book.logo))
                            }
                            allBooks.postValue(totalBooks)
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
        }
    }
}
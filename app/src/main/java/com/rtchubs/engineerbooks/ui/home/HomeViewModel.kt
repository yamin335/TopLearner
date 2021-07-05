package com.rtchubs.engineerbooks.ui.home

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.api.ResponseCodes.CODE_SUCCESS
import com.rtchubs.engineerbooks.local_db.dao.BookChapterDao
import com.rtchubs.engineerbooks.local_db.dao.CourseDao
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import com.rtchubs.engineerbooks.models.home.CourseCategory
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import com.rtchubs.engineerbooks.repos.HomeRepository
import com.rtchubs.engineerbooks.repos.MediaRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper,
    private val application: Application,
    private val repository: HomeRepository,
    private val mediaRepository: MediaRepository,
    private val courseDao: CourseDao,
    private val bookChapterDao: BookChapterDao
) : BaseViewModel(application) {
    val defaultResponse: MutableLiveData<DefaultResponse> = MutableLiveData()

    val allCourseCategoriesFromDB: LiveData<List<CourseCategory>> = liveData {
        courseDao.getAllCourseCategories().collect { list ->
            emit(list)
        }
    }

    val allCourseCategoryList: MutableLiveData<List<CourseCategory>> by lazy {
        MutableLiveData<List<CourseCategory>>()
    }

    fun saveCourseCategoriesInDB(courseCategories: List<CourseCategory>) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                courseDao.updateAllCourseCategories(courseCategories)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun saveBooksInDB(books: List<ClassWiseBook>) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                bookChapterDao.addBooks(books)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

//    val slideDataList: MutableLiveData<List<AdSlider>> by lazy {
//        MutableLiveData<List<AdSlider>>()
//    }

//    fun getAds() {
//        if (checkNetworkStatus(true)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(mediaRepository.getAdsRepo())) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        slideDataList.postValue(apiResponse.body.data?.ads)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        }
//    }

//    fun getAcademicBooks(mobile: String, class_id: Int) {
//        if (checkNetworkStatus(false)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(repository.allBookRepo(mobile, class_id))) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        allBooks.postValue(apiResponse.body.data?.books)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        }
//    }

    fun getAllCourses() {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.allCourseRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        if (apiResponse.body.code != CODE_SUCCESS) {
                            toastError.postValue(apiResponse.body.message ?: AppConstants.serverConnectionErrorMessage)
                            return@launch
                        }
                        if (apiResponse.body.data?.CourseCatagorys.isNullOrEmpty()) {
                            toastError.postValue(apiResponse.body.message ?: AppConstants.noCourseFoundMessage)
                            return@launch
                        }
                        allCourseCategoryList.postValue(apiResponse.body.data?.CourseCatagorys)
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

    fun getAllFreeBooks() {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.allFreeBookRepo())) {
                    is ApiSuccessResponse -> {
                        saveBooksInDB(apiResponse.body.data?.books ?: ArrayList())
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
    }

//    fun getAdminPanelBooks() {
//        if (checkNetworkStatus(false)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(repository.adminPanelBookRepo())) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        val totalBooks = ArrayList<ClassWiseBook>()
//                        val books = apiResponse.body.data?.books
//                        books?.let {
//                            it.forEach { book ->
//                                totalBooks.add(ClassWiseBook(book.id ?: 0, book.uuid, book.name, book.title, book.authors,
//                                    book.is_paid == 1, book.book_type_id, book.price, book.status,book.logo))
//                            }
//                            allBooks.postValue(totalBooks)
//                        }
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        }
//    }

//    fun requestBankList(type:String) {
//        if (checkNetworkStatus(true)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            Log.e("token", preferencesHelper.getAccessTokenHeader())
//            viewModelScope.launch(handler) {
//                when (val apiResponse =
//                    ApiResponse.create(repository.requestBankListRepo(type,preferencesHelper.getAccessTokenHeader()))) {
//                    is ApiSuccessResponse -> {
//                        var d=DefaultResponse(apiResponse.body.toString(), "", "", true)
//                        defaultResponse.postValue(d)
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        Log.e("error", apiResponse.errorMessage)
//                        defaultResponse.postValue(
//                            Gson().fromJson(
//                                apiResponse.errorMessage,
//                                DefaultResponse::class.java
//                            )
//                        )
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        }
//    }
}
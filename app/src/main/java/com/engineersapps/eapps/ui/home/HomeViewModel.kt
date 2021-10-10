package com.engineersapps.eapps.ui.home

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.AcademicClassDao
import com.engineersapps.eapps.local_db.dao.BookChapterDao
import com.engineersapps.eapps.local_db.dao.CourseDao
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.models.home.CourseCategory
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.models.registration.DefaultResponse
import com.engineersapps.eapps.prefs.PreferencesHelper
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.repos.MediaRepository
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper,
    private val application: Application,
    private val repository: HomeRepository,
    private val mediaRepository: MediaRepository,
    private val registrationRepository: RegistrationRepository,
    private val courseDao: CourseDao,
    private val bookChapterDao: BookChapterDao,
    private val academicClassDao: AcademicClassDao
) : BaseViewModel(application) {
    val defaultResponse: MutableLiveData<DefaultResponse> = MutableLiveData()

    val allCourseCategoriesFromDB: LiveData<List<CourseCategory>> = liveData {
        courseDao.getAllCourseCategories().collect { list ->
            emit(list)
        }
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

    private fun saveBooksInDB(books: List<ClassWiseBook>) {
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
//                        if (apiResponse.body.code != CODE_SUCCESS) {
//                            toastError.postValue(apiResponse.body.msg ?: AppConstants.serverConnectionErrorMessage)
//                            return@launch
//                        }

                        val courses = apiResponse.body.data?.courses
                        if (courses.isNullOrEmpty()) {
                            toastError.postValue(apiResponse.body.msg ?: AppConstants.noCourseFoundMessage)
                            return@launch
                        }

                        val courseMap = courses.groupBy { it.catagory_id }
                        val courseCategories: ArrayList<CourseCategory> = ArrayList()
                        for (key in courseMap.keys) {
                            val courseList = courseMap[key]
                            val categoryName = if (!courseList.isNullOrEmpty()) courseList[0].catagory_name else "Unknown Courses"
                            courseCategories.add(CourseCategory(key, categoryName, courseList))
                        }

                        saveCourseCategoriesInDB(courseCategories)
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
                        checkForValidSession(apiResponse.errorMessage)
                    }
                }
            }
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
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
package com.engineersapps.eapps.ui.free_book

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.AcademicClassDao
import com.engineersapps.eapps.local_db.dao.BookChapterDao
import com.engineersapps.eapps.models.AdSlider
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.prefs.PreferencesHelper
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.repos.MediaRepository
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class FreeBooksViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper,
    private val application: Application,
    private val repository: HomeRepository,
    private val mediaRepository: MediaRepository,
    private val registrationRepository: RegistrationRepository,
    private val bookChapterDao: BookChapterDao,
    private val academicClassDao: AcademicClassDao
) : BaseViewModel(application) {

    //val defaultResponse: MutableLiveData<DefaultResponse> = MutableLiveData()

    val allBooks: MutableLiveData<List<ClassWiseBook>> by lazy {
        MutableLiveData<List<ClassWiseBook>>()
    }

    val allFreeBooksFromDB = MutableLiveData<List<ClassWiseBook>>()

    private fun getFreeBookFromDB() {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                allFreeBooksFromDB.postValue(bookChapterDao.getClassWiseBooks(selectedClassId))
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

//    val allBooksFromDB: LiveData<List<ClassWiseBook>> = liveData {
//        bookChapterDao.getAllBooks().collect { list ->
//            emit(list)
//        }
//    }

//    val allAcademicClass: LiveData<List<AcademicClass>> = liveData {
//        academicClassDao.getAllClasses().collect { list ->
//            emit(list)
//        }
//    }

    var selectedClassId: Int = 0
        set(value) {
            field = value
            getFreeBookFromDB()
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

//    private fun updateClassesInDB(classes: List<AcademicClass>) {
//        try {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//            }
//
//            viewModelScope.launch(handler) {
//                academicClassDao.updateAllAcademicClasses(classes)
//            }
//        } catch (e: SQLiteException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun updateBooksInDB(books: List<ClassWiseBook>) {
//        try {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//            }
//
//            viewModelScope.launch(handler) {
//                bookChapterDao.updateBooks(books)
//            }
//        } catch (e: SQLiteException) {
//            e.printStackTrace()
//        }
//    }

    fun saveBooksInDB(books: List<ClassWiseBook>) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                val numbers = bookChapterDao.addBooks(books)
                if (numbers.isNotEmpty()) {
                    getFreeBookFromDB()
                }
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    val slideDataList: MutableLiveData<List<AdSlider>> by lazy {
        MutableLiveData<List<AdSlider>>()
    }

    fun getAds() {
        if (checkNetworkStatus(true)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(mediaRepository.getAdsRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        slideDataList.postValue(apiResponse.body.data?.ads)
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

    fun getAllFreeBooks() {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.allFreeBookRepo())) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        allBooks.postValue(apiResponse.body.data?.books)
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
//                        allBooks.postValue(Pair(class_id, apiResponse.body.data?.books))
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

//    fun  getAdminPanelBooks() {
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

//    fun getAcademicClass() {
//        if (checkNetworkStatus(true)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//            }
//
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(registrationRepository.getAcademicClassRepo())) {
//                    is ApiSuccessResponse -> {
//                        updateClassesInDB(apiResponse.body.data?.classes ?: ArrayList())
//                    }
//                    is ApiEmptyResponse -> {
//                    }
//                    is ApiErrorResponse -> {
//                        checkForValidSession(apiResponse.errorMessage)
//                    }
//                }
//            }
//        }
//    }
}
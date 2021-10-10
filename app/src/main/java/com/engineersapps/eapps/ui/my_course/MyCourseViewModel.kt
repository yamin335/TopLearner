package com.engineersapps.eapps.ui.my_course

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.BookChapterDao
import com.engineersapps.eapps.local_db.dao.CourseDao
import com.engineersapps.eapps.local_db.dao.MyCourseDao
import com.engineersapps.eapps.models.home.CourseCategory
import com.engineersapps.eapps.models.my_course.MyCourse
import com.engineersapps.eapps.models.my_course.MyCourseBook
import com.engineersapps.eapps.models.my_course.MyCourseListRequest
import com.engineersapps.eapps.models.payment.CoursePaymentRequest
import com.engineersapps.eapps.models.transactions.CreateOrderBody
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.repos.TransactionRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MyCourseViewModel @Inject constructor(
    private val application: Application,
    private val repository: HomeRepository,
    private val transactionRepository: TransactionRepository,
    private val bookChapterDao: BookChapterDao,
    private val myCourseDao: MyCourseDao,
    private val courseDao: CourseDao
) : BaseViewModel(application) {

    val isPendingCoursePurchaseSuccess: MutableLiveData<Boolean?> by lazy {
        MutableLiveData<Boolean?>()
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
                        val myCourses = apiResponse.body.data?.courses ?: ArrayList()
                        val myCourseList = ArrayList<MyCourse>()
                        for (course in myCourses) {
                            var purchaseDate = Date()
                            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                            try {
                                purchaseDate = format.parse(course.date ?: "") ?: continue
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            val duration = course.duration ?: 0
                            val date = Calendar.getInstance()
                            date.time = purchaseDate
                            date[Calendar.DATE] = date[Calendar.DATE] + duration

                            var expireDate = ""
                            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                            try {
                                expireDate = dateFormat.format(date.time) ?: continue
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            course.expiredate = expireDate
                            myCourseList.add(course)
                        }
                        saveMyCoursesInDB(myCourseList)
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
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
        }
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

    fun purchaseCourse(createOrderBody: CreateOrderBody?, coursePaymentRequest: CoursePaymentRequest) {
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
                        if (createOrderBody == null) {
                            apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        }
                        createOrderBody?.let {
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
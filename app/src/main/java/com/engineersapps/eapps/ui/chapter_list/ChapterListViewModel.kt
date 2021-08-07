package com.engineersapps.eapps.ui.chapter_list

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.*
import com.engineersapps.eapps.local_db.dao.BookChapterDao
import com.engineersapps.eapps.local_db.dbo.ChapterItem
import com.engineersapps.eapps.models.chapter.BookChapter
import com.engineersapps.eapps.repos.HomeRepository
import com.engineersapps.eapps.repos.MediaRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChapterListViewModel @Inject constructor(
    private val application: Application,
    private val mediaRepository: MediaRepository,
    private val bookChapterDao: BookChapterDao,
    private val homeRepository: HomeRepository
    ) : BaseViewModel(application) {

    val chapterListFromDB: LiveData<ChapterItem> = liveData {
        bookChapterDao.getBookChapters(ChapterListFragment.bookID).collect { chapters ->
            emit(chapters)
        }
    }

    fun saveBookChaptersInDB(bookId: String, chapters: List<BookChapter>) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                bookChapterDao.updateChapters(ChapterItem(bookId, chapters))
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun deleteAChapterFromDB(bookId: String) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                bookChapterDao.deleteAChapter(bookId)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    val chapterList: MutableLiveData<List<BookChapter>> by lazy {
        MutableLiveData<List<BookChapter>>()
    }

    fun getChapterList(bookID: Int?) {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
                chapterList.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(homeRepository.getChaptersRepo(bookID))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        chapterList.postValue(apiResponse.body.data?.chapters)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        chapterList.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        checkForValidSession(apiResponse.errorMessage)
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        chapterList.postValue(null)
                    }
                }
            }
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
        }
    }
}
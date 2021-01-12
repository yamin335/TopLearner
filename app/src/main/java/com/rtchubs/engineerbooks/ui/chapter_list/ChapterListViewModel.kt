package com.rtchubs.engineerbooks.ui.chapter_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.Chapter
import com.rtchubs.engineerbooks.models.chapter.BookChapter
import com.rtchubs.engineerbooks.models.registration.Upazilla
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import com.rtchubs.engineerbooks.repos.MediaRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChapterListViewModel @Inject constructor(
    private val application: Application,
    private val mediaRepository: MediaRepository
    ) : BaseViewModel(application) {
    val chapterList: MutableLiveData<List<BookChapter>> by lazy {
        MutableLiveData<List<BookChapter>>()
    }

    fun getChapterList(bookID: String?) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(mediaRepository.getChaptersRepo(bookID))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        chapterList.postValue(apiResponse.body.data?.chapters)
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
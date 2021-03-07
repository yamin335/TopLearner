package com.rtchubs.engineerbooks.ui.notice_board

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.rtchubs.engineerbooks.models.notice_board.Notice
import com.rtchubs.engineerbooks.repos.AdminRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import javax.inject.Inject

class NoticeBoardViewModel @Inject constructor(
    private val application: Application,
    private val adminRepository: AdminRepository
    ) : BaseViewModel(application) {

    val allNotices: MutableLiveData<List<Notice>> by lazy {
        MutableLiveData<List<Notice>>()
    }

    fun getAllNotices() {
//        if (checkNetworkStatus(true)) {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//                toastError.postValue(AppConstants.serverConnectionErrorMessage)
//            }
//
//            apiCallStatus.postValue(ApiCallStatus.LOADING)
//            viewModelScope.launch(handler) {
//                when (val apiResponse = ApiResponse.create(adminRepository.noticeRepo())) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        allNotices.postValue(apiResponse.body.data?.noticeboard)
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
    }
}
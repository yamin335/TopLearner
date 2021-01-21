package com.rtchubs.engineerbooks.ui.live_video

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.models.registration.DefaultResponse
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.models.registration.UserRegistrationData
import com.rtchubs.engineerbooks.repos.LoginRepository
import com.rtchubs.engineerbooks.repos.RegistrationRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class LiveVideoViewModel @Inject constructor(
    private val application: Application,
    private val repository: RegistrationRepository
) : BaseViewModel(application) {

}
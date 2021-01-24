package com.rtchubs.engineerbooks.ui.e_code

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

class ECodeViewModel @Inject constructor(
    private val application: Application,
    private val mediaRepository: MediaRepository
    ) : BaseViewModel(application) {
}
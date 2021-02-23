package com.rtchubs.engineerbooks.ui.offer

import android.app.Application
import com.rtchubs.engineerbooks.repos.MediaRepository
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import javax.inject.Inject

class OfferViewModel @Inject constructor(
    private val application: Application,
    private val mediaRepository: MediaRepository
    ) : BaseViewModel(application) {
}
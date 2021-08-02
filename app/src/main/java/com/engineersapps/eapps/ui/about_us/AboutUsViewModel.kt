package com.engineersapps.eapps.ui.about_us

import android.app.Application
import com.engineersapps.eapps.repos.MediaRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import javax.inject.Inject

class AboutUsViewModel @Inject constructor(
    private val application: Application,
    private val mediaRepository: MediaRepository
    ) : BaseViewModel(application) {
}
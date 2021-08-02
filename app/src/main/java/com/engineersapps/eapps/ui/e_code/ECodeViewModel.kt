package com.engineersapps.eapps.ui.e_code

import android.app.Application
import com.engineersapps.eapps.repos.MediaRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import javax.inject.Inject

class ECodeViewModel @Inject constructor(
    private val application: Application,
    private val mediaRepository: MediaRepository
    ) : BaseViewModel(application) {
}
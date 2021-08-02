package com.engineersapps.eapps.ui

import android.app.Application
import com.engineersapps.eapps.ui.common.BaseViewModel
import javax.inject.Inject

class LoginActivityViewModel @Inject constructor(private val application: Application) : BaseViewModel(application) {

}
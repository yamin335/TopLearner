package com.engineersapps.eapps.ui.terms_and_conditions

import android.app.Application
import com.engineersapps.eapps.repos.RegistrationRepository
import com.engineersapps.eapps.ui.common.BaseViewModel
import javax.inject.Inject

class TermsViewModel @Inject constructor(private val application: Application, private val repository: RegistrationRepository) : BaseViewModel(application) {

}
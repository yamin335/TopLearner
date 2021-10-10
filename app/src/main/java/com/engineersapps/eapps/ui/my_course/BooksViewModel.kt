package com.engineersapps.eapps.ui.my_course

import android.app.Application
import com.engineersapps.eapps.ui.common.BaseViewModel
import javax.inject.Inject

class BooksViewModel @Inject constructor(
    private val application: Application
    ) : BaseViewModel(application) {

}
package com.engineersapps.eapps.ui.common

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ResponseCodes.CODE_EXPIRED_TOKEN
import com.engineersapps.eapps.api.ResponseCodes.CODE_INVALID_TOKEN
import com.engineersapps.eapps.models.home.SessionError
import com.engineersapps.eapps.prefs.PreferencesHelper
import com.engineersapps.eapps.ui.splash.SplashFragment
import com.engineersapps.eapps.util.AppConstants.INTENT_SESSION_EXPIRED
import com.engineersapps.eapps.util.AppConstants.INTENT_TOKEN_EXPIRED
import com.engineersapps.eapps.util.NetworkUtils
import com.engineersapps.eapps.util.showErrorToast
import com.google.gson.Gson

abstract class BaseViewModel constructor(val context: Application) : ViewModel() {

    val apiCallStatus: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val toastError = MutableLiveData<String>()
    val toastWarning = MutableLiveData<String>()
    val toastSuccess = MutableLiveData<String>()
    val popBackStack = MutableLiveData<Boolean>()

    fun checkNetworkStatus(shouldShowMessage: Boolean) = when {
        NetworkUtils.isNetworkConnected(context) -> {
            true
        }
        shouldShowMessage -> {
            showErrorToast(context, context.getString(R.string.internet_error_msg))
            false
        }
        else -> {
            false
        }
    }

    fun checkForValidSession(error: String) {
        try {
            val sessionError = Gson().fromJson(error, SessionError::class.java)
            sessionError?.let {
                val code = it.code ?: return@let
                if (code == CODE_INVALID_TOKEN) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(INTENT_SESSION_EXPIRED))
                } else if (code == CODE_EXPIRED_TOKEN) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(INTENT_TOKEN_EXPIRED))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onAppExit(preferences: SharedPreferences) {
        preferences.edit().apply {
            putString("LoggedUserPassword",null)
            putString("LoggedUserID", null)
            putBoolean("goToLogin", false)
            apply()
        }
    }

    fun onLogOut(preferencesHelper: PreferencesHelper) {
        SplashFragment.fromLogout = true
        preferencesHelper.isLoggedIn = false
    }
}
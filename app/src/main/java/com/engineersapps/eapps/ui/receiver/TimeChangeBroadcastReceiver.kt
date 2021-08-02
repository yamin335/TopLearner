package com.engineersapps.eapps.ui.receiver

import android.content.Context
import android.content.Intent
import com.engineersapps.eapps.prefs.PreferencesHelper
import dagger.android.DaggerBroadcastReceiver
import javax.inject.Inject

class TimeChangeBroadcastReceiver: DaggerBroadcastReceiver() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val action = intent?.action
        if (action == Intent.ACTION_TIME_CHANGED || action == Intent.ACTION_TIMEZONE_CHANGED) {
            preferencesHelper.isDeviceTimeChanged = true
        }
    }
}
package com.engineersapps.eapps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.Nullable
import com.engineersapps.eapps.util.CommonUtils
import com.engineersapps.eapps.util.NetworkUtils


class SyncOnConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(@Nullable context: Context?, intent: Intent) {
        //Timber.d("triggering on connectivity change")
        if (context != null) {
            if (!NetworkUtils.isNetworkConnected(context)) {
                CommonUtils.fireErrorMessageEvent(
                        error = context.getString(R.string.no_internet_error),
                        showInAlert = false
                )
            }
        }
    }
}

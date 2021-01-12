package com.rtchubs.engineerbooks.util

import android.annotation.TargetApi
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat

object NotificationUtils {
    fun getNotificationBuilder(context: Context, mChannelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, mChannelId)
    }

    @TargetApi(VERSION_CODES.O)
    fun prepareChannel(context: Context, mChannelId: String, mChannelName: String, mChannelDescription: String) {
        // Notification channel settings
        // Default Notification Priority
        val mChannelImportance = NotificationManager.IMPORTANCE_HIGH
        val mChannelLockScreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        val nm = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        var mChannel = nm.getNotificationChannel(mChannelId)
        if (mChannel == null) {
            mChannel = NotificationChannel(mChannelId, mChannelName, mChannelImportance) // User visible channel name
            mChannel.description = mChannelDescription // User visible channel description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.GREEN
            mChannel.enableVibration(false)
            mChannel.setShowBadge(true)
            mChannel.lockscreenVisibility = mChannelLockScreenVisibility
            nm.createNotificationChannel(mChannel)
        }
    }
}
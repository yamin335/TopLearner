package com.rtchubs.engineerbooks.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import com.rtchubs.engineerbooks.R
import kotlinx.android.synthetic.main.toast_custom_error.view.*
import kotlinx.android.synthetic.main.toast_custom_success.view.*
import kotlinx.android.synthetic.main.toast_custom_warning.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun isTimeAndZoneAutomatic(context: Context?): Boolean {
    return Settings.Global.getInt(context?.contentResolver, Settings.Global.AUTO_TIME, 0) == 1 && Settings.Global.getInt(context?.contentResolver, Settings.Global.AUTO_TIME_ZONE, 0) == 1
}

fun showErrorToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.toast_custom_error, null)
        toastView.errorMessage.text = message
        toast.view = toastView
        toast.show()
    }
}

fun showWarningToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.toast_custom_warning, null)
        toastView.warningMessage.text = message
        toast.view = toastView
        toast.show()
    }
}



fun showSuccessToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
        val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.toast_custom_success, null)
        toastView.successMessage.text = message
        toast.view = toastView
        toast.show()
    }
}

fun goToFacebook(context: Context, pageName: String) {
    try {
        val applicationInfo = context.packageManager.getApplicationInfo("com.facebook.katana", 0)
        if (applicationInfo.enabled) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/n/?$pageName"))
            // Verify the intent will resolve to at least one activity
            if (webIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(webIntent)
            }
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/n/?$pageName"))
            val chooser = Intent.createChooser(webIntent, "View Facebook Page Using")
            // Verify the intent will resolve to at least one activity
            if (webIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
            }
        }
    } catch (exception: PackageManager.NameNotFoundException) {
        exception.printStackTrace()
    }
}

fun goToYoutube(context: Context, youtubeID: String) {
    val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$youtubeID"))
    val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/$youtubeID"))
    //val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$youtubeID"))
    try {
        context.startActivity(intentApp)
    } catch (ex: ActivityNotFoundException) {
        context.startActivity(intentBrowser)
    }
}
package com.engineersapps.eapps.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.engineersapps.eapps.ui.profiles.PERMISSION_REQUEST_CODE

object PermissionUtils {
    fun isCameraAndGalleryPermissionGranted(fragmentActivity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            when (PackageManager.PERMISSION_GRANTED) {
                fragmentActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    true
                }
                fragmentActivity.checkSelfPermission(Manifest.permission.CAMERA)
                -> {
                    true
                }
                else -> {
                    ActivityCompat.requestPermissions(
                        fragmentActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                        PERMISSION_REQUEST_CODE
                    )
                    false
                }
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    fun isGalleryPermission(fragmentActivity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (fragmentActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    fragmentActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    fun isCameraPermission(fragmentActivity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (fragmentActivity.checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    fragmentActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CODE
                )
                false
            }
        } else {
            true
        }
    }
}
package com.engineersapps.eapps.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.engineersapps.eapps.BuildConfig
import com.engineersapps.eapps.R
import com.engineersapps.eapps.util.showErrorToast
import com.engineersapps.eapps.util.showSuccessToast
import com.engineersapps.eapps.util.showWarningToast
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import dagger.android.support.DaggerAppCompatActivity

private const val APP_UPDATE_REQUEST_CODE = 1669
private const val APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.IMMEDIATE
class AppUpdateCheckingActivity : DaggerAppCompatActivity() {

    private lateinit var updateListener: InstallStateUpdatedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForAppUpdate()
    }

    override fun onResume() {
        super.onResume()
        checkForAppUpdate()
    }

    private fun checkForAppUpdate() {
        val appUpdateManager : AppUpdateManager
        if (BuildConfig.DEBUG) {
            appUpdateManager = FakeAppUpdateManager(baseContext)
            appUpdateManager.setUpdateAvailable(1)
        } else {
            appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        }

        if (BuildConfig.DEBUG) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val appUpdateInfo = appUpdateManager.appUpdateInfo

            appUpdateInfo.addOnSuccessListener {
                handleUpdate(appUpdateManager, appUpdateInfo)
            }
        }
    }

    private fun handleUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
            handleImmediateUpdate(manager, info)
        } else if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.FLEXIBLE) {
            handleFlexibleUpdate(manager, info)
        }
    }

    private fun handleImmediateUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
            && info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

            manager.startUpdateFlowForResult(info.result,
                AppUpdateType.IMMEDIATE, this, APP_UPDATE_REQUEST_CODE)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Simulates an immediate update
        if (BuildConfig.DEBUG) {
            val fakeAppUpdate = manager as FakeAppUpdateManager
            if (fakeAppUpdate.isImmediateFlowVisible) {
                fakeAppUpdate.userAcceptsUpdate()
                fakeAppUpdate.downloadStarts()
                fakeAppUpdate.downloadCompletes()
                //launchRestartDialog(manager)
            }
        }
    }

    private fun handleFlexibleUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if (info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            && info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            handleImmediateUpdate(manager, info)
            return
        } else if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
            && info.result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

            //btn_update.visibility = View.VISIBLE
            setUpdateAction(manager, info)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setUpdateAction(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        updateListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.FAILED, InstallStatus.UNKNOWN -> {
                    checkForAppUpdate()
                }
                InstallStatus.PENDING -> {
                    //tv_status.text = getString(R.string.info_pending)
                }
                InstallStatus.CANCELED -> {
                    //tv_status.text = getString(R.string.info_canceled)
                }
                InstallStatus.DOWNLOADING -> {
                    //tv_status.text = getString(R.string.info_downloading)
                }
                InstallStatus.DOWNLOADED -> {
                    //tv_status.text = getString(R.string.info_installing)
                    launchRestartDialog(manager)
                }
                InstallStatus.INSTALLING -> {
                    //tv_status.text = getString(R.string.info_installing)
                }
                InstallStatus.INSTALLED -> {
                    //tv_status.text = getString(R.string.info_installed)
                    manager.unregisterListener(updateListener)
                }
                else -> {
                    //tv_status.text = getString(R.string.info_restart)
                }
            }
        }

        manager.registerListener(updateListener)
        manager.startUpdateFlowForResult(info.result, AppUpdateType.FLEXIBLE, this,
            APP_UPDATE_REQUEST_CODE)

        // Simulates a flexible update
        if (BuildConfig.DEBUG) {
            val fakeAppUpdate = manager as FakeAppUpdateManager
            if (fakeAppUpdate.isConfirmationDialogVisible) {
                fakeAppUpdate.userAcceptsUpdate()
                fakeAppUpdate.downloadStarts()
                fakeAppUpdate.downloadCompletes()
                fakeAppUpdate.completeUpdate()
                fakeAppUpdate.installCompletes()
            }
        }
    }

    private fun launchRestartDialog(manager: AppUpdateManager) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.update_title))
            .setMessage(getString(R.string.update_message))
            .setPositiveButton(getString(R.string.action_restart)) { _, _ ->
                manager.completeUpdate()
            }
            .create().show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            APP_UPDATE_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
                            showSuccessToast(baseContext, getString(R.string.toast_updated))
                        } else {
                            showSuccessToast(baseContext, getString(R.string.toast_started))
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        showWarningToast(baseContext, getString(R.string.toast_cancelled))
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        showErrorToast(baseContext, getString(R.string.toast_failed))
                        // If the update is cancelled or fails,
                        // you can request to start the update again.
                        checkForAppUpdate()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
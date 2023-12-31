package com.engineersapps.eapps

import android.os.Build
import androidx.databinding.DataBindingUtil
import androidx.work.Configuration
import androidx.work.WorkManager
import com.engineersapps.eapps.binding.FragmentDataBindingComponent
import com.engineersapps.eapps.di.DaggerAppComponent
import com.engineersapps.eapps.util.AppConstants
import com.engineersapps.eapps.util.AppConstants.downloadFolder
import com.engineersapps.eapps.util.AppConstants.downloadedPdfFiles
import com.engineersapps.eapps.util.FileUtils
import com.engineersapps.eapps.util.NotificationUtils
import com.engineersapps.eapps.worker.DaggerWorkerFactory
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication() {

    private val applicationInjector = DaggerAppComponent.builder()
        .application(this)
        .build()

    @Inject
    lateinit var workerFactory: DaggerWorkerFactory
//    @Inject
//    lateinit var picasso: Picasso
    override fun applicationInjector() = applicationInjector

    override fun onCreate() {
        super.onCreate()

        // Inject this class's @Inject-annotated members.
        applicationInjector.inject(this)
        /*if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }*/

        // Fabric.with(this, Crashlytics())


        //set picasso to support http protocol
//        Picasso.setSingletonInstance(picasso)

        DataBindingUtil.setDefaultComponent(FragmentDataBindingComponent())

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )

        FileUtils.makeEmptyFolderIntoExternalStorageWithTitle(this, downloadFolder)
        FileUtils.makeEmptyFolderIntoExternalStorageWithTitle(this, downloadedPdfFiles)

        // Create notification channels
        val mChannelId = AppConstants.downloadServiceNotificationChannelID
        val mChannelName = AppConstants.downloadServiceNotificationChannelName
        val mChannelDescription = AppConstants.downloadServiceNotificationChannelDescription
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.prepareChannel(this, mChannelId, mChannelName, mChannelDescription)
        }
    }

    companion object {
        private const val TAG = "App"
    }
}
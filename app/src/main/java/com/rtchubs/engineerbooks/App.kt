package com.rtchubs.engineerbooks

import android.os.Build
import androidx.databinding.DataBindingUtil
import androidx.work.Configuration
import androidx.work.WorkManager
import com.rtchubs.engineerbooks.binding.FragmentDataBindingComponent
import com.rtchubs.engineerbooks.di.DaggerAppComponent
import com.rtchubs.engineerbooks.util.AppConstants
import com.rtchubs.engineerbooks.util.AppConstants.downloadFolder
import com.rtchubs.engineerbooks.util.FileUtils
import com.rtchubs.engineerbooks.util.NotificationUtils
import com.rtchubs.engineerbooks.worker.DaggerWorkerFactory
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication() {

    private val applicationInjector = DaggerAppComponent.builder()
        .application(this)
        .build()

    @Inject
    lateinit var workerFactory: DaggerWorkerFactory
    @Inject
    lateinit var picasso: Picasso
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
        Picasso.setSingletonInstance(picasso)

        DataBindingUtil.setDefaultComponent(FragmentDataBindingComponent())

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )

        FileUtils.makeEmptyFolderIntoExternalStorageWithTitle(this, downloadFolder)

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
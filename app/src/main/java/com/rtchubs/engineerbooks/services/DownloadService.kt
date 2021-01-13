package com.rtchubs.engineerbooks.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.util.AppConstants
import com.rtchubs.engineerbooks.util.AppConstants.DOWNLOAD_URL
import com.rtchubs.engineerbooks.util.AppConstants.FILE_NAME
import com.rtchubs.engineerbooks.util.AppConstants.FILE_PATH
import com.rtchubs.engineerbooks.util.AppConstants.FILE_TYPE
import com.rtchubs.engineerbooks.util.AtomicNumberGenerator
import com.rtchubs.engineerbooks.util.FileUtils
import com.rtchubs.engineerbooks.util.NotificationUtils
import timber.log.Timber
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.math.abs

class DownloadService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // User invisible channel ID
        val mChannelId = AppConstants.downloadServiceNotificationChannelID
        val notificationId = AtomicNumberGenerator.getUniqueNumber()
        val maxProgress = 100
        val currentProgress = 0
        val builder = NotificationUtils.getNotificationBuilder(this, mChannelId).apply {
            setContentTitle("Video Downloading...")
            setContentText("$currentProgress%")
            setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
            priority = NotificationCompat.PRIORITY_MAX
        }
        NotificationManagerCompat.from(this).apply {
            // Issue the initial notification with zero progress
            builder.setProgress(maxProgress, currentProgress, false)
            notify(notificationId, builder.build())

            // Do the job here that tracks the progress.
            // Usually, this should be in a
            // worker thread
            // To show progress, update PROGRESS_CURRENT and update the notification with:
            // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            // notificationManager.notify(notificationId, builder.build());



            // When done, update the notification one more time to remove the progress bar
//            builder.setContentText("Download complete")
//                .setProgress(0, 0, false)
//            notify(notificationId, builder.build())
        }

        startForeground(notificationId, builder.build())

        if (intent == null) stopSelf()

        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        val thread = HandlerThread(
            "ServiceStartArguments",
            Process.THREAD_PRIORITY_BACKGROUND
        )
        thread.start()

        val downloadUrl = intent?.getStringExtra(DOWNLOAD_URL) ?: ""
        val filePath = intent?.getStringExtra(FILE_PATH) ?: ""
        val fileName = intent?.getStringExtra(FILE_NAME) ?: ""
        val fileType = intent?.getStringExtra(FILE_TYPE) ?: ""

        // Get the HandlerThread's Looper and use it for our Handler
        val serviceLooper = thread.looper
        val serviceHandler =
            ServiceHandler(
                serviceLooper,
                this,
                downloadUrl,
                filePath,
                fileName,
                fileType,
                notificationId,
                builder
            )
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        val msg: Message = serviceHandler.obtainMessage()
        msg.arg1 = startId
        serviceHandler.sendMessage(msg)

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    // Handler that receives messages from the thread
    private inner class ServiceHandler(
        looper: Looper,
        private val context: Context,
        private val downloadUrl: String,
        private val storagePath: String,
        private val fileName: String,
        private val fileType: String,
        private val notificationId: Int,
        private val notificationBuilder: NotificationCompat.Builder
    ) : Handler(looper) {
        val maxProgress = 100
        var currentProgress = 0
        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            try {
                val urlConnection: HttpURLConnection
                val url = URL(downloadUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                //urlConnection.doOutput = true
                urlConnection.connect()

                val downloadedFile = File(
                    storagePath,
                    fileName
                )
                if (!downloadedFile.exists()) {
                    downloadedFile.createNewFile()
                }
                val inputStream: InputStream = urlConnection.inputStream
                val totalSize = urlConnection.contentLength
                val fileOutputStream = FileOutputStream(downloadedFile)
                var downloadedSize = 0
                val buffer = ByteArray(2024)
                var bufferLength: Int
                fileOutputStream.use { outputStream ->
                    inputStream.use { inStream ->
                        while (inStream.read(buffer).also { bufferLength = it } > 0) {
                            outputStream.write(buffer, 0, bufferLength)
                            downloadedSize += bufferLength
                            // To show progress, update PROGRESS_CURRENT and update the notification with:
                            currentProgress = abs(downloadedSize * 100 / totalSize)
                            notificationBuilder.setProgress(maxProgress, currentProgress, false)
                            notificationBuilder.apply {
                                setContentText("$currentProgress%")
                            }
                            NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
                            Timber.e("downloadedSize:%s", currentProgress)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = Intent(AppConstants.DOWNLOAD_COMPLETE)
            intent.putExtra(FILE_PATH, storagePath)
            intent.putExtra(FILE_NAME, fileName)
            intent.putExtra(FILE_TYPE, fileType)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }
}
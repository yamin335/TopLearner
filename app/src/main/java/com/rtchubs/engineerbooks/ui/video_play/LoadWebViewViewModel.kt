package com.rtchubs.engineerbooks.ui.video_play

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.api.*
import com.rtchubs.engineerbooks.local_db.dao.HistoryDao
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import com.rtchubs.engineerbooks.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.math.abs

class LoadWebViewViewModel @Inject constructor(private val application: Application, private val historyDao: HistoryDao) : BaseViewModel(application) {

    val historyItems: LiveData<List<HistoryItem>> = liveData {
        historyDao.getHistoryItems().collect { list ->
            emit(list)
        }
    }

    val filesInDownloadPool = ArrayList<String>()

    val videoFileDownloadResponse: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    val pdfFileDownloadResponse: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    fun doesItemExists(bookId: Int, chapterId: Int): LiveData<List<HistoryItem>> {
        val doesExists: MutableLiveData<List<HistoryItem>> = MutableLiveData()
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                doesExists.postValue(historyDao.doesItemExistsInHistory(bookId, chapterId))
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        return doesExists
    }

    fun updateToHistory(id: Int) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                historyDao.updateHistoryItemViewCount(id)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun addToHistory(historyItem: HistoryItem) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                historyDao.addItemToHistory(historyItem)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun downloadVideoFile(downloadUrl: String, filePath: String, fileName: String) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                videoFileDownloadResponse.postValue(downloadFile(downloadUrl, filePath, fileName))
            }
        }
    }

    fun downloadPdfFile(downloadUrl: String, filePath: String, fileName: String) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                pdfFileDownloadResponse.postValue(downloadFile(downloadUrl, filePath, fileName))
            }
        }
    }

    suspend fun downloadFile(downloadUrl: String, filePath: String, fileName: String): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            // Normally we would do some work here, like download a file.
            try {
                val urlConnection: HttpURLConnection
                val url = URL(downloadUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                //urlConnection.doOutput = true
                urlConnection.connect()

                val downloadedFile = File(
                    filePath,
                    fileName
                )
                if (!downloadedFile.exists()) {
                    downloadedFile.createNewFile()
                }
                val inputStream: InputStream = urlConnection.inputStream
                val fileOutputStream = FileOutputStream(downloadedFile)
                val buffer = ByteArray(2024)
                var bufferLength: Int
                fileOutputStream.use { outputStream ->
                    inputStream.use { inStream ->
                        while (inStream.read(buffer).also { bufferLength = it } > 0) {
                            outputStream.write(buffer, 0, bufferLength)
                        }
                    }
                }
                apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                Pair(filePath, fileName)
            } catch (e: Exception) {
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                e.printStackTrace()
                null
            }
        }
    }
}
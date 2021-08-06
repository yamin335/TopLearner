package com.engineersapps.eapps.ui.video_play

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.local_db.dao.HistoryDao
import com.engineersapps.eapps.local_db.dbo.HistoryItem
import com.engineersapps.eapps.models.chapter.PdfDownloadResponse
import com.engineersapps.eapps.ui.common.BaseViewModel
import com.engineersapps.eapps.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class LoadWebViewViewModel @Inject constructor(private val application: Application, private val historyDao: HistoryDao) : BaseViewModel(application) {

    val historyItems: LiveData<List<HistoryItem>> = liveData {
        historyDao.getHistoryItems().collect { list ->
            emit(list)
        }
    }

    val videoFileDownloadResponse: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    val pdfFileDownloadResponse: MutableLiveData<PdfDownloadResponse> by lazy {
        MutableLiveData<PdfDownloadResponse>()
    }

    val solutionPdfFileDownloadResponse: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    val showHideProgress: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
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
        if (checkNetworkStatus(true)) {
            showHideProgress.postValue(true)
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
                showHideProgress.postValue(false)
            }

            //apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                videoFileDownloadResponse.postValue(downloadFile(downloadUrl, filePath, fileName))
            }
        }
    }

    fun downloadPdfFile(downloadUrl: String, filePath: String, fileName: String, pdfForFragment: String) {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                val response = downloadFile(downloadUrl, filePath, fileName) ?: return@launch
                pdfFileDownloadResponse.postValue(PdfDownloadResponse(response.first, response.second, pdfForFragment))
            }
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
        }
    }

    fun downloadSolutionPdfFile(downloadUrl: String, filePath: String, fileName: String) {
        if (checkNetworkStatus(false)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                solutionPdfFileDownloadResponse.postValue(downloadFile(downloadUrl, filePath, fileName))
            }
        } else {
            apiCallStatus.postValue(ApiCallStatus.ERROR)
        }
    }

    private suspend fun downloadFile(downloadUrl: String, filePath: String, fileName: String): Pair<String, String>? {
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
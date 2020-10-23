package com.rtchubs.engineerbooks.ui.video_play

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.engineerbooks.local_db.dao.HistoryDao
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoadWebViewViewModel @Inject constructor(private val application: Application, private val historyDao: HistoryDao) : BaseViewModel(application) {

    val historyItems: LiveData<List<HistoryItem>> = liveData {
        historyDao.getHistoryItems().collect { list ->
            emit(list)
        }
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
}
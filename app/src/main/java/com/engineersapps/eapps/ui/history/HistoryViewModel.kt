package com.engineersapps.eapps.ui.history

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.engineersapps.eapps.local_db.dao.HistoryDao
import com.engineersapps.eapps.local_db.dbo.HistoryItem
import com.engineersapps.eapps.ui.common.BaseViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class HistoryViewModel @Inject constructor(private val application: Application, private val historyDao: HistoryDao) : BaseViewModel(application) {

    val historyItems: LiveData<List<HistoryItem>> = liveData {
        historyDao.getHistoryItems().collect { list ->
            emit(list)
        }
    }
}
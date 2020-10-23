package com.rtchubs.engineerbooks.ui.chapter_list

import androidx.lifecycle.ViewModel
import com.rtchubs.engineerbooks.models.Chapter
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import javax.inject.Inject

class ChapterListViewModel @Inject constructor(private val preferencesHelper: PreferencesHelper) : ViewModel() {
}
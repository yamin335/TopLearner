package com.rtchubs.engineerbooks.local_db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rtchubs.engineerbooks.models.chapter.BookChapter

@Entity(tableName = "chapters")
data class ChapterItem(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "bookId") val bookId: String,
    @ColumnInfo(name = "chapters") val chapters: List<BookChapter>
)
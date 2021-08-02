package com.engineersapps.eapps.local_db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.engineersapps.eapps.models.chapter.BookChapter

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "book_id") val bookID: Int,
    @ColumnInfo(name = "chapter_id") val chapterID: Int,
    @ColumnInfo(name = "chapter_name") val chapterName: String?,
    @ColumnInfo(name = "chapter") val chapter: BookChapter?,
    @ColumnInfo(name = "view_count") val viewCount: Int?
)
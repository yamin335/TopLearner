package com.rtchubs.engineerbooks.local_db.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "book_id") val bookID: Int,
    @ColumnInfo(name = "book_title") val bookTitle: String?,
    @ColumnInfo(name = "chapter_id") val chapterID: Int,
    @ColumnInfo(name = "chapter_title") val chapterTitle: String?,
    @ColumnInfo(name = "view_count") val viewCount: Int?
)
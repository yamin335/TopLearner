package com.rtchubs.engineerbooks.local_db.dao

import androidx.room.*
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItemToHistory(item: HistoryItem): Long

    @Query("UPDATE history SET view_count = view_count + 1 WHERE id = :id")
    suspend fun updateHistoryItemViewCount(id: Int)

    @Delete
    suspend fun deleteHistoryItem(cartItem: HistoryItem)

    @Query("SELECT * FROM history")
    fun getHistoryItems(): Flow<List<HistoryItem>>

    @Query("SELECT COUNT(id) FROM history")
    fun getHistoryItemsCount(): Flow<Int>

    @Query("SELECT * FROM history WHERE book_id = :bookId AND chapter_id=:chapterId")
    suspend fun doesItemExistsInHistory(bookId: Int, chapterId: Int): List<HistoryItem>
}
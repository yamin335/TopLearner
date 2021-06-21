package com.rtchubs.engineerbooks.local_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rtchubs.engineerbooks.models.my_course.MyCourseBook
import kotlinx.coroutines.flow.Flow

@Dao
interface MyCourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemToMyCourseBooks(myCourseBook: MyCourseBook): Long

//    @Query("UPDATE history SET view_count = view_count + 1 WHERE id = :id")
//    suspend fun updateHistoryItemViewCount(id: Int)

//    @Delete
//    suspend fun deleteHistoryItem(cartItem: HistoryItem)

    @Query("SELECT * FROM my_course_paid_books")
    fun getAllMyCourseBooks(): Flow<List<MyCourseBook>>

    @Query("SELECT * FROM my_course_paid_books WHERE id = :bookId")
    suspend fun getMyCourseBook(bookId: Int): MyCourseBook

//    @Query("SELECT COUNT(id) FROM history")
//    fun getHistoryItemsCount(): Flow<Int>

//    @Query("SELECT COUNT(id) FROM my_course_paid_books WHERE id = :bookId")
//    suspend fun doesItemExistsInMyCourseBooks(bookId: Int): Int

//    @Query("SELECT EXISTS (SELECT 1 FROM favorite WHERE id = :id)")
//    fun doesItemExistsInHistory(bookId: Int, chapterId: Int): Flow<Boolean>
}
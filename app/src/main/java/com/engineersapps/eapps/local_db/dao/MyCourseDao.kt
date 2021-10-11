package com.engineersapps.eapps.local_db.dao

import androidx.room.*
import com.engineersapps.eapps.models.my_course.MyCourse
import com.engineersapps.eapps.models.my_course.MyCourseBook
import kotlinx.coroutines.flow.Flow

@Dao
interface MyCourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMyCourses(myCourses: List<MyCourse>)

    @Query("SELECT * FROM my_course")
    fun getAllMyCourses(): Flow<List<MyCourse>>

    @Query("SELECT COUNT(invoiceid) FROM my_course")
    suspend fun getMyCourseItemsCount(): Int

    @Query("DELETE FROM my_course")
    suspend fun deleteAllMyCourses()

    @Transaction
    suspend fun updateAllMyCourses(myCourses: List<MyCourse>) {
        deleteAllMyCourses()
        addMyCourses(myCourses)
    }

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

    @Query("SELECT * FROM my_course_paid_books WHERE id IN (:bookIds)")
    suspend fun getMyCourseBookList(bookIds: List<Int>): List<MyCourseBook>

    @Query("DELETE FROM my_course_paid_books")
    suspend fun deleteAllMyCoursePaidBooks()

//    @Query("SELECT COUNT(id) FROM history")
//    fun getHistoryItemsCount(): Flow<Int>

//    @Query("SELECT COUNT(id) FROM my_course_paid_books WHERE id = :bookId")
//    suspend fun doesItemExistsInMyCourseBooks(bookId: Int): Int

//    @Query("SELECT EXISTS (SELECT 1 FROM favorite WHERE id = :id)")
//    fun doesItemExistsInHistory(bookId: Int, chapterId: Int): Flow<Boolean>
}
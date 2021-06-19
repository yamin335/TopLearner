package com.rtchubs.engineerbooks.local_db.dao

import androidx.room.*
import com.rtchubs.engineerbooks.models.home.CourseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCourseCategories(courseCategories: List<CourseCategory>)

    @Query("DELETE FROM course_category")
    suspend fun deleteAllCourseCategories()

    @Query("SELECT * FROM course_category")
    fun getAllCourseCategories(): Flow<List<CourseCategory>>

    @Transaction
    suspend fun updateAllCourseCategories(courseCategories: List<CourseCategory>) {
        deleteAllCourseCategories()
        addCourseCategories(courseCategories)
    }
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addChapters(bookChapters: ChapterItem): Long
//
//    @Query("SELECT * FROM chapters WHERE bookId = :bookId")
//    fun getBookChapters(bookId: String): Flow<ChapterItem>
//
//    @Update
//    fun updateUsers(bookChapters: BookChapters)
}
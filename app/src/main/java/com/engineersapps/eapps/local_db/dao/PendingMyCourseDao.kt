package com.engineersapps.eapps.local_db.dao

import androidx.room.*
import com.engineersapps.eapps.models.transactions.CreateOrderBody

@Dao
interface PendingMyCourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPendingCourse(pendingCourse: CreateOrderBody): Long

    @Query("SELECT * FROM pending_my_courses")
    suspend fun getAllPendingCourses(): List<CreateOrderBody>

    @Delete
    suspend fun deletePendingCourse(pendingCourse: CreateOrderBody)
}
package com.rtchubs.engineerbooks.local_db.dao

import androidx.room.*
import com.rtchubs.engineerbooks.models.registration.AcademicClass
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveClasses(classes: List<AcademicClass>)

    @Query("DELETE FROM all_classes")
    suspend fun deleteAllClasses()

    @Query("SELECT * FROM all_classes")
    fun getAllClasses(): Flow<List<AcademicClass>>

    @Transaction
    suspend fun updateAllAcademicClasses(classes: List<AcademicClass>) {
        deleteAllClasses()
        saveClasses(classes)
    }
}
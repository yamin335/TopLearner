package com.engineersapps.eapps.local_db.dao

import androidx.room.*
import com.engineersapps.eapps.models.registration.AcademicClass
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveClasses(classes: List<AcademicClass>)

    @Query("DELETE FROM all_classes")
    suspend fun deleteAllClasses()

    @Query("SELECT * FROM all_classes")
    fun getAllClasses(): Flow<List<AcademicClass>>

    @Query("SELECT * FROM all_classes")
    suspend fun getAllAcademicClasses(): List<AcademicClass>

    @Transaction
    suspend fun updateAllAcademicClasses(classes: List<AcademicClass>) {
        deleteAllClasses()
        saveClasses(classes)
    }
}
package com.rtchubs.engineerbooks.local_db.dao

import androidx.room.*
import com.rtchubs.engineerbooks.local_db.dbo.ChapterItem
import com.rtchubs.engineerbooks.models.home.ClassWiseBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BookChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBooks(books: List<ClassWiseBook>)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<ClassWiseBook>>

    @Transaction
    suspend fun updateBooks(books: List<ClassWiseBook>) {
        deleteAllBooks()
        addBooks(books)
    }

//    @Query("DELETE FROM chapters")
//    suspend fun deleteAllChapters()

    @Query("DELETE FROM chapters WHERE bookId = :bookId")
    suspend fun deleteAChapter(bookId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChapters(bookChapters: ChapterItem): Long

    @Query("SELECT * FROM chapters WHERE bookId = :bookId")
    fun getBookChapters(bookId: String): Flow<ChapterItem>

    @Transaction
    suspend fun updateChapters(bookChapters: ChapterItem) {
        deleteAChapter(bookChapters.bookId)
        addChapters(bookChapters)
    }

//
//    @Update
//    fun updateUsers(bookChapters: BookChapters)
}
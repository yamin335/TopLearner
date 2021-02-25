package com.rtchubs.engineerbooks.local_db.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rtchubs.engineerbooks.local_db.dao.BookChapterDao
import com.rtchubs.engineerbooks.local_db.dao.HistoryDao
import com.rtchubs.engineerbooks.local_db.dbo.ChapterItem
import com.rtchubs.engineerbooks.local_db.dbo.HistoryItem
import com.rtchubs.engineerbooks.models.home.ClassWiseBook

/**
 * Main database.
 */
@Database(
    entities = [
        ClassWiseBook::class,
        ChapterItem::class,
        HistoryItem::class
    ],
    version = 1,
    exportSchema = false
)

@TypeConverters(RoomDataTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookChapterDao(): BookChapterDao
    abstract fun historyDao(): HistoryDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(app: Application): AppDatabase = INSTANCE
            ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(
                        app
                    )
                        .also { INSTANCE = it }
            }

        private fun buildDatabase(app: Application) =
            Room.databaseBuilder(app, AppDatabase::class.java, "engineers_apps")
                // prepopulate the database after onCreate was called
//                .addCallback(object : Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        // Do database operations through coroutine or any background thread
//                        val handler = CoroutineExceptionHandler { _, exception ->
//                            println("Caught during database creation --> $exception")
//                        }
//
//                        CoroutineScope(Dispatchers.IO).launch(handler) {
//                            prePopulateAppDatabase(getInstance(app).loginDao())
//                        }
//                    }
//                })
                .build()

//        suspend fun prePopulateAppDatabase(loginDao: LoginDao) {
//            val admin = Login(0, "Administrator", "1234", 1, isActive = true, isAdmin = true, isLogged = false)
//            loginDao.insertLoginData(admin)
//        }
    }
}
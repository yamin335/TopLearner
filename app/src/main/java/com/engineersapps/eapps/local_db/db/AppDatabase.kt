package com.engineersapps.eapps.local_db.db

import android.app.Application
import androidx.room.*
import com.engineersapps.eapps.local_db.dao.*
import com.engineersapps.eapps.local_db.dbo.ChapterItem
import com.engineersapps.eapps.local_db.dbo.HistoryItem
import com.engineersapps.eapps.models.home.ClassWiseBook
import com.engineersapps.eapps.models.home.CourseCategory
import com.engineersapps.eapps.models.my_course.MyCourse
import com.engineersapps.eapps.models.my_course.MyCourseBook
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.models.transactions.CreateOrderBody


private const val DATABASE = "app_db_1"
private const val DATABASE_VERSION = 2

/**
 * Main database.
 */
@Database(
    entities = [
        CourseCategory::class,
        ClassWiseBook::class,
        MyCourse::class,
        MyCourseBook::class,
        ChapterItem::class,
        HistoryItem::class,
        AcademicClass::class,
        CreateOrderBody::class
    ],
    version = DATABASE_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ]
)

@TypeConverters(RoomDataTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookChapterDao(): BookChapterDao
    abstract fun historyDao(): HistoryDao
    abstract fun courseDao(): CourseDao
    abstract fun myCourseDao(): MyCourseDao
    abstract fun academicClassDao(): AcademicClassDao
    abstract fun pendingMyCourseDao(): PendingMyCourseDao

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
            Room.databaseBuilder(app, AppDatabase::class.java, DATABASE)//.fallbackToDestructiveMigration()
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
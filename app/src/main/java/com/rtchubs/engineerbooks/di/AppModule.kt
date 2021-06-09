package com.rtchubs.engineerbooks.di

import android.app.Application
import android.content.Context
import com.rtchubs.engineerbooks.local_db.dao.BookChapterDao
import com.rtchubs.engineerbooks.local_db.dao.HistoryDao
import com.rtchubs.engineerbooks.local_db.db.AppDatabase
import com.rtchubs.engineerbooks.prefs.AppPreferencesHelper
import com.rtchubs.engineerbooks.prefs.PreferencesHelper
import com.rtchubs.engineerbooks.util.AppConstants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class,
        NetworkModule::class,
        WorkerBindingModule::class
    ]
)
class AppModule {
    @Singleton
    @Provides
    fun provideDb(app: Application): AppDatabase {
        return AppDatabase.getInstance(app)
    }

    @Singleton
    @Provides
    fun provideBookChapterDao(db: AppDatabase): BookChapterDao {
        return db.bookChapterDao()
    }

    @Singleton
    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao {
        return db.historyDao()
    }

    //    @Singleton
    //    @Provides
    //    fun providePicasso(app: Application, okHttpClient: OkHttpClient) = Picasso.Builder(app)
    //        .downloader(OkHttp3Downloader(okHttpClient))
    //        .listener { _, _, e -> if (BuildConfig.DEBUG) e.printStackTrace() }
    //        .loggingEnabled(BuildConfig.DEBUG)
    //        .build()

    /*@Singleton
    @Provides
    fun provideProfileDao(db: AppDb): ProfileDao {
        return db.profileDao()
    }

    @Singleton
    @Provides
    fun provideBankDao(db: AppDb): BankDao {
        return db.bankDao()
    }

    @Singleton
    @Provides
    fun provideStatementDao(db: AppDb): StatementDao {
        return db.statementDao()
    }*/


    @Singleton
    @Provides
    internal fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper {
        return appPreferencesHelper
    }

    @Provides
    @PreferenceInfo
    internal fun providePreferenceName(): String {
        return AppConstants.PREF_NAME
    }

    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context {
        return application
    }
}
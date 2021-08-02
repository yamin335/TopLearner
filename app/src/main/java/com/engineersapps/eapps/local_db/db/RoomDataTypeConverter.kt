package com.engineersapps.eapps.local_db.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.engineersapps.eapps.models.chapter.BookChapter
import com.engineersapps.eapps.models.chapter.ChapterField
import com.engineersapps.eapps.models.home.Course
import java.lang.reflect.Type

class RoomDataTypeConverter {
    private val gson by lazy {
        Gson()
    }

    @TypeConverter
    fun courseListToJsonString(value: List<Course>): String? = gson.toJson(value)

    @TypeConverter
    fun jsonStringToCourseList(value: String): List<Course> = gson.fromJson(value, Array<Course>::class.java).toList()

    @TypeConverter
    fun bookChaptersToJsonString(value: List<BookChapter>): String? = gson.toJson(value)

    @TypeConverter
    fun jsonStringToBookChapters(value: String): List<BookChapter> = gson.fromJson(value, Array<BookChapter>::class.java).toList()

    @TypeConverter
    fun jsonStringToChapterField(value: String?): ChapterField? {
        return gson.fromJson(value, ChapterField::class.java)
    }

    @TypeConverter
    fun chapterFieldToJsonString(chapterField: ChapterField?): String? {
        return gson.toJson(chapterField)
    }

    @TypeConverter
    fun jsonStringToBookChapter(value: String): BookChapter? {
        return gson.fromJson(value, BookChapter::class.java)
    }

    @TypeConverter
    fun bookChapterToJsonString(bookChapter: BookChapter): String? {
        return gson.toJson(bookChapter)
    }

    @TypeConverter
    fun fromStringToList(value: String?): List<String?>? {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromLisToString(list: List<String?>?): String? {
        return gson.toJson(list)
    }

//    @TypeConverter
//    fun fromString(value: String?): ArrayList<String?>? {
//        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
//        return gson.fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun fromArrayLisr(list: ArrayList<String?>?): String? {
//        return gson.toJson(list)
//    }

}

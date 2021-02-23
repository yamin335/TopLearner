package com.rtchubs.engineerbooks.local_db.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rtchubs.engineerbooks.models.chapter.BookChapter
import com.rtchubs.engineerbooks.models.chapter.ChapterField
import java.lang.reflect.Type

class RoomDataTypeConverter {
    private val gson by lazy {
        Gson()
    }

    @TypeConverter
    fun jsonStringToChapterField(value: String?): ChapterField? {
        return gson.fromJson(value, ChapterField::class.java)
    }

    @TypeConverter
    fun chapterFieldToJsonString(chapterField: ChapterField?): String? {
        return gson.toJson(chapterField)
    }

    @TypeConverter
    fun jsonStringToBookChapter(value: String): BookChapter {
        return gson.fromJson(value, BookChapter::class.java)
    }

    @TypeConverter
    fun bookChapterToJsonString(bookChapter: BookChapter): String {
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

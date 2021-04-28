package com.rtchubs.engineerbooks.models.home

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class ClassWiseBookResponse(val code: Int?, val data: ClassWiseBookData?, val msg: String?)

@Entity(tableName = "books")
data class ClassWiseBook(
    var id: Int,
    @PrimaryKey(autoGenerate = false) val udid: String, val name: String?, val title: String?,
    val author: String?, var isPaid: Boolean?, val book_type_id: Int?,
    val price: Double?, val status: String?, val logo: String?): Serializable

data class ClassWiseBookData(val books: List<ClassWiseBook>?)



data class AllBookResponse(val code: Int?, val status: String?, val message: String?, val data: AllBookData?)

data class AllBookModel(val id: Int?, val uuid: String, val book_type_id: Int?, val name: String?,
                           val authors: String?, val title: String?, val description: String?, val is_archived: Int?,
                           val is_paid: Int?, val created_at: String?, val updated_at: String?, val price: Double?, val status: String?,val logo: String?)

data class AllBookData(val books: List<AllBookModel>?)

data class PaidBook(var bookID: Int?, var bookName: String?, var classID: Int?, var className: String?, var isPaid: Boolean, var price: Double): Serializable

data class CourseCategory(val udid: Int, val name: String?, val books: List<ClassWiseBook>?)

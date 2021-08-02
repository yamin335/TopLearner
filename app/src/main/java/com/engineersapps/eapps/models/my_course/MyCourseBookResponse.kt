package com.engineersapps.eapps.models.my_course

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class SingleBookResponse(val code: Int?, val data: SingleBookResponseData?, val msg: String?)

data class SingleBookResponseData(val book: MyCourseBook?)

@Entity(tableName = "my_course_paid_books")
data class MyCourseBook(
    var id: Int,
    @PrimaryKey(autoGenerate = false) val udid: String, val name: String?, val title: String?,
    val author: String?, var isPaid: Boolean?, val book_type_id: Int?,
    val price: Double?, val status: String?, val logo: String?): Serializable

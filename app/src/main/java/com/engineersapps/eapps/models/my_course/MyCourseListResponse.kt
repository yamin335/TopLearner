package com.engineersapps.eapps.models.my_course

import androidx.room.Entity
import androidx.room.PrimaryKey

data class MyCourseListRequest(val mobile: String?)

data class MyCourseListResponse(val code: Int?, val data: MyCourseListResponseData?, val msg: String?)

data class MyCourseListResponseData(val courses: List<MyCourse>?)

@Entity(tableName = "my_course")
data class MyCourse(@PrimaryKey(autoGenerate = false) val invoiceid: String, var title: String?,
                    var logo: String?, var book_id: Int?, var books: String?,
                    var endtime: String?, val createdAt: String?, val updatedAt: String?,
                    val student_id: Int?, val course_id: Int?, val total_amount: Int?,
                    val paid_amount: Int?, val due_amount: Int?, val mobile: String?,
                    val date: String?, var expiredate: String?, val Table: String?, val duration: Int?)

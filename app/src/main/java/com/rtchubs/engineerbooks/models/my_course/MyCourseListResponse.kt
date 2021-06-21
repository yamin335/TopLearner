package com.rtchubs.engineerbooks.models.my_course

data class MyCourseListRequest(val mobile: String?)

data class MyCourseListResponse(val code: Int?, val data: MyCourseListResponseData?, val msg: String?)

data class MyCourseListResponseData(val courses: List<MyCourse>?)

data class MyCourse(val udid: String?, var title: String?, var logo: String?, var book_id: Int?, val createdAt: String?, val updatedAt: String?,
                    val student_id: Int?, val course_id: Int?, val total_amount: Int?,
                    val paid_amount: Int?, val due_amount: Int?, val mobile: String?,
                    val date: String?, val expiredate: String?, val Table: String?)

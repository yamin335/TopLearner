package com.engineersapps.eapps.models.payment

data class CoursePaymentResponse(val code: Int?, val data: CoursePaymentData?, val msg: String?)

data class CoursePaymentData(val student_course: StudentCourse?)

data class StudentCourse(val udid: String?, val createdAt: String?,
                         val updatedAt: String?, val student_id: Int?,
                         val course_id: Int?, val total_amount: Int?,
                         val paid_amount: Int?, val due_amount: Int?,
                         val mobile: String?, val date: String?,
                         val expiredate: String?, val Table: String?)

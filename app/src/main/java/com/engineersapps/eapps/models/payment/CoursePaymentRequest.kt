package com.engineersapps.eapps.models.payment

data class CoursePaymentRequest(val mobile: String?,var invoiceid: String = "", val student_id: Int?,
                                val course_id: Int?, val total_amount: Int?,
                                val paid_amount: Int?, val duration: Int?)
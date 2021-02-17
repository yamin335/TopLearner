package com.rtchubs.engineerbooks.models

data class LiveClassScheduleResponse(val code: Int?, val status: String?, val message: String?, val data: LiveClassScheduleData?)

data class LiveClassScheduleData(val liveclassschedule: List<LiveClassSchedule>?)

data class LiveClassSchedule(val id: Int?, val created_at: String?, val updated_at: String?, val class_id: Int?,
                             val link: String?, val class_name: String?, val class_date: String?,
                             val class_date_time: String?, val description: String?, val archived: Int?)
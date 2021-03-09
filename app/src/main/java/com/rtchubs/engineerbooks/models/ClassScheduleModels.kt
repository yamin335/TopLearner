package com.rtchubs.engineerbooks.models

data class LiveClassScheduleResponse(val code: Int?, val data: LiveClassScheduleData?, val msg: String?)

data class LiveClassScheduleData(val live_class_schedules: List<LiveClassSchedule>?)

data class LiveClassSchedule(val udid: String?, val createdAt: String?,
                                         val updatedAt: String?, val link: String?,
                                         val class_name: String?, val class_id: Int?,
                                         val ClassDate: String?, val ClassDateTime: String?,
                                         val is_live: Boolean?, val archived: Boolean?, val Table: String?)
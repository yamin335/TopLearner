package com.rtchubs.engineerbooks.models.notice_board

data class NoticeResponse(val code: Int?, val status: String?, val message: String?, val data: NoticeResponseData?)

data class NoticeResponseData(val noticeboard: List<Notice>?)

data class Notice(val id: Int?, val date: String?, val status: Int?, val name: String?,
                  val title: String?, val description: String?, val created_at: String?,
                  val updated_at: String?)
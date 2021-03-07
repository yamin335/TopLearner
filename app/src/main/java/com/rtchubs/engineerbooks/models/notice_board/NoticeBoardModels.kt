package com.rtchubs.engineerbooks.models.notice_board

data class NoticeResponse(val code: Int?, val data: NoticeResponseData?, val msg: String?)

data class NoticeResponseData(val notices: List<Notice>?)

data class Notice(val udid: String?, val createdAt: String?, val updatedAt: String?,
                            val class_id: Int?, val name: String?, val title: String?,
                            val description: String?, val Date: String?, val status: Boolean?,
                            val archived: Boolean?, val Table: String?)
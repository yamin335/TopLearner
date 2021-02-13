package com.rtchubs.engineerbooks.models.chapter

import java.io.Serializable

data class ChapterResponse(val code: Int?, val status: String?, val message: String?,
                           val data: ChapterResponseData?)

data class BookChapter(val id: Int?, val book_id: Int?, val name: String?,
                       val title: String?, val logo: String?, val pdf: String?, val somadhan: String?,
                       val status: Number?, val created_at: String?, val updated_at: String?,
                       val fields: List<ChapterField>?):Serializable

data class ChapterResponseData(val chapters: List<BookChapter>?)

data class ChapterField(val id: Int?, val chapter_id: Int?, val book_id: Int?,
                        val name: String?, val title: String?, val folder: String?,
                        val logo: String?, val video_filename: String?, val uuid: String?,
                        val height: Any?, val width: Any?, val visible: Int?,
                        val showOptions: Int?, val isHovered: Int?, val position: Any?,
                        val is_copied: Int?, val lang: String?, val created_at: String?, val updated_at: String?):Serializable



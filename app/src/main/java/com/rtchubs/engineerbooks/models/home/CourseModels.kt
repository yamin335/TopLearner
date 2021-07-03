package com.rtchubs.engineerbooks.models.home

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class AllCourseCategoryResponse(val code: Int?, val message: String?,
                                     val data: AllCourseCategoryResponseData?): Serializable

data class AllCourseCategoryResponseData(val CourseCatagorys: List<CourseCategory>?): Serializable

@Entity(tableName = "course_category")
data class CourseCategory(@PrimaryKey(autoGenerate = false) val id: Int?, val created_at: String?, val updated_at: String?,
                          val title: String?, val name: String?, val courses: List<Course>?): Serializable

data class Course(val id: Int?, val name: String?, val created_at: String?,
                  val updated_at: String?, val book_id: Int?, val demo_book_id: Int?, val title: String?,
                  val vediolink: String?, val price: Int?, val discount_price: Int?,
                  val status: Int?, val description: String?, val class_name: String?,
                  val book_type_id: Int?, val logourl: String?, val starttime: String?,
                  val endtime: String?, val course_catagory_id: Int?,
                  val course_catagory_name: String?, val course_items: List<CourseItems>?,
                  val course_teachers: List<CourseTeacher>?,
                  val course_chapter: List<CourseChapter>?, val teachers: List<Teacher>?): Serializable

data class CourseItems(val id: Int?, val created_at: String?, val updated_at: String?,
                       val type: String?, val description: String?, val course_id: Int?): Serializable

data class CourseTeacher(val id: Int?, val created_at: String?, val updated_at: String?,
                         val teacher_id: Int?, val course_id: Int?): Serializable

data class CourseChapter(val id: Int?, val created_at: String?, val updated_at: String?,
                         val course_id: Int?, val title: String?, val name: String?,
                         val animation: List<Animation>?, var isExpanded: Boolean = false): Serializable

data class Animation(val id: Int?, val created_at: String?, val updated_at: String?,
                     val course_chapter_id: Int?, val title: String?, val name: String?, val type: String?): Serializable

data class Teacher(val id: Int?, val created_at: String?, val updated_at: String?,
                   val other_show: String?, val official: String?, val mobile: String?,
                   val image_url: String?, val institute: String?, val name: String?,
                   val laravel_through_key: Int?): Serializable






//data class AllCourseResponse(val message: String?, val code: Int?, val data: AllCourseResponseData?)
//
//data class AllCourseResponseData(val courses: CourseData?)
//
//data class CourseData(val current_page: Int?, val data: List<Course>?, val first_page_url: String?,
//                      val from: Int?, val last_page: Int?, val last_page_url: String?,
//                      val next_page_url: Any?, val path: String?, val per_page: Int?,
//                      val prev_page_url: Any?, val to: Int?, val total: Int?)
//
//data class Course(val id: Int?, val name: String?, val created_at: String?,
//                  val updated_at: String?, val book_id: Int?, val title: String?,
//                  val vediolink: String?, val price: Int?, val discount_price: Int?,
//                  val status: Int?, val description: String?, val class_name: String?,
//                  val book_type_id: Int?, val logourl: String?, val course_faq: List<CourseFaq>?,
//                  val course_items: List<CourseItem>?, val course_teachers: List<CourseTeacher>?,
//                  val course_content: List<CourseContent>?, val book: CourseBook?):Serializable
//
//data class CourseFaq(val id: Int?, val created_at: String?, val updated_at: String?,
//                                val question: String?, val answer: String?, val course_id: Int?, var isExpanded: Boolean = false):Serializable
//
//data class CourseItem(val id: Int?, val created_at: String?, val updated_at: String?,
//                                  val type: String?, val description: String?, val course_id: Int?):Serializable
//
//data class CourseTeacher(val id: Int?, val created_at: String?, val updated_at: String?,
//                                     val teacher_id: Int?, val course_id: Int?):Serializable
//
//data class CourseContent(val id: Int?, val created_at: String?, val updated_at: String?,
//                         val chapter_title: String?, val chapter: String?,
//                         val animation_title: String?, val animation: String?,
//                         val course_id: Int?, var isExpanded: Boolean = false):Serializable
//
//data class CourseBook(val id: Int?, val uuid: String?, val book_type_id: Int?,
//                      val name: String?, val authors: String?, val title: String?,
//                      val price: Int?, val status: String?, val description: String?,
//                      val is_archived: Int?, val is_paid: Any?, val created_at: String?,
//                      val updated_at: String?, val logo: String?, val updatedid: Int?,
//                      val createdid: Int?):Serializable





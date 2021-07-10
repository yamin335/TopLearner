package com.rtchubs.engineerbooks.models.home

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//data class AllCourseCategoryResponse(val code: Int?, val message: String?,
//                                     val data: AllCourseCategoryResponseData?): Serializable
//
//data class AllCourseCategoryResponseData(val CourseCatagorys: List<CourseCategory>?): Serializable

//data class Course(val id: Int?, val name: String?, val created_at: String?,
//                  val updated_at: String?, val book_id: Int?, val demo_book_id: Int?, val title: String?,
//                  val vediolink: String?, val price: Int?, val discount_price: Int?,
//                  val status: Int?, val description: String?, val class_name: String?,
//                  val book_type_id: Int?, val logourl: String?, val starttime: String?,
//                  val endtime: String?, val course_catagory_id: Int?,
//                  val course_catagory_name: String?, val course_items: List<CourseItems>?,
//                  val course_teachers: List<CourseTeacher>?,
//                  val course_chapters: List<CourseChapter>?, val teachers: List<Teacher>?): Serializable
//
//data class CourseItems(val id: Int?, val created_at: String?, val updated_at: String?,
//                       val type: String?, val description: String?, val course_id: Int?): Serializable
//
//data class CourseTeacher(val id: Int?, val created_at: String?, val updated_at: String?,
//                         val teacher_id: Int?, val course_id: Int?): Serializable
//
//data class CourseChapter(val id: Int?, val created_at: String?, val updated_at: String?,
//                         val course_id: Int?, val title: String?, val name: String?,
//                         val chapter_contents: List<Animation>?, var isExpanded: Boolean = false): Serializable
//
//data class Animation(val id: Int?, val created_at: String?, val updated_at: String?,
//                     val course_chapter_id: Int?, val title: String?, val name: String?, val type: String?): Serializable
//
//data class Teacher(val id: Int?, val created_at: String?, val updated_at: String?,
//                   val other_show: String?, val official: String?, val mobile: String?,
//                   val image_url: String?, val institute: String?, val name: String?,
//                   val laravel_through_key: Int?): Serializable






// result generated from /json

@Entity(tableName = "course_category")
data class CourseCategory(@PrimaryKey(autoGenerate = false) val id: Int?, val name: String?, val courses: List<Course>?): Serializable

data class CourseCategoryResponse(val code: Int?, val data: CourseCategoryResponseData?, val msg: String?): Serializable

data class CourseCategoryResponseData(val courses: List<Course>?): Serializable

data class Course(val id: Int?, val udid: String?, val name: String?, val catagory_id: Int?,
                  val catagory_name: String?, val title: String?, val vediolink: String?,
                  val logourl: String?,val description: String?, val price: Int?,
                  val discount_price: Int?,val class_name: String?, val book_id: Int?,
                  val demo_book_id: Int?,val course_teachers: List<Teacher>?, val course_contents: List<Any>?,
                  val course_items: List<CourseItems>?, val course_chapters: List<CourseChapter>?,
                  val book: ClassWiseBook?, val demo_book: ClassWiseBook?, val book_type_id: Int?): Serializable

data class Teacher(val image_url: String?, val name: String?, val institute: String?, val teacher_id: String?, val course_id: String?): Serializable

data class CourseItems(val udid: String?, val description: String?, val type: String?, val course_id: Int?): Serializable

data class CourseChapter(val udid: String?, val name: String?, val title: String?, val id: Int?, val chapter_contents: List<Animation>?, var isExpanded: Boolean = false): Serializable

data class Animation(val udid: String?, val title: String?, val name: String?, val type: String?): Serializable






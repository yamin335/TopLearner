package com.rtchubs.engineerbooks.models.home

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "course_category")
data class CourseCategory(@PrimaryKey(autoGenerate = false) val id: Int?, val name: String?, val courses: List<Course>?): Serializable

data class CourseCategoryResponse(val code: Int?, val data: CourseCategoryResponseData?, val msg: String?): Serializable

data class CourseCategoryResponseData(val courses: List<Course>?): Serializable

data class Course(val id: Int?, val udid: String?, val name: String?, val catagory_id: Int?,
                  val catagory_name: String?, val title: String?, val vediolink: String?,
                  val logourl: String?,val description: String?, val price: Int?,
                  val discount_price: Int?, val first_payment_title: String?,
                  val first_payment_amount: Int?, val second_payment_title: String?,
                  val second_payment_amount: Int?, val third_payment_title: String?,
                  val third_payment_amount: Int?, val class_name: String?, val book_id: Int?,
                  val demo_book_id: Int?,val course_teachers: List<Teacher>?, val course_contents: List<Any>?,
                  val course_items: List<CourseItems>?, val course_chapters: List<CourseChapter>?,
                  val book: ClassWiseBook?, val demo_book: ClassWiseBook?, val book_type_id: Int?,
                  val first_duration: String?, val second_duration: String?, val third_duration: String?): Serializable

data class Teacher(val image_url: String?, val name: String?, val institute: String?, val teacher_id: String?, val course_id: String?): Serializable

data class CourseItems(val udid: String?, val description: String?, val type: String?, val course_id: Int?): Serializable

data class CourseChapter(val udid: String?, val name: String?, val title: String?, val id: Int?, val chapter_contents: List<Animation>?, var isExpanded: Boolean = false): Serializable

data class Animation(val udid: String?, val title: String?, val name: String?, val type: String?): Serializable






package com.rtchubs.engineerbooks.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuizAnswerItem(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("serial")
    @Expose
    val serial: String?,

    @SerializedName("title")
    @Expose
    val title: String?,

    @SerializedName("is_correct_answer")
    @Expose
    val isCorrectAnswer: Boolean?
) : Serializable
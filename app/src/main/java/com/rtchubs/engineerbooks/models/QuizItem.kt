package com.rtchubs.engineerbooks.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuizItem(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("serial")
    @Expose
    val serial: String?,

    @SerializedName("question")
    @Expose
    val question: String?,

    val answers: List<QuizAnswerItem>?
) : Serializable
package com.rtchubs.engineerbooks.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Chapter(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("title")
    @Expose
    val title: String?,

    @SerializedName("image_url")
    @Expose
    val imageUrl: String?,

    @SerializedName("chapter_url")
    @Expose
    val chapterVideoUrl: String?
) : Serializable
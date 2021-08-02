package com.engineersapps.eapps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VideoItem(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("title")
    @Expose
    val title: String?,

    @SerializedName("image_url")
    @Expose
    val imageUrl: String?,

    val description: String?
) : Serializable

data class HomeClassListItem(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("name")
    @Expose
    val name: String?
) : Serializable
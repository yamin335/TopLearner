package com.engineersapps.eapps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.engineersapps.eapps.R
import java.io.Serializable

data class SubBook(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("name")
    @Expose
    val name: String? = null,

    @SerializedName("hospital")
    @Expose
    val hospital: String? = null,

    @SerializedName("book_url")
    @Expose
    val bookUrl: String? = null,


    @SerializedName("image")
    @Expose
    val profImage: Int? = R.drawable.engineers
) : Serializable {

}
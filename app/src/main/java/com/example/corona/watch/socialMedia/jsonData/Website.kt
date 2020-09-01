package com.example.corona.watch.socialMedia.jsonData


import com.example.corona.watch.socialMedia.jsonData.DetailWs
import com.google.gson.annotations.SerializedName

data class Website(
    @SerializedName("detail")
    val detail: DetailWs,
    @SerializedName("id")
    val id: String
)
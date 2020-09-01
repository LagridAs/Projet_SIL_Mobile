package com.example.corona.watch.videos.jsonData


import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("detail")
    val detail: Detail,
    @SerializedName("id")
    val id: String
)
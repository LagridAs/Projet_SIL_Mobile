package com.example.corona.watch.videos.jsonData


import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("date")
    val date: String,
    @SerializedName("src")
    val src: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("user")
    val user: String
)
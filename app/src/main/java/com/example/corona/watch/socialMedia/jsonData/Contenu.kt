package com.example.corona.watch.socialMedia.jsonData


import com.google.gson.annotations.SerializedName

data class Contenu(
    @SerializedName("cover")
    val cover: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("siteName")
    val siteName: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)
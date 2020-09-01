package com.example.corona.watch.articles.json


import com.google.gson.annotations.SerializedName

data class DetailArt(
    @SerializedName("author")
    val author: Author?,
    @SerializedName("contenu")
    val contenu: String?,
    @SerializedName("cover")
    val cover: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("title")
    val title: String?
)
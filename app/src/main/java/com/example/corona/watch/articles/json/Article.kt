package com.example.corona.watch.articles.json


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Article(
    @SerializedName("detail")
    val detailArt: DetailArt,
    @SerializedName("id")
    val id: String
):Serializable
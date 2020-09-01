package com.example.corona.watch.socialMedia.jsonData


import com.google.gson.annotations.SerializedName

data class DetailFb_Yt(
    @SerializedName("contenu")
    val contenu: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("src")
    val src: String,
    @SerializedName("type")
    val type: String
)
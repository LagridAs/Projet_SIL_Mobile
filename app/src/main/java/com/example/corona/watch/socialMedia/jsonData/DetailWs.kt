package com.example.corona.watch.socialMedia.jsonData


import com.example.corona.watch.socialMedia.jsonData.Contenu
import com.google.gson.annotations.SerializedName

data class DetailWs(
    @SerializedName("contenu")
    val contenu: Contenu,
    @SerializedName("date")
    val date: String,
    @SerializedName("src")
    val src: String,
    @SerializedName("type")
    val type: String
)
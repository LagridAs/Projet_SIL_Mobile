package com.example.articletest.jsonData

import com.example.corona.watch.articles.json.User
import com.google.gson.annotations.SerializedName

data class CommentToadd(
    @SerializedName("contenu")
    val contenu: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("heure")
    val heure: String,
    @SerializedName("user")
    val user: User
)
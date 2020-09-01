package com.example.corona.watch.articles.json


import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)
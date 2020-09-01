package com.example.corona.watch.articles.json


data class Comment(
    val detail: Detail,
    val id: String
)

data class Detail(
    val date: String? = null,
    val heure: String? = null,
    val user: User? = null,
    val contenu: String? = null
)
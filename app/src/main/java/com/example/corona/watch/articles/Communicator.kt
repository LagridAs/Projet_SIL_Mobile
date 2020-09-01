package com.example.corona.watch.articles


import com.example.corona.watch.articles.json.Article

interface Communicator {

    fun passArtIdToComment(artId:String)
    fun passArtToDetails(art:String)
    fun passPostId(idPost: String)
    fun passVideoId(idVideo: String)


}
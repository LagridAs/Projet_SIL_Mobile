package com.example.corona.watch.socialMedia.jsonData


import com.example.corona.watch.socialMedia.jsonData.DetailFb_Yt
import com.google.gson.annotations.SerializedName

data class Facebook(
    @SerializedName("detail")
    val detail: DetailFb_Yt?,
    @SerializedName("id")
    val id: String
)
package com.example.corona.watch.maps.province


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("nbConfirme")
    val nbConfirme: Int,
    @SerializedName("nbDeces")
    val nbDeces: Int,
    @SerializedName("nbRetablie")
    val nbRetablie: Int
)
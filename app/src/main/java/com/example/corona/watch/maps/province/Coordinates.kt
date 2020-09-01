package com.example.corona.watch.maps.province


import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("lengitude")
    val lengitude: String
)
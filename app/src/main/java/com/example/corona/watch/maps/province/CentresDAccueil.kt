package com.example.corona.watch.maps.province


import com.google.gson.annotations.SerializedName

data class CentresDAccueil(
    @SerializedName("coordinates")
    val coordinates: Coordinates,
    @SerializedName("name")
    val name: String
)
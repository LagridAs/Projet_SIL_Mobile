package com.example.corona.watch.maps.province


import com.google.gson.annotations.SerializedName

data class ProvinceItem(
    @SerializedName("CentresD_accueil")
    val centresDAccueil: List<CentresDAccueil>,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("wilaya")
    val wilaya: String
)
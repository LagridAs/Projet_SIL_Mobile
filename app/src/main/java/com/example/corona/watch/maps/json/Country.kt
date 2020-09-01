package com.example.corona.watch.maps.json

import com.google.gson.annotations.SerializedName

data class Country(

    // Le nom du pays
	@field:SerializedName("country")
	val country: String? = null,

    // Les informations sur la maladie dans le pays
	@field:SerializedName("data")
	val data: Data? = null
)
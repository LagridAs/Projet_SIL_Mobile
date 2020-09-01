package com.example.corona.watch.maps.json

import com.google.gson.annotations.SerializedName

data class Data (

	@field:SerializedName("nbSuspect")
	val nbSuspect: Int? = null,

	@field:SerializedName("nbRetablie")
	val nbRetablie: Int? = null,

	@field:SerializedName("nbDeces")
	val nbDeces: Int? = null,

	@field:SerializedName("nbPorteurs")
	val nbPorteurs: Int? = null,

	@field:SerializedName("nbConfirme")
	val nbConfirme: Int? = null
)
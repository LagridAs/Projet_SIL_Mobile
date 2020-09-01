package com.example.corona.watch.maps.dangerareas

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DangerArea(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("detail")
	val detail: Detail? = null
) : Parcelable

@Parcelize
data class Location(

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
) : Parcelable

@Parcelize
data class Detail(

	@field:SerializedName("location")
	val location: Location? = null,

	@field:SerializedName("rayon")
	val rayon: Int? = null
) : Parcelable

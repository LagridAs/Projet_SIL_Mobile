package com.example.corona.watch.healthcare.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoMed(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("data")
	val data: Data? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("rythme_cardiaque")
	val rythmeCardiaque: Int? = null,

	@field:SerializedName("poids")
	val poids: Int? = null,

	@field:SerializedName("temperature")
	val temperature: Double? = null
) : Parcelable

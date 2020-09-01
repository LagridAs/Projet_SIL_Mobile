package com.example.corona.watch.notification.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationObject(
	val id: String? = null,
	val data: Data? = null
) : Parcelable

@Parcelize
data class Data(
	val title: String? = null,
	val content: String? = null,
	val type: String? = null,
	val date:String? = null,
	val status: String? = null
) : Parcelable

package com.example.corona.watch.tools

import android.app.Application
import android.content.Context


class BaseApplication : Application() {
    companion object {
        lateinit var currentApplication: BaseApplication
        val appContext: Context
            get() = currentApplication.applicationContext
    }

    init {
        currentApplication = this
    }
    val domainName = "https://6365c919cb53.ngrok.io"
    //val domainName = ""

}
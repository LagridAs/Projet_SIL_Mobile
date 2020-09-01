package com.example.corona.watch.tools

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.corona.watch.R

class LoadingDialog(myactivity:Activity) {
    var activity : Activity
    lateinit var dialog: AlertDialog

    init {
        activity = myactivity
    }
    fun startLoadingDialog(){
        val builder : AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater : LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_loading,null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

}
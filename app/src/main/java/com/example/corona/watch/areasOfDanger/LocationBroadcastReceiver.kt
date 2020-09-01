package com.example.corona.watch.areasOfDanger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED ) {
            val i = Intent(p0, Location::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            p0?.startService(i)
        }
    }

}
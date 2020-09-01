package com.example.corona.watch.areasOfDanger

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log


class Location : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startJob()
    }

    private fun startJob(){
        val componentName = ComponentName(this, LocationService::class.java)
        val info = JobInfo.Builder(123,componentName)
            .setPersisted(true)
            .setPeriodic(15*60*1000).build()
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        if(scheduler.schedule(info) == JobScheduler.RESULT_SUCCESS){
            Log.d("scd","Job scheduled")
        } else {
            Log.d("scd","NOT")
        }
    }
    fun stopJob(){
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(123)
        Log.d("scd","Job cancelled")
    }
}
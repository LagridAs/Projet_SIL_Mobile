package com.example.corona.watch.tools


import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.app.PendingIntent
import android.content.SharedPreferences
import com.example.corona.watch.R
import com.example.corona.watch.main.BottomMenuFragment
import com.example.corona.watch.main.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {


    var TAG = ""

    var sp : SharedPreferences? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from!!)



        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            var message = remoteMessage.data.get("body")
            var title = remoteMessage.data.get("title")
            var type = remoteMessage.data.get("type")
            var channel : String
            var icon : Int
            var id : Int
            if(type=="article") {
                channel = "article_channel_id"
                id = 1000
            } else {
                channel = "map_channel_id"
                id = 1002
            }
            val intent = PendingIntent.getBroadcast(this, 0, Intent(this, MainActivity::class.java), 0)
            val notification = NotificationCompat.Builder(this,channel)
                .setSmallIcon(R.drawable.ic_logo_notif)
                .setColor(resources.getColor(R.color.lightViolet))
                .setContentText(message)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setDefaults(android.app.Notification.DEFAULT_SOUND)
                .setContentIntent(intent)
                .build()

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(id,notification)

            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val intent_broad = Intent()
            intent_broad.putExtra("type",type)
            intent_broad.action = "com.example.corona.watch.CUSTOM_INTENT"
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent_broad)
            updateNotifications(type)
        }
    }

    fun updateNotifications(state:String?){
        sp = getSharedPreferences("notification",Context.MODE_PRIVATE)
        if(state=="article"){
            var editor = sp!!.edit()
            editor.putInt("article", sp!!.getInt("article",0)+1)
            editor.commit()
        } else if (state == "map") {
            var editor = sp!!.edit()
            editor.putBoolean("map", true)
            editor.commit()
        }

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("New Token : ",p0)
    }

}

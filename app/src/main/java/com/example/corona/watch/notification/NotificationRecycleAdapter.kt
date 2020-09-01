package com.example.corona.watch.notification

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.corona.watch.R
import com.example.corona.watch.notification.models.NotificationObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_form_signal.view.*
import kotlinx.android.synthetic.main.layout_notification_object.view.*

class NotificationRecycleAdapter(private val cellClickListener: CellClickListener) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var items = ArrayList<NotificationObject>()
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var mRequestQueue: RequestQueue
    var accessToken = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_notification_object,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is NotificationViewHolder -> {
                if(items[position].data!!.status=="NON_LU")
                {
                    holder.itemView.setBackgroundColor(Color.parseColor("#DCDCDC"))
                    holder.itemView.setOnClickListener {
                        it.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        cellClickListener.onCellClickListener(it)
                    }
                } else if (items[position].data!!.status=="LU"){
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }

                holder.bind(items[position])
            }
        }
    }



    fun submitList(notificationList : ArrayList<NotificationObject>){
        items = notificationList
    }

    class NotificationViewHolder constructor(itemView : View)
        : RecyclerView.ViewHolder(itemView){
        private val notificationContent: TextView? = itemView.content_notif
        private val notificationDate: TextView? = itemView.date_notif

        fun bind(notification : NotificationObject){
            notificationContent?.text = notification.data!!.content
            notificationDate?.text = notification.data!!.date
            itemView.tag = notification.id
        }
    }

}
package com.example.corona.watch.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.notification.models.Data
import com.example.corona.watch.notification.models.NotificationObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_notification.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

class NotificationFragment : Fragment(), CellClickListener {

    lateinit var notificationRecycleAdapter: NotificationRecycleAdapter
    var data = ArrayList<NotificationObject>()
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var mRequestQueue: RequestQueue
    var accessToken : String? = ""
    var getUrl : String =  "/api/users/"+mUser!!.uid + "/notifications"
    var putUrl : String =  "/api/users/"+mUser!!.uid + "/notifications/"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRequestQueue = Volley.newRequestQueue(context)
        getToken()
        getUrl = (activity as MainActivity).domainName + getUrl
        putUrl = (activity as MainActivity).domainName + putUrl
        data.add(NotificationObject("1",data = Data(
            "title","content","ZONE","2020-06-25","NON_LU"
        )
        ))

        getNotifications(getUrl,object : VolleyCallBack{
            override fun onSuccess() {
                try {
                    initRecycleView()
                    notificationRecycleAdapter.submitList(data)
                }catch (e:Exception){}
            }
        })

    }


    private fun initRecycleView(){
        notification_recycleview.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingItemDecoration = TopSpacingItemDecoration(5)
            addItemDecoration(topSpacingItemDecoration)
            notificationRecycleAdapter = NotificationRecycleAdapter(this@NotificationFragment)
            adapter = notificationRecycleAdapter

        }
    }
    private fun getNotifications(getUrl: String, callback:VolleyCallBack?) {

        val mJsonObjectRequest = @SuppressLint("SimpleDateFormat")
        object : StringRequest(Request.Method.GET, getUrl,
            Response.Listener { response ->

                var jsonArray: JSONArray = JSONArray(response)
                var gson: Gson = Gson()
                var jsonObject: JSONObject = JSONObject()
                var notificationTempList = ArrayList<NotificationObject>()

                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    var data = jsonObject.getJSONObject("data")
                    if(data.getString("type") == "ZONES"){
                        notificationTempList.add(gson.fromJson(jsonObject.toString(), NotificationObject::class.java))
                    }
                }

                notificationTempList.sortedWith(compareBy { SimpleDateFormat("yyyy-MM-dd HH:mm").parse(it.data!!.date)})

                data = notificationTempList
                Log.d("reponsenotification",data.toString())
                callback?.onSuccess()

                //it : JSON Object
            }, Response.ErrorListener {
                it.printStackTrace()
            }) {}
        mJsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        mRequestQueue.add(mJsonObjectRequest)


    }
    // Interface qui nous aide à simuler une reqeute http synchronne
    interface VolleyCallBack {
        fun onSuccess()
    }

    override fun onCellClickListener(it:View) {
        updateNotification(putUrl + "${it.tag}?status=LU")
    }

    private fun updateNotification(url : String){

        val putRequest = object : StringRequest(Request.Method.PUT, url,
            Response.Listener { _ ->
                Log.d("puturl", url)
            }, Response.ErrorListener { error ->
                error.printStackTrace()
            }) {


            override fun getHeaders(): MutableMap<String, String> {
                var params : MutableMap<String, String> = mutableMapOf()
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Bearer $accessToken"
                return params
            }

        }
        putRequest.retryPolicy = DefaultRetryPolicy(
            0,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        mRequestQueue.add(putRequest)



    }
    private fun getToken() : String? {

        var token : String? = ""

        mUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val idToken: String? = it.result?.token
                Log.d("Real token",idToken)
                accessToken = idToken
            } else {
                // Handle error -> task.getException();
            }
        }
        return token
    }

}
interface CellClickListener {
    fun onCellClickListener(it:View)
}
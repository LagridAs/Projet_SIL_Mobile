package com.example.corona.watch.areasOfDanger

import android.Manifest
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.maps.dangerareas.DangerArea
import com.example.corona.watch.tools.BaseApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.mapbox.mapboxsdk.geometry.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*


class LocationService : JobService(){
    val TAG = "LocationService"
    var jobCancelled = false
    var database : SharedPreferences? = null
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var accessToken : String? = ""
    private var urlRequest: String = BaseApplication.currentApplication.domainName +"/api/zones/"
    private var postUrl : String = BaseApplication.currentApplication.domainName + "/api/users/"+mUser!!.uid + "/notifications"
    var whereAmI : LatLng = LatLng(0.0, 0.0)
    // file pour ajouter les requetes http
    lateinit var mRequestQueue: RequestQueue

    // ArrayList pour sauvegarder la liste des pays et ses infos
    var dangerAreasList: ArrayList<DangerArea> = ArrayList<DangerArea>()
    private var locationManager: LocationManager? = null


    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG,"Job started")
        mRequestQueue = Volley.newRequestQueue(applicationContext)
        urlRequest = urlRequest
        locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        database = getSharedPreferences("Location",Context.MODE_PRIVATE)
        Log.d("locationtags",":"+database!!.getBoolean("safe",false).toString())
        Log.d("locationtags",":"+database!!.getString("baladiya",null).toString())
        getDangerAreas(urlRequest,object :VolleyCallBack{
            override fun onSuccess() {
                try {
                    doBackgroundWork(p0)
                } catch (e:Exception){}
            }
        })
        return true
    }
    interface VolleyCallBack {
        fun onSuccess()
    }

    private fun calculateDistanceTo(point1: LatLng, point2: LatLng): Double {
        return distance(point1.latitude,point2.latitude,point1.longitude,point2.longitude)
    }
    private fun distance(
        lat1: Double,
        lat2: Double,
        lon1: Double,
        lon2: Double
    ): Double {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        var lat1 = lat1
        var lat2 = lat2
        var lon1 = lon1
        var lon2 = lon2
        lon1 = Math.toRadians(lon1)
        lon2 = Math.toRadians(lon2)
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)

        // Haversine formula
        val dlon = lon2 - lon1
        val dlat = lat2 - lat1
        val a = (sin(dlat / 2).pow(2.0)
                + (cos(lat1) * cos(lat2)
                * sin(dlon / 2).pow(2.0)))
        val c = 2 * asin(sqrt(a))

        // Radius of earth in kilometers. Use 3956
        // for miles
        val r = 6371.0

        // calculate the result
        return c * r
    }

    fun isInDanger(point: LatLng, area: DangerArea):Boolean {
        var dist = calculateDistanceTo(point,latLngArea(area))
        dist -= area.detail!!.rayon!!.toDouble()
        return dist<=0
    }


    private fun latLngArea(area : DangerArea): LatLng {
        return LatLng(area.detail!!.location!!.latitude!!.toDouble(),area.detail!!.location!!.longitude!!.toDouble())
    }

    fun latLngLocation(p0: android.location.Location?):LatLng {
        return LatLng(p0?.latitude!!,p0?.longitude!!)
    }

    fun getClosestDangerArea() : DangerArea?{
        var closestArea : DangerArea? = null
        var closestDistance = Double.MAX_VALUE
        for(area in dangerAreasList){
            Log.d("areaofdanger",calculateDistanceTo(whereAmI,latLngArea(area)).toString())
            if(calculateDistanceTo(whereAmI,latLngArea(area))<closestDistance){
                closestArea = area
            }
        }
        return closestArea
    }

    fun messageOfDanger(): Boolean {
        val closestArea = getClosestDangerArea()
        if(closestArea != null && isInDanger(whereAmI,closestArea)){
            return true
        }
        return false
    }


    private fun getDangerAreas(urlRequest: String, callback: VolleyCallBack?) {
        // Création d'une requte http de type Get
        // urlInternational : String = "http://2d738da6.ngrok.io/api/carte/international"
        Log.d("insideget","get")
        val mJsonObjectRequest = object : StringRequest(Request.Method.GET, urlRequest,
            Response.Listener { response ->
                var jsonArray: JSONArray = JSONArray(response)
                var gson: Gson = Gson()
                var jsonObject: JSONObject = JSONObject()
                var areas_temp = ArrayList<DangerArea>()
                //transformer la réponse en une liste de Country
                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    areas_temp.add(gson.fromJson(jsonObject.toString(), DangerArea::class.java))
                }

                dangerAreasList = areas_temp


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

        // Ajouter la requete http construit dans la file des requetes http
        // pour le lancer
        mRequestQueue.add(mJsonObjectRequest)


    }

    private fun doBackgroundWork(p0: JobParameters?){
        val handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()
        val someHandler = Handler(handlerThread.looper)
        someHandler.postDelayed(object:Runnable {
            override fun run() {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }

                try {
                    getToken()
                    // Request location updates
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0L,
                        0f,
                        locationListener
                    )
                } catch (ex: SecurityException) {
                    Log.d("myTag", "Security Exception, no location available")
                }
                jobFinished(p0,false)
            }

        },10)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(p0: android.location.Location?) {
            Log.d("locationtag", "" + p0?.longitude + ":" + p0?.latitude)
            var gcd: Geocoder = Geocoder(applicationContext, Locale("en"))
            try {
                var addresses: List<Address> = gcd.getFromLocation(p0?.latitude!!, p0?.longitude!!, 1)
                if (jobCancelled) return
                if (addresses.isNotEmpty()) {
                    whereAmI = latLngLocation(p0)
                    if(messageOfDanger() && (database!!.getBoolean("safe",false) || addresses[0].locality != database!!.getString("baladiya",null))) {
                        Log.d("locationtags",":"+database!!.getBoolean("safe",false).toString())
                        Log.d("locationtags",":"+database!!.getString("baladiya",null).toString())
                        Log.d("locationtags",dangerAreasList.toString())
                        val notification = NotificationCompat.Builder(baseContext,"DANGER_ZONE_CHANNEL_ID")
                            .setSmallIcon(R.drawable.ic_logo_notif)
                            .setColor(Color.RED)
                            .setContentTitle("تحذير")
                            .setContentText("انت متواجد في "+addresses[0].locality +".هذه منطقة خطر .").setAutoCancel(true)
                            .build()

                        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        manager.notify(1003,notification)

                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                        postNotification(postUrl,"تحذير","انت متواجد في "+addresses[0].locality +".هذه منطقة خطر .","ZONES",simpleDateFormat.format(Date()))

                    }
                    var editor = database!!.edit()
                    editor.putString("baladiya",addresses[0].locality)
                    editor.putBoolean("safe", !messageOfDanger())
                    editor.commit()


                }}catch (e:Exception){
                Log.d("error","an error has occured!")
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun postNotification(url : String, title :String,content:String, type : String, currentDate:String){
        var body: JSONObject = JSONObject()

        body.put("date", currentDate)
        body.put("type", type)
        body.put("title", title)
        body.put("content", content)

        val bodyString = body.toString()
        val postRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { _ ->
                Log.d("success", "message")
            }, Response.ErrorListener { error ->
                error.printStackTrace()
            }) {


            override fun getBody(): ByteArray? {
                return try {
                    bodyString?.toByteArray()
                } catch (e: Exception) {
                    Log.w("body ", "error")
                    null
                }
            }


            override fun getHeaders(): MutableMap<String, String> {
                var params : MutableMap<String, String> = mutableMapOf()
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Bearer $accessToken"
                return params
            }

        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )


        mRequestQueue.add(postRequest)



    }


    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d(TAG,"Job cancelled")
        jobCancelled = true
        return false
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
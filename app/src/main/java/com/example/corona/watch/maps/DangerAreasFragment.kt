package com.example.corona.watch.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.maps.dangerareas.DangerArea
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.fragment_danger_areas.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.math.*


class DangerAreasFragment : Fragment() , OnMapReadyCallback, PermissionsListener {
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    var whereAmI : LatLng = LatLng(0.0, 0.0)
    private var mapView: MapView? = null
    // url pour récupérer les informations de tous les pays
    private var urlRequest: String = "/api/zones/"

    // file pour ajouter les requetes http
    lateinit var mRequestQueue: RequestQueue

    // ArrayList pour sauvegarder la liste des pays et ses infos
    var dangerAreasList: ArrayList<DangerArea> = ArrayList<DangerArea>()

    override fun onCreate(savedInstanceState: Bundle?) {
        context?.let {
            Mapbox.getInstance(it,
                "pk.eyJ1IjoiYWRtaW4taXNsYW0iLCJhIjoiY2s4MGtyd2FnMGhmeDNldW5tYXNlMG54aiJ9.DqCW5md1l6YykOAkokL80A" ) }
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_danger_areas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRequestQueue = Volley.newRequestQueue(context)
        urlRequest = (activity as MainActivity).domainName  + urlRequest
        mapView = mapNationaleZones
        mapView!!.getMapAsync(this)

        myLocationButtonOff.setOnClickListener {
            (activity as MainActivity).supportFragmentManager
                .beginTransaction().
                replace(R.id.main_frame,NationalMapFragment()).commit()
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/admin-islam/ck8iqtk0i1rdf1iqds2fcutgv")) {
            mapboxMap.uiSettings.isRotateGesturesEnabled = false
            try {
                enableLocationComponent(it);

            } catch ( uriSyntaxException : URISyntaxException) {
                uriSyntaxException.printStackTrace()
            }
        }
        getDangerAreas(urlRequest,object:VolleyCallBack{
            override fun onSuccess() {
                try {
                    for(dangerArea in dangerAreasList){
                        mapboxMap.addPolygon(
                            generatePerimeter(
                                LatLng(dangerArea.detail!!.location!!.latitude!!.toDouble(), dangerArea.detail!!.location!!.longitude!!.toDouble()),
                                dangerArea.detail!!.rayon!!.toDouble(),
                                64
                            )!!
                        )

                        val message = messageOfDanger()
                        if(message!=null){
                            val snack = Snackbar.make(mapNationaleZones,message,Snackbar.LENGTH_INDEFINITE)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                val view1: TextView = snack.view
                                    .findViewById(R.id.snackbar_text) as TextView
                                view1.layoutDirection = View.LAYOUT_DIRECTION_RTL
                            }
                            snack.show()
                        }
                    }
                }catch (e:Exception){}

            }

        })



    }
    private fun calculateDistanceTo(point1:LatLng, point2:LatLng): Double {
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

    fun isInDanger(point:LatLng,area: DangerArea):Boolean {
        var dist = calculateDistanceTo(point,latLngArea(area))
        dist -= area.detail!!.rayon!!.toDouble()
        return dist<=0
    }

    private fun distanceToDanger(point:LatLng, area: DangerArea):Double {
        var dist = calculateDistanceTo(point,latLngArea(area))
        dist -= area.detail!!.rayon!!.toDouble()
        return dist
    }

    private fun latLngArea(area : DangerArea): LatLng{
        return LatLng(area.detail!!.location!!.latitude!!.toDouble(),area.detail!!.location!!.longitude!!.toDouble())
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

    fun messageOfDanger() : String?{
        val closestArea = getClosestDangerArea()
        if(closestArea != null){
            return if(isInDanger(whereAmI,closestArea)){
                "أنت متواجد في منطقة خطر . يرجى توخي الحذر ."
            }else {
                "أنت تبعد عن أقرب منطقة خطر بحوالي  "+ floor(distanceToDanger(whereAmI,closestArea)) +" كم ."
            }
        }
        return null
    }

    private fun getDangerAreas(urlRequest: String, callback: VolleyCallBack?) {
        // Création d'une requte http de type Get
        // urlInternational : String = "http://2d738da6.ngrok.io/api/carte/international"
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(context, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(context, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
        }
    }

    private fun generatePerimeter(
        centerCoordinates: LatLng,
        radiusInKilometers: Double,
        numberOfSides: Int
    ): PolygonOptions? {
        val positions: ArrayList<LatLng> = ArrayList()
        val distanceX =
            radiusInKilometers / (111.319 * Math.cos(centerCoordinates.latitude * Math.PI / 180))
        val distanceY = radiusInKilometers / 110.574
        val slice = 2 * Math.PI / numberOfSides
        var theta: Double
        var x: Double
        var y: Double
        var position: LatLng
        for (i in 0 until numberOfSides) {
            theta = i * slice
            x = distanceX * cos(theta)
            y = distanceY * sin(theta)
            position = LatLng(
                centerCoordinates.latitude + y,
                centerCoordinates.longitude + x
            )
            positions.add(position)
        }
        return PolygonOptions()
            .addAll(positions)
            .fillColor(Color.RED)
            .alpha(0.4f)
    }

    @SuppressLint("MissingPermission")
    fun enableLocationComponent(loadedMapStyle: Style) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

// Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
                .trackingGesturesManagement(true)
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()


// Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

// Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

// Enable to make the LocationComponent visible
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                } else {
                    isLocationComponentEnabled = true

// Set the LocationComponent's camera mode
                    cameraMode = CameraMode.TRACKING

// Set the LocationComponent's render mode
                    renderMode = RenderMode.COMPASS

                    locationEngine!!.getLastLocation(object :
                        LocationEngineCallback<LocationEngineResult> {
                        override fun onSuccess(result: LocationEngineResult?) {
                            whereAmI = LatLng(
                                result!!.lastLocation!!.latitude,
                                result!!.lastLocation!!.longitude
                            )
                        }

                        override fun onFailure(exception: Exception) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }


    interface VolleyCallBack {
        fun onSuccess()
    }
}
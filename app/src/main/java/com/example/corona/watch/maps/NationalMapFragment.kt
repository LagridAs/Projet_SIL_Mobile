package com.example.corona.watch.maps


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.maps.province.CentresDAccueil
import com.example.corona.watch.maps.province.Data
import com.example.corona.watch.maps.province.ProvinceItem
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.fragment_carte_nationale.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList


class NationalMapFragment : Fragment() {
    var urlnational : String? =  "/api/carte/national"
    lateinit var mRequestQueue : RequestQueue
    var dataProvinces  : ArrayList<ProvinceItem> = ArrayList<ProvinceItem>()


    var flag = true
    val TAG = ""
    private var mapView: MapView? = null
    private var v:View?=null
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
        v=inflater.inflate(R.layout.fragment_carte_nationale, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        urlnational = (activity as MainActivity).domainName+urlnational

        mapView = v?.findViewById(R.id.mapNationale)
        mapView?.onCreate(savedInstanceState)
        val myuri = "mapbox://styles/admin-islam/ck8iqtk0i1rdf1iqds2fcutgv"
        mapView?.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.Builder().fromUri(myuri)) {
                mapboxMap.uiSettings.isRotateGesturesEnabled = false
                try {

                    mRequestQueue = Volley.newRequestQueue(context)
                    Log.d("","9bal jsonParseeeeee")

                    jsonParse(object :
                        VolleyCallBack {
                        override fun onSuccess() {
                            try {
                                addInfectedLayer(it)
                            }catch (e:Exception){}

                        }
                    })

                } catch ( uriSyntaxException : URISyntaxException) {
                    uriSyntaxException.printStackTrace()
                }

            }
            mapboxMap.addOnMapClickListener{point ->
                onClickProvince(point,mapboxMap)
                true
            }
        }

        myLocationButton.setOnClickListener {
            (activity as MainActivity).supportFragmentManager
                .beginTransaction().
                replace(R.id.main_frame,DangerAreasFragment()).commit()
        }
    }

    private fun jsonParse(callback: VolleyCallBack)  {
        val mJsonObjectRequest = object : StringRequest(
            Method.GET,urlnational,
            Response.Listener {response ->

                var jsonArray= JSONArray(response)
                var gson= Gson()
                var jsonObject= JSONObject()
                Log.d(TAG,"9ballllll onSuccessssssssssssssssssssssss")


                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    Log.d(TAG,"lalaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    dataProvinces.add(gson.fromJson(jsonObject.toString(), ProvinceItem::class.java))
                }

                callback.onSuccess()

                //it : JSON Object
            },Response.ErrorListener {
                it.printStackTrace()
            }) {}

        mJsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        mRequestQueue.add(mJsonObjectRequest)
    }

    @SuppressLint("InflateParams")
    fun showCustomDialog(nomProvince:String,nbrConfirme:Int,nbrDeces:Int,nbrRetablis:Int,centres:List<CentresDAccueil>) {
        val alertDialog: AlertDialog
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_custom_view, null)

        val headertxt = dialogView.findViewById<TextView>(R.id.header)
        headertxt.text = nomProvince
        val confirme= dialogView.findViewById<TextView>(R.id.nbrconfirme)
        confirme.text = nbrConfirme.toString()
        val deces= dialogView.findViewById<TextView>(R.id.nbrmorts)
        deces.text = nbrDeces.toString()
        val retablis= dialogView.findViewById<TextView>(R.id.nbrgueris)
        retablis.text = nbrRetablis.toString()

        //spinner
        val centerNames:ArrayList<String> = arrayListOf()
        for (element in centres){
            centerNames.add(element.name)
        }

        // Initializing an ArrayAdapter
        val adapter = context?.let {
            ArrayAdapter(
                it, // Context
                android.R.layout.simple_spinner_item, // Layout
                centerNames // Array
            )
        }

        // Set the drop down view resource
        adapter?.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with dapter
        val spinnerCentre= dialogView.findViewById<Spinner>(R.id.centreSpinner)
        spinnerCentre.adapter = adapter
        //fin spinner
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setOnDismissListener { }
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        //alertDialog.window!!.getAttributes().windowAnimations = R.style.PauseDialogAnimation
        alertDialog.show()
    }

    @Throws(IOException::class)
    fun loadJSONFromAsset(context: Context, jsonFileName: String): String {
        (context.assets).open(jsonFileName).let {
            val buffer = ByteArray(it.available())
            it.read(buffer)
            it.close()
            return String(buffer, Charset.forName("UTF-8"))
        }
    }

    private fun addInfectedLayer(it : Style) {
        val urbanAreasSource = GeoJsonSource("urban-areas",
            context?.let { it1 -> loadJSONFromAsset(it1,"dzzz.json") })
        if(filterProvinces() != ""){
            it.addSource(urbanAreasSource)
            var urbanArea : FillLayer = FillLayer("urban-areas-fill", "urban-areas")
            urbanArea.setProperties(
                PropertyFactory.fillColor(Color.parseColor("#E87050")),
                PropertyFactory.fillOpacity(0.4f)
            )
            urbanArea.withFilter(
                Expression.raw(filterProvinces())
            )

            it.addLayerBelow(urbanArea, "border")
        }
    }

    private fun filterProvinces() : String{
        var filter = ""
        if (infectedProvinces().isNotEmpty()) {
            filter = "[\"any\""
            for (element in infectedProvinces()) {
                filter += ",[\"==\",[\"get\",\"name\"],\"${element}\"]"
            }
            filter+="]"

        }
        return filter
    }

    private fun infectedProvinces():ArrayList<String>{
        var infectedProvinces  = ArrayList<String>()
        for (i in dataProvinces) {
            if (i.data.nbConfirme > 0 ){
                infectedProvinces.add(i.wilaya)
            }
        }

        return infectedProvinces
    }

    private fun onClickProvince(point: LatLng, mapboxMap:MapboxMap){

        val provinceNameAr : String = getProvinceNameAr(point)
        val provinceName : String = getProvinceName(point)
        var data: Data
        var centres:List<CentresDAccueil>

        if (provinceNameAr.isNotBlank()) {
            val position = CameraPosition.Builder()
                .target(LatLng(point.latitude, point.longitude))
                .zoom(6.0)
                .tilt(10.0)
                .build()
            mapboxMap.cameraPosition = position
            data= getDataProvince(provinceName)
            centres= getCentreProvince(provinceName)

            showCustomDialog(
                provinceNameAr,
                data.nbConfirme,
                data.nbDeces,
                data.nbRetablie,
                centres
            )




        }
    }

    private fun getProvinceNameAr(point : LatLng) : String {
        var provinceName = ""
        try {
            var geocoder = Geocoder(context, Locale("ar"))
            var addresses : List<Address> = geocoder.getFromLocation(point.latitude, point.longitude, 1)
            if (addresses.isNotEmpty())
            {
                provinceName =addresses[0].adminArea
            }
        }catch (e: Exception){

        }
        return provinceName
    }

    private fun getProvinceName(point : LatLng) : String {

        var provinceName = ""
        try {
            var geocoder= Geocoder(context)
            var addresses : List<Address> = geocoder.getFromLocation(point.latitude, point.longitude, 1)



            if (addresses.isNotEmpty())
            {
                provinceName =addresses[0].adminArea
            }
        }catch (e: Exception){

        }
        return provinceName
    }
    private fun getDataProvince(province: String) : Data {
        var data = Data(0,0,0)
        for(i in dataProvinces) {
            if (province.contains(i.wilaya)){
                data = i.data
                break
            }
        }
        return data
    }
    private fun getCentreProvince(province:String):List<CentresDAccueil>{
        var centres= listOf<CentresDAccueil>()
        for(i in dataProvinces) {
            if (province.contains(i.wilaya)){
                centres = i.centresDAccueil
                break
            }
        }
        return centres
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }



    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)

    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }
    interface VolleyCallBack {
        fun onSuccess()
    }


}

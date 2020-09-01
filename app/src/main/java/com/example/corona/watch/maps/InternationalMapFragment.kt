package com.example.corona.watch.maps

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.maps.json.Country
import com.example.corona.watch.maps.json.Data
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.maps.Style.OnStyleLoaded
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import com.example.corona.watch.main.MainActivity
import kotlinx.coroutines.Dispatchers.IO


class InternationalMapFragment : Fragment(), OnMapReadyCallback {


    // url pour récupérer les informations de tous les pays

    private var urlInternational: String = "/api/carte/international"
    // file pour ajouter les requetes http
    lateinit var mRequestQueue: RequestQueue

    private var v:View?=null
    // ArrayList pour sauvegarder la liste des pays et ses infos
    var dataCountries: ArrayList<Country> = ArrayList<Country>()

    private var mapView: MapView? = null

    // le token de Mapbox token pour acceder à la carte
    private val MAPBOX_ACCESS_TOKEN =
        "pk.eyJ1IjoiYWRtaW4taXNsYW0iLCJhIjoiY2s4MGtyd2FnMGhmeDNldW5tYXNlMG54aiJ9.DqCW5md1l6YykOAkokL80A"

    override fun onCreate(savedInstanceState: Bundle?) {
        context?.let {
            Mapbox.getInstance(it, MAPBOX_ACCESS_TOKEN ) }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Créer une file qui va gérer les requetes http
        mRequestQueue = Volley.newRequestQueue(context)
        urlInternational  = (activity as MainActivity).domainName+urlInternational
        // Chargement de la carte internationale
        loadMap(savedInstanceState)
    }

    // La Méthode pour charger le map utilisant l'API mapbox
    fun loadMap(savedInstanceState: Bundle?) {
        mapView = internationalMap
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
    }


    // la méthode qui prend en charge les détails de la requete http
    fun jsonParse(urlInternational: String,callback:VolleyCallBack?) {
        // Création d'une requte http de type Get
        // urlInternational : String = "http://2d738da6.ngrok.io/api/carte/international"
        val mJsonObjectRequest = object : StringRequest(Request.Method.GET, urlInternational,
            Response.Listener { response ->

                // listener lorqu'on reçoit la reponse de la reqeuete http
                // La réponse de la requete sera un JSONArray
                var jsonArray: JSONArray = JSONArray(response)
                var gson: Gson = Gson()
                var jsonObject: JSONObject = JSONObject()
                var countries_temp = ArrayList<Country>()
                //transformer la réponse en une liste de Country
                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    countries_temp.add(gson.fromJson(jsonObject.toString(), Country::class.java))
                }

                Log.d("Testing this", "$countries_temp  $urlInternational")
                dataCountries = countries_temp

                // cet appel va nous permettre d'appler la fonction passer en entrée
                // aprés que l'opération de récupuration des données soit fini
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

    // La méthode pour colorer les pays infectés
    fun addInfectedLayer(it: Style) {

        if (filterCountries() != null) {

            // récupérer un fichier local geojson qui contient les frontieres des pays
            var urbanAreasSource: GeoJsonSource = GeoJsonSource(
                "urban-areas",
                loadJsonFromAsset("ne_50m_admin_0_countries.geojson")
            )

            // ajouter un nouveau layer à partir du fichier geojson chargé
            it.addSource(urbanAreasSource)
            var urbanArea: FillLayer = FillLayer("urban-areas-fill", "urban-areas")


            // colorer le nouveau layer ajouté
            urbanArea.setProperties(
                PropertyFactory.fillColor(Color.parseColor("#E87050")),
                PropertyFactory.fillOpacity(0.4f)
            )


            // Ajouter un filtre (des pays infectés)
            urbanArea.withFilter(
                filterCountries()!!
            )

            // Appliquer le nouveau layer au-dessus de notre carte
            it.addLayerBelow(urbanArea, "admin-1-boundary-bg")
        }


    }

    // La méthode qui est appelé aprés un click sur un pays
    // qui affiche les informations de ce pays
    fun onClickCountry(point: LatLng) {

        Log.d("inside","start")
        // obtenir le nom du pays cliqué
        val countryName: String = getCountryName(point)
        val countryNameEnglish: String = getCountryNameEnglish(point)

        Log.d("insideCountry",countryNameEnglish)
        Log.d("insideCountryAr",countryName)
        Log.d("insideCountryAr","after 2")

        if (countryName.isNotBlank()) {
            Log.d("inside","startIf")
            var gson: Gson = Gson()

            // démarrer l'activité PopCountry qui va afficher les informations d'un pays
            // On passe les infos d'un pays et le nom de ce pays
            var intent: Intent = Intent(context, PopCountry::class.java)


            intent.putExtra("nameCountry", countryName)
            intent.putExtra("data", gson.toJson(getDataCountry(countryNameEnglish)))
            Log.d("inside","beforeActivity")
            startActivity(intent)
            Log.d("inside","afterActivity")
        }
        Log.d("inside","afteronClick")
    }

    // La méthode pour retrouver les informations d'un pays depuis
    // ArrayList of countries : datacountries
    // en introduisant le nom de ce pays
    fun getDataCountry(country: String): Data? {
        var data: Data? = Data(0, 0, 0, 0, 0)
        for (i in dataCountries) {
            if (i.country == country) {
                data = i.data
                break
            }
        }


        return data
    }

    // La méthode pour recuperer la liste des pays infectées
    // depuis ArrayList of countries : datacountries
    fun infectedCountries(): ArrayList<String> {

        // on a choisi la critère : nb de pourteurs > 1 pour le moment
        var infectedCountries = ArrayList<String>()
        for (i in dataCountries) {
            if (i.data!!.nbPorteurs!! > 0) {
                infectedCountries.add(i.country!!)
            }
        }

        return infectedCountries
    }


    //la méthode pour charger un GeoJSON file depuis le dossier assets.
    private fun loadJsonFromAsset(filename: String): String? {
        try {
            val `is` = requireContext().assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            return String(buffer, Charset.forName("UTF-8"))

        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

    }

    // la méthode pour générer un filtre des pays infecté
    fun filterCountries(): Expression? {

        var filter: String = ""
        if (infectedCountries().isNotEmpty()) {
            filter = "[\"any\""
            for (element in infectedCountries()) {
                filter += ",[\"==\",[\"get\",\"sovereignt\"],\"$element\"]"
            }
            filter += "]"
            return Expression.raw(filter)
        }
        return null

    }

    // la méthode pour obtenir le nom d'un pays en Arabe
    fun getCountryName(point: LatLng): String {
        var countryName: String = ""
        try {
            var gcd: Geocoder = Geocoder(context, Locale("ar"))
            var addresses: List<Address> = gcd.getFromLocation(point.latitude, point.longitude, 1)
            if (addresses.size > 0) {
                countryName = addresses.get(0).countryName
            }
        } catch (e: Exception) {

        }
        return countryName
    }

    // la méthode pour obtenir le nom d'un pays en Anglais
    fun getCountryNameEnglish(point: LatLng): String {

        var countryName: String = ""
        try {
            var gcd: Geocoder = Geocoder(context, Locale("en"))
            var addresses: List<Address> = gcd.getFromLocation(point.latitude, point.longitude, 1)



            if (addresses.size > 0) {
                countryName = addresses.get(0).countryName
            }
        } catch (e: Exception) {

        }
        return countryName
    }


    // Interface qui nous aide à simuler une reqeute http synchronne
    interface VolleyCallBack {
        fun onSuccess()
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

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(
            Style.Builder()
                .fromUri("mapbox://styles/admin-islam/ckbk4169v08g81ip78dbksnjf")
        ) {style->

            // desactiver la rotation de la carte
            mapboxMap.uiSettings.isRotateGesturesEnabled = false

            try {

                CoroutineScope(IO).launch {
                    // Créer et exécuter une requete http

                    jsonParse(urlInternational,object :
                        VolleyCallBack {
                        override fun onSuccess() {
                            try{
                                addInfectedLayer(style)
                                (activity as MainActivity).restoreMapNotification()
                            }
                            catch (e:Exception){}
                        }
                    })
                }


            } catch (uriSyntaxException: URISyntaxException) {
                uriSyntaxException.printStackTrace()
            }
        }

        // Ajouter d'un click Listener pour la carte
        mapboxMap.addOnMapClickListener { point ->

            // Définir l'action effectué s'il y a un clique sur la carte
            onClickCountry(point)
            true
        }
    }

}


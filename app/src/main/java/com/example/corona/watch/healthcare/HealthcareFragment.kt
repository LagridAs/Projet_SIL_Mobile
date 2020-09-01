package com.example.corona.watch.healthcare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.healthcare.pojo.InfoMed
import com.example.corona.watch.main.MainActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_healthcare.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HealthcareFragment : Fragment() {
    // url pour récupérer les informations de tous les pays
    private var urlGet: String = "/api/suivi/"

    var token_ : String? = ""
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser!!
    // file pour ajouter les requetes http
    lateinit var mRequestQueue: RequestQueue
    var arraySeries = ArrayList<ILineDataSet>()

    // ArrayList pour sauvegarder la liste des pays et ses infos
    var infoMedList: ArrayList<InfoMed> = ArrayList<InfoMed>()
    var temperatureList:ArrayList<Entry> = ArrayList<Entry>()
    var weightList:ArrayList<Entry> = ArrayList<Entry>()
    var cardiacRhythmList:ArrayList<Entry> = ArrayList<Entry>()
    var dates = ArrayList<Float>()
    var mLineChart : LineChart? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_healthcare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sections.selectTab(sections.getTabAt(2))
        urlGet = (activity as MainActivity).domainName + urlGet  + mUser!!.uid
        getToken()
        mLineChart = graph
        mLineChart!!.isDragEnabled = true
        mLineChart!!.isScaleXEnabled = true
        mLineChart!!.isScaleYEnabled = true
        mLineChart!!.isHorizontalScrollBarEnabled = true
        val xAxis: XAxis = mLineChart!!.xAxis
        xAxis.labelRotationAngle = (-45).toFloat()
        sections.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val series: LineDataSet  = when (tab) {
                    sections.getTabAt(1) -> {
                        LineDataSet(temperatureList,"الحرارة (درجة مئوية)")

                    }
                    sections.getTabAt(0) -> {
                        LineDataSet(cardiacRhythmList,"دقات القلب (نبضة/الدقيقة)")

                    }
                    else -> {
                        LineDataSet(weightList,"الوزن (كغ)")
                    }

                }
                arraySeries = ArrayList<ILineDataSet>()
                arraySeries.add(series)
                val lineData = LineData(arraySeries)
                mLineChart!!.data = lineData
                configChart()
            }

        })
        mRequestQueue = Volley.newRequestQueue(context)



        healthcareInfo.setOnClickListener {

            var intent: Intent = Intent(context, HealthcareInfoActivity::class.java)
            startActivity(intent)
        }

        getRequest(urlGet,object :VolleyCallBack{
            override fun onSuccess() {
                try {
                    afterGet()
                }catch (e:Exception){}
            }

        })



    }

    fun afterGet(){
        for(i in infoMedList){
            temperatureList.add(Entry(SimpleDateFormat("yyyy-MM-dd").parse(i.date).time.toFloat(),i.data!!.temperature!!.toFloat()))
            weightList.add(Entry(SimpleDateFormat("yyyy-MM-dd").parse(i.date).time.toFloat(),i.data!!.poids!!.toFloat()))
            cardiacRhythmList.add(Entry(SimpleDateFormat("yyyy-MM-dd").parse(i.date).time.toFloat(),i.data!!.rythmeCardiaque!!.toFloat()))
            dates.add(SimpleDateFormat("yyyy-MM-dd").parse(i.date).time.toFloat())
        }
        temperatureList.reverse()
        weightList.reverse()
        cardiacRhythmList.reverse()
        dates.reverse()

        if(weightList.isNotEmpty()){
            val series = LineDataSet(weightList,"الوزن")
            arraySeries = ArrayList<ILineDataSet>()
            arraySeries.add(series)
            val lineData = LineData(arraySeries)
            mLineChart!!.data = lineData
            configChart()
        }

    }
    fun configChart(){
        val xAxis: XAxis = mLineChart!!.xAxis
        xAxis.valueFormatter = myValueFormatter(dates)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        mLineChart!!.setOnChartValueSelectedListener(object :OnChartValueSelectedListener{
            override fun onNothingSelected() {
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                Toast.makeText(context,"date: "+getDateTime(e?.x!!),Toast.LENGTH_LONG).show()
            }

        })
        mLineChart!!.invalidate()
    }

    fun getRequest(urlGet:String,callback: VolleyCallBack?){

        val mJsonObjectRequest = object : StringRequest(Request.Method.GET, urlGet,
            Response.Listener { response ->
                Log.d("reponsssse",response)
                // listener lorqu'on reçoit la reponse de la reqeuete http
                // La réponse de la requete sera un JSONArray
                var jsonArray: JSONArray = JSONArray(response)
                var gson: Gson = Gson()
                var jsonObject: JSONObject = JSONObject()
                var info_temp = ArrayList<InfoMed>()
                //transformer la réponse en une liste de Country
                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    info_temp.add(gson.fromJson(jsonObject.toString(), InfoMed::class.java))
                }

                infoMedList = info_temp

                // cet appel va nous permettre d'appler la fonction passer en entrée
                // aprés que l'opération de récupuration des données soit fini
                callback?.onSuccess()

                //it : JSON Object
            }, Response.ErrorListener {
                it.printStackTrace()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                var params : MutableMap<String, String> = mutableMapOf()
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Bearer $token_"
                return params
            }
        }
        mJsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Ajouter la requete http construit dans la file des requetes http
        // pour le lancer
        mRequestQueue.add(mJsonObjectRequest)




    }


    private fun getToken() : String? {

        var token : String? = ""

        mUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val idToken: String? = it.result?.token
                Log.d("Real token",idToken)
                token_ = idToken
            } else {
                // Handle error -> task.getException();
            }
        }
        return token
    }


    interface VolleyCallBack {
        fun onSuccess()
    }
    private fun getDateTime(s: Float): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            "Time"
        }
    }

    class myValueFormatter(private val xValsDateLabel: ArrayList<Float>) : ValueFormatter() {
        private fun getDateTime(s: Float): String {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val netDate = Date(s.toLong())
                sdf.format(netDate)
            } catch (e: Exception) {
                "Time"
            }
        }
        override fun getFormattedValue(value: Float): String {
            return getDateTime(value)
        }

    }

}

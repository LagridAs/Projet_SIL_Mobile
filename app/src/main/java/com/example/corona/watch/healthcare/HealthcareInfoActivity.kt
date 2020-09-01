package com.example.corona.watch.healthcare

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.maps.PopCountry
import com.example.corona.watch.tools.BaseApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_healthcare_info.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HealthcareInfoActivity : AppCompatActivity() {
    private var urlPost: String = "/api/suivi/"
    var token_ : String? = ""
    lateinit var mRequestQueue: RequestQueue
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val domainName  = BaseApplication.currentApplication.domainName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthcare_info)

        // les Paramètres d'affichage
        var dm : DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        var width:Int = dm.widthPixels
        var height:Int = dm.heightPixels
        window.setLayout((width*.85).toInt(),(height*.7).toInt())


        var  myCalendar : Calendar = Calendar.getInstance()
        var date_listener = DatePickerDialog.OnDateSetListener { p0, p1, p2, p3 ->
            myCalendar.set(Calendar.YEAR, p1);
            myCalendar.set(Calendar.MONTH, p2);
            myCalendar.set(Calendar.DAY_OF_MONTH, p3)
            updateLabel(myCalendar)
        }

        date_picker.setOnClickListener {
            DatePickerDialog(this, date_listener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()

        }

        urlPost = domainName + urlPost + mUser!!.uid
        getToken()


        mRequestQueue = Volley.newRequestQueue(this)
        send_btn.setOnClickListener {
            if (checkFields()) {
                postRequest(
                    urlPost,
                    weightField.text.toString().toInt(),
                    cardiacField.text.toString().toInt(),
                    temperatureField.text.toString().toDouble(),
                    date_picker.text.toString()
                )
                finish()
            }
            else Toast.makeText(this,"يرجى مراجعة المعلومات المقدمة .",Toast.LENGTH_SHORT).show()

        }
    }
    // mise à jour de la date ,filtrer par date
    fun updateLabel(myCalendar : Calendar) {
        var myFormat: String = "yyyy-MM-dd"
        var sdf : SimpleDateFormat = SimpleDateFormat(myFormat)

        var tdate : String = sdf.format(myCalendar.time)
        date_picker.setText(tdate)
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
    fun checkFields():Boolean{
        return try {
            weightField.text.toString().toInt()
            cardiacField.text.toString().toInt()
            temperatureField.text.toString().toDouble()
            date_picker.text.toString()
            true
        } catch (e:Exception){
            false
        }
    }

    fun postRequest(urlPost : String, weight :Int,cardiac : Int,temperature : Double,currentDate:String) {
        var body: JSONObject = JSONObject()

        body.put("uid", FirebaseAuth.getInstance().currentUser?.uid)
        body.put("date", currentDate)
        var data: JSONObject = JSONObject()
        data.put("poids", weight)
        data.put("temperature", temperature)
        data.put("rythme_cardiaque", cardiac)
        body.put("data",data)


        val bodyString = body.toString()
        Log.d("body post", bodyString)


        var obj = JSONObject()
        val request = object : StringRequest(Request.Method.POST, urlPost,
            Response.Listener { response ->
                Log.d("response ", response)
                Toast.makeText(
                    this, "تم ارسال المعلومات بنجاح .",
                    Toast.LENGTH_LONG
                ).show()
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
                params["Authorization"] = "Bearer $token_"
                return params
            }

        }
        request.retryPolicy = DefaultRetryPolicy(
            0,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        mRequestQueue.add(request)



    }


}

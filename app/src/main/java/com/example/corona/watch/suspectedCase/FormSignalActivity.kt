package com.example.corona.watch.suspectedCase

import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_form_signal.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.corona.watch.R
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.tools.BaseApplication
import com.example.corona.watch.tools.LoadingDialog
import com.github.tntkhang.gmailsenderlibrary.GMailSender
import com.github.tntkhang.gmailsenderlibrary.GmailListener
import com.google.firebase.auth.FirebaseUser
import org.json.JSONObject


class FormSignalActivity : AppCompatActivity() {
    var currentPath : String? = null
    var typeFile : String? = null
    val mStorageRef : StorageReference = FirebaseStorage.getInstance().reference
    lateinit var loadingDialog : LoadingDialog
    val urlCasSuspect = BaseApplication.currentApplication.domainName+"/api/suspects/"
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var token_ : String? = ""
    lateinit var queue : RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_signal)

        loadingDialog = LoadingDialog(this)

        val intent : Intent = intent
        currentPath  = intent.getStringExtra("path")!!
        typeFile = intent.getStringExtra("type")
        Glide.with(applicationContext)
            .load(currentPath.toString())
            .into(imageView)

        getToken()

        queue = Volley.newRequestQueue(this)



        nomUtilisateur.setText(FirebaseAuth.getInstance().currentUser?.displayName)
        val sdf = SimpleDateFormat("hh:mm dd-MM-yyyy")
        val currentDate = Date()
        val currentDateFormatted = sdf.format(currentDate)
        date.setText(currentDateFormatted)

        main_btn.setOnClickListener{ goMainPage()}
        back_btn.setOnClickListener { goBackPage() }
        send_btn.setOnClickListener {
            upload(currentDate)

        }

    }
    fun goMainPage() {
        val intent_main : Intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent_main)
    }
    fun goBackPage() {
        val intent_back : Intent = Intent(applicationContext, SignalActivity::class.java)
        startActivity(intent_back)
    }

    fun getExt(uri : Uri) : String? {
        val cr : ContentResolver = contentResolver
        var typeMap : MimeTypeMap = MimeTypeMap.getSingleton()
        return typeMap.getExtensionFromMimeType(cr.getType(uri))
    }
    fun upload(currentDate : Date){
        val file = Uri.fromFile(File(currentPath!!))
        var uploadTask : UploadTask
        var media = ""
        if(typeFile=="IMAGE") {
            media = "images/${file.lastPathSegment}"

        }  else {
            media = "videos/${file.lastPathSegment}"

        }

        var dateRef = mStorageRef.child(media)
        uploadTask  = dateRef.putFile(file)


        val id = 1
        var mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //var mBuilder  : NotificationCompat.Builder = NotificationCompat.Builder(this)
        var mBuilder = NotificationCompat.Builder(this,"progress_channel_id")
        mBuilder.setContentTitle("ارسال الملف")
            .setContentText("جاري الإرسال")
            .setSmallIcon(R.drawable.ic_upload)
            .setColor(resources.getColor(R.color.lightViolet))

        goMainPage()
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("$progress%")

            // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
            mBuilder.setProgress(100, progress.toInt(), false)
            // Displays the progress bar for the first time.

            mNotifyManager.notify(id, mBuilder.build())
            // Sleeps the thread, simulating an operation


        }.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(applicationContext, R.string.fileNotSent, Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            // Handle successful uploads on complete
            // ...
            Toast.makeText(applicationContext, R.string.fileSent, Toast.LENGTH_SHORT).show()
            // When the loop is finished, updates the notification
            mBuilder.setContentText(resources.getString(R.string.fileSent))
                // Removes the progress bar
                .setProgress(0, 0, false)
            mNotifyManager.notify(id, mBuilder.build())

            var sdf_day = SimpleDateFormat("dd-MM-yyyy")
            var date_passed = sdf_day.format(currentDate)

            dateRef.getDownloadUrl().addOnSuccessListener {
                Log.d("url firebase",it.toString())
                postCas(urlCasSuspect,description_field.text.toString(),
                    it.toString(),date_passed)
            }

        }

    }

    fun postCas(url : String, description :String,media : String,currentDate:String) {
            var body: JSONObject = JSONObject()

            body.put("date", currentDate)
            body.put("status", "NON_LU")
            var proof: JSONObject = JSONObject()
            proof.put("src", media)
            proof.put("type", typeFile!!)
            body.put("proof", proof)
            body.put("description", description)
            body.put("user", FirebaseAuth.getInstance().currentUser?.uid)

            val body_string = body.toString()
            Log.d("bosy post", body_string)

        Log.d("hadahowa",token_)

            var obj = JSONObject()
            val post_request = object : StringRequest(Request.Method.POST, url,
                Response.Listener { response ->
                    Log.d("response ", response)
                    Toast.makeText(
                        this, "تم تقديم الشكوى بنجاح .",
                        Toast.LENGTH_LONG
                    ).show()
                    sendEmail(
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        FirebaseAuth.getInstance().currentUser?.displayName!!,
                        typeFile!!, description
                    )
                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                }) {


                override fun getBody(): ByteArray? {
                    return try {
                        body_string?.toByteArray()
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
            post_request.retryPolicy = DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            queue.add(post_request)



    }

    fun sendEmail(id: String,name : String,media:String,description: String) {
        GMailSender.withAccount("grp.corona.watch@gmail.com", "coron@2020")
            .withTitle("Nouveau cas suspect")
            .withBody("l'utilisateur "+ name +" avec id : "+ id + " a signalé un cas suspect . il a signalé un cas  avec un(e) "+ media +" comme preuve et la description suivante : "+description)
            .withSender("Utilisateur")
            .toEmailAddress("ga_djamaa@esi.dz")
            .withListenner(object:GmailListener{
                override fun sendFail(err: String?) {
                    Log.w("error",err)
                }
                override fun sendSuccess() {
                    Log.d("message","email envoyé")
                }
            })
            .send();
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

}
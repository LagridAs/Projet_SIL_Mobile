package com.example.corona.watch.profile

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.tools.BaseApplication
import com.example.corona.watch.tools.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_form_video_users.*
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class FormVideoUsersActivity : AppCompatActivity() {
    var currentPath : String? = null
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var accessToken : String? = ""
    private val mStorageRef : StorageReference = FirebaseStorage.getInstance().reference
    private val urlVideoInternaute = BaseApplication.currentApplication.domainName+"/api/videos/"

    private lateinit var queue : RequestQueue
    lateinit var currentDate : Date
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_video_users)

        getToken()
        val intent : Intent = intent
        currentPath  = intent.getStringExtra("path")!!

        formvideo.setVideoPath(currentPath.toString())
        // create an object of media controller

        queue = Volley.newRequestQueue(this)
        // create an object of media controller
        val mediaController = MediaController(this)
        mediaController.setAnchorView(formvideo)
        mediaController.setMediaPlayer(formvideo)
        formvideo.setMediaController(mediaController)

        nom_utilisateur_formvideo.text = FirebaseAuth.getInstance().currentUser?.displayName
        val sdf = SimpleDateFormat("hh:mm dd-MM-yyyy")
        currentDate = Date()
        val currentDateFormatted = sdf.format(currentDate)
        date_formvideo.text = currentDateFormatted

        main_btn_form_video.setOnClickListener{
            goMainPage()
        }
        back_btn_form_video.setOnClickListener {
            goBackPage()
        }

        send_btn_form_video.setOnClickListener {
            sendVideo()
        }
    }

    private fun goMainPage() {
        val intentMain : Intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intentMain)
    }
    private fun goBackPage() {
        val intentBack : Intent = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intentBack)
    }

    fun sendVideo(){
        val file = Uri.fromFile(File(currentPath!!))
        var uploadTask : UploadTask
        var dateRef = mStorageRef.child("internautes/${file.lastPathSegment}")

        uploadTask  = dateRef.putFile(file)


        val id = 2
        var mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //var mBuilder  : NotificationCompat.Builder = NotificationCompat.Builder(this)
        var mBuilder = NotificationCompat.Builder(this,"inernautes_channel_id")
        mBuilder.setContentTitle("ارسال الفيديو")
            .setContentText("جاري الإرسال")
            .setSmallIcon(R.drawable.ic_upload).color = resources.getColor(R.color.lightViolet)

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

            // When the loop is finished, updates the notification
            mBuilder.setContentText(resources.getString(R.string.fileSent))
                // Removes the progress bar
                .setProgress(0, 0, false)
            mNotifyManager.notify(id, mBuilder.build())

            var sdfDay = SimpleDateFormat("dd-MM-yyyy")
            var datePassed = sdfDay.format(currentDate)

            dateRef.downloadUrl.addOnSuccessListener {
                Log.d("urlfirebase",it.toString())
                postVideoInternaute(urlVideoInternaute,title_field_video_form.text.toString(),
                    it.toString(),datePassed)
            }

        }

    }
    private fun postVideoInternaute(url : String, title :String, src : String, currentDate:String){
        var body: JSONObject = JSONObject()

        body.put("date", currentDate)
        body.put("src", src)
        body.put("title", title)
        body.put("user", FirebaseAuth.getInstance().currentUser?.uid)
        val bodyString = body.toString()
        val postRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("success", "message")
                Toast.makeText(applicationContext, R.string.fileSent, Toast.LENGTH_SHORT).show()
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
            0,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(postRequest)



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
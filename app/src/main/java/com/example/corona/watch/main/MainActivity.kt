package com.example.corona.watch.main

import android.Manifest
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.example.corona.watch.R
import com.example.corona.watch.articles.ArticleFragment
import com.example.corona.watch.articles.CommentFragment
import com.example.corona.watch.articles.Communicator
import com.example.corona.watch.articles.DetailsArtFragment
import com.example.corona.watch.maps.InternationalMapFragment
import com.example.corona.watch.tools.BaseApplication
import com.example.corona.watch.videos.VideoFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.fragment_main_menu.*
import java.lang.Exception


class MainActivity : AppCompatActivity(), Communicator {
    val TAG = "TOKEN"
    lateinit var top_fragment : Fragment
    lateinit var under_menu_fragment : Fragment
    lateinit var bottom_fragment : Fragment
    lateinit var main_fragment : Fragment
    lateinit var mPref : SharedPreferences
    val domainName  = BaseApplication.currentApplication.domainName
    private val filter = IntentFilter("com.example.corona.watch.CUSTOM_INTENT")
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        init()
        try {
            if(intent.getStringExtra("page") != null && intent.getStringExtra("page") == "videos"){
                initVideos()
            }
        }catch (e:Exception){}
        getToken()
        tokenNotification()
        checkRunTimePermission()
    }

    private fun checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    10
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        (this as Activity?)!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    val dialog =
                        AlertDialog.Builder(this)
                    dialog.setTitle("Permission Required")
                    dialog.setCancelable(false)
                    dialog.setMessage("You have to Allow permission to access user location")
                    dialog.setPositiveButton(
                        "Settings"
                    ) { dialog, which ->
                        val i = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(
                                "package",
                                this.packageName, null
                            )
                        )
                        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(i, 1001)
                    }
                    val alertDialog = dialog.create()
                    alertDialog.show()
                }
            }
        }
    }

    fun initVideos(){

        under_menu_fragment  = BlankFragment()
        supportFragmentManager.beginTransaction().replace(R.id.under_top_menu,under_menu_fragment).commit()
        under_top_menu.visibility = View.GONE
        // Charger la fragment MapFragment qui affiche le map international
        main_fragment  = VideoFragment()
        supportFragmentManager.beginTransaction().replace(R.id.main_frame,main_fragment).commit()

        (bottom_fragment as BottomMenuFragment).clickVideos()
    }
    fun init(){

        // Charger les 2 bars en haut
         top_fragment  = TopMenuFragment()
        supportFragmentManager.beginTransaction().add(R.id.top_menu,top_fragment).commit()
        under_menu_fragment  = UnderMenuFragment()
        supportFragmentManager.beginTransaction().add(R.id.under_top_menu,under_menu_fragment).commit()

        // Charger le bar en bas
         bottom_fragment = BottomMenuFragment()
        supportFragmentManager.beginTransaction().add(R.id.bottom_menu,bottom_fragment).commit()


        // Charger la fragment MapFragment qui affiche le map international
        main_fragment  = InternationalMapFragment()
        supportFragmentManager.beginTransaction().add(R.id.main_frame,main_fragment).commit()
    }

    fun getToken() : String? {

        var token : String? = null

        mUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful()) {
                val idToken: String? = it.getResult()?.getToken()
                Log.d("Real token",idToken)
                token = idToken
            } else {
                // Handle error -> task.getException();
            }
        }

        return token
    }

    private val the_receiver = object : BroadcastReceiver() {

        override fun onReceive(c: Context, i: Intent) {

            var state = i.extras!!.get("type") as String
            updateView(state)
        }
    }
    // Set When broadcast event will fire.

        fun updateView(state : String?){
        if(state=="article"){
            (bottom_fragment as BottomMenuFragment).updateArticleUI()
        } else if (state == "map") {
            (bottom_fragment as BottomMenuFragment).updateMapUI()
        }

    }

    fun getInVideos(){
        Toast.makeText(this,"VIDEOS",Toast.LENGTH_SHORT).show()
    }
    fun restoreArticleNotification(){
        (bottom_fragment as BottomMenuFragment).restoreArticleUI()
    }

    fun restoreMapNotification(){
        (bottom_fragment as BottomMenuFragment).restoreMapUI()
    }


    override fun onStart() {
        // Register reciever if activity is in front
        LocalBroadcastManager.getInstance(this).registerReceiver(the_receiver, filter);

        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(the_receiver);

    }

    fun navigateSocialMedia(){
        under_top_menu.visibility = View.VISIBLE
    }

    fun navigateArticles(){
        under_top_menu.visibility = View.GONE
    }


    fun navigateMap(){
        under_top_menu.visibility = View.VISIBLE
    }

    fun navigateHealthCare(){
        under_top_menu.visibility = View.GONE
    }

    fun navigateNotification(){
        under_top_menu.visibility = View.GONE
    }

    override fun passArtIdToComment(artId: String) {
        val urlComment= "$domainName/api/articles/$artId/comments"
        val bundle2 = Bundle()
        bundle2.putSerializable("urlcomment",urlComment)
        val transaction = this.supportFragmentManager.beginTransaction()
        val fragCommentsPostFragment = CommentFragment()
        fragCommentsPostFragment.arguments = bundle2
        transaction.replace(R.id.main_frame, fragCommentsPostFragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }
    override fun passArtToDetails(art: String) {
        Log.d(ContentValues.TAG, "dans passArt")
        val bundle2 = Bundle()
        bundle2.putSerializable("article",art)
        val transaction = this.supportFragmentManager.beginTransaction()
        val fragCommentsPostFragment = DetailsArtFragment()
        fragCommentsPostFragment.arguments = bundle2
        transaction.replace(R.id.main_frame, fragCommentsPostFragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    override fun passPostId(idPost: String) {
        val urlcomment= "$domainName/api/posts/$idPost/comments"
        val bundle2 = Bundle()
        bundle2.putSerializable("urlcomment",urlcomment)
        val transaction = this.supportFragmentManager.beginTransaction()
        val fragCommentsPostFragment = CommentFragment()
        fragCommentsPostFragment.arguments = bundle2
        transaction.replace(R.id.main_frame, fragCommentsPostFragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    override fun passVideoId(idVideo: String) {
        val urlcomment= "$domainName/api/videos/$idVideo/comments"
        val bundle2 = Bundle()
        bundle2.putSerializable("urlcomment",urlcomment)
        val transaction = this.supportFragmentManager.beginTransaction()
        val fragCommentsPostFragment = CommentFragment()
        fragCommentsPostFragment.arguments = bundle2
        transaction.replace(R.id.main_frame, fragCommentsPostFragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    

    fun tokenNotification(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token


                Log.d("hadahowatoken", token)
            })
    }

}


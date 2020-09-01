package com.example.corona.watch.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.content.CursorLoader
import androidx.room.Database
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.corona.watch.R
import com.example.corona.watch.authentication.SignInActivity
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.suspectedCase.FormSignalActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {
    val REQUEST_VIDEO_CAPTURE = 2
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Log.d("photourl", mUser?.photoUrl.toString())


        val options: RequestOptions = RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.account)
            .error(R.drawable.account)



        Glide.with(this).load(mUser?.photoUrl).apply(options)
            .into(img_profile_profile)

        username_profile.setText(mUser?.displayName)

        sign_out_layout.setOnClickListener {
            signOut()
        }

        linearLayout2.setOnClickListener {
            ActivityCompat.requestPermissions(this,Array<String>(1){ Manifest.permission.READ_EXTERNAL_STORAGE},
                1)
            dispatchTakeVideoIntent()
        }
        linearLayout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("page","videos")
            }
            startActivity(intent)
        }
    }
    private fun signOut() {

        // Firebase sign out
        FirebaseAuth.getInstance().signOut()

        //change Activity
        updateUI(null)

    }
    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {


        } else {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addVideo(){
        val intent = Intent(applicationContext,FormVideoUsersActivity::class.java)
        startActivity(intent)
    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent(this, FormVideoUsersActivity::class.java)

        var path : String = ""
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            intent.putExtra("path",getRealPathFromURIVideos(data!!.data!!))
            startActivity(intent)
        }
    }

    private fun getRealPathFromURIVideos(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        val loader = CursorLoader(applicationContext, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor!!.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
}
package com.example.corona.watch.suspectedCase

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.loader.content.CursorLoader
import com.example.corona.watch.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_signal.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SignalActivity : AppCompatActivity() {
    val REQUEST_VIDEO_CAPTURE = 2
    val REQUEST_SELECT_IMAGE_IN_ALBUM = 3
    val REQUEST_TAKE_GALLERY_VIDEO = 4
    lateinit var currentPhotoPath: String
    val REQUEST_IMAGE_CAPTURE = 1
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signal)


        this.
        signal_btn.setOnClickListener {
            ActivityCompat.requestPermissions(this,Array<String>(1){Manifest.permission.READ_EXTERNAL_STORAGE},
                    1)
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.type_file))
                .setNeutralButton(resources.getString(R.string.photo)){
                        dialog, which ->
                    MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.photo))
                    .setNeutralButton(resources.getString(R.string.take_camera)){
                            dialog, which -> dispatchTakePictureIntent()
                    }
                    .setPositiveButton(resources.getString(R.string.choose_gallery)) { dialog, which ->
                        // Respond to positive button press
                        selectImageInAlbum()
                    }
                    .show()
                }
                .setPositiveButton(resources.getString(R.string.video)) { dialog, which ->
                    // Respond to positive button press
                    MaterialAlertDialogBuilder(this)
                        .setTitle(resources.getString(R.string.video))
                        .setNeutralButton(resources.getString(R.string.take_camera)){
                                dialog, which -> dispatchTakeVideoIntent()
                        }
                        .setPositiveButton(resources.getString(R.string.choose_gallery)) { dialog, which ->
                            // Respond to positive button press
                            selectVideoInAlbum()
                        }
                        .show()
                }
                .show()
        }
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            takeVideoIntent.resolveActivity(packageManager)?.also {
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5)
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    fun selectVideoInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "video/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO)
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent(this, FormSignalActivity::class.java)

        var path : String = ""
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            intent.putExtra("path",getRealPathFromURIImages(data!!.data!!))
            intent.putExtra("type","IMAGE")
            startActivity(intent)
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            intent.putExtra("path",getRealPathFromURIVideos(data!!.data!!))
            intent.putExtra("type","VIDEO")
            startActivity(intent)
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            path = getRealPathFromURIImagesSaved(data!!.data!!)
            intent.putExtra("path",path)
            intent.putExtra("type","IMAGE")
            startActivity(intent)
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_GALLERY_VIDEO) {


            path = getRealPathFromURIVideoSaved(data!!.data!!)
            intent.putExtra("path",path)
            intent.putExtra("type","VIDEO")
            startActivity(intent)
        }
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
                    Toast.makeText(this@SignalActivity, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun getRealPathFromURIImagesSaved(contentURI: Uri): String {
        val wholeID = DocumentsContract.getDocumentId(contentURI)
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null
        )

        var filePath = ""

        val columnIndex = cursor!!.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }

        cursor.close()
        return filePath
    }

    private fun getRealPathFromURIVideoSaved(contentURI: Uri): String {
        val wholeID = DocumentsContract.getDocumentId(contentURI)
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val column = arrayOf(MediaStore.Video.Media.DATA)
        val sel = MediaStore.Video.Media._ID + "=?"
        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null
        )
        var filePath = ""
        val columnIndex = cursor!!.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }


    private fun getRealPathFromURIImages(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(applicationContext, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor!!.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
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

    fun getToken() : String? {
        var idToken : String? = ""
       // mUser.getIdToken(true).addOnSuccessListener {
            idToken = ""
        //}
        return idToken
    }


}

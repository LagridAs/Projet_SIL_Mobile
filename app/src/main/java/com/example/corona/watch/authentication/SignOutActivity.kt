package com.example.corona.watch.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.corona.watch.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*


class SignOutActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sign_out_btn.setOnClickListener { signOut() }
        id_user.setText(auth.currentUser?.email)



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
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}

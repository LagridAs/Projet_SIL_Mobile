package com.example.corona.watch.tools

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.corona.watch.R
import com.example.corona.watch.authentication.SignInActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed(
            {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.abc_popup_enter,
                    R.anim.abc_popup_exit
                )
            },
            750 // value in milliseconds
        )

    }
}

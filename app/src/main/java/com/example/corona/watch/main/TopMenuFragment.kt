package com.example.corona.watch.main


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.corona.watch.R
import com.example.corona.watch.authentication.SignInActivity
import com.example.corona.watch.notification.NotificationFragment
import com.example.corona.watch.profile.ProfileActivity
import com.example.corona.watch.suspectedCase.SignalActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_top_menu.*


class TopMenuFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile_btn.setOnClickListener { goProfile() }
        signal_btn_top.setOnClickListener { signalPageIntent() }
        notification_button.setOnClickListener {
            notificationPage()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_menu, container, false)
    }

    private fun signalPageIntent(){
        val intent = Intent(context,
            SignalActivity::class.java)
        startActivity(intent)
    }
    private fun notificationPage(){
        activity?.supportFragmentManager?.
        beginTransaction()?.
        replace(R.id.main_frame,NotificationFragment())?.commit()
        (activity as MainActivity).navigateNotification()
    }

    private fun goProfile(){
        val intent = Intent(context, ProfileActivity::class.java)
        startActivity(intent)
    }
    private fun signOut() {
        //(activity as MainActivity).db.NotificationDao().deleteAll()
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()

        //change Activity
        updateUI(null)

    }
    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {


        } else {
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
        }
    }

}

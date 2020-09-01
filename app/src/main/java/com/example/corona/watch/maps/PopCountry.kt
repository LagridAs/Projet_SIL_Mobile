package com.example.corona.watch.maps

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.corona.watch.R
import kotlinx.android.synthetic.main.activity_pop_country.*
import com.example.corona.watch.maps.json.Data
import com.google.gson.Gson


class PopCountry : Activity() {

    var gson : Gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_country)

        // les Paramètres d'affichage
        var dm : DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        var width:Int = dm.widthPixels
        var height:Int = dm.heightPixels
        window.setLayout((width*.7).toInt(),(height*.5).toInt())


        // Recevoir data passer par MapFragment
        val intent = intent
        val nameCountry : String? = intent.getStringExtra("nameCountry")
        name_country.setText(nameCountry)
        val data : Data? = gson.fromJson(intent.getStringExtra("data"),Data::class.java)


        // Remplir activity par data reçu
        fillInfo(data)



    }

    // La méthode pour remplir les différentes champs de l'activité
    fun fillInfo(data: Data?){
            confirmed_cases.setText(data?.nbConfirme?.toString())
            active_cases.setText(data?.nbPorteurs?.toString())
            recovered_cases.setText(data?.nbRetablie?.toString())
            dead_cases.setText(data?.nbDeces?.toString())
            unconfirmed_cases.setText(data?.nbSuspect?.toString())

    }
}

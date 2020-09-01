package com.example.corona.watch.maps

import com.example.corona.watch.maps.Centre

data class InfoProvince (val nbrPorteur:Int,
                         val nbrconfirme:Int,
                         val nbrDeces:Int,
                         val nbrgueris:Int,
                         val nbrsuspects:Int,
                         val centres: ArrayList<Centre>?
)
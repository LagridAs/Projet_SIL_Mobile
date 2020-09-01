package com.example.corona.watch.maps

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import androidx.test.rule.ActivityTestRule
import com.example.corona.watch.maps.json.Data
import com.google.gson.Gson
import androidx.test.platform.app.InstrumentationRegistry
import com.example.corona.watch.R


@RunWith(AndroidJUnit4ClassRunner::class)
class PopCountryClassTest {

    @Rule
    @JvmField
    var activityActivityTestRule = object : ActivityTestRule<PopCountry>(
        PopCountry::class.java){
        override fun getActivityIntent(): Intent {
            val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
            val intent = Intent(targetContext, PopCountry::class.java)
            var dzCountryData = Data(nbSuspect = 1200
                ,nbConfirme = 8400
                ,nbDeces = 1000
                ,nbPorteurs = 5400
                ,nbRetablie = 2000)
            var gson = Gson()
            intent.putExtra("nameCountry", "Algeria")
            intent.putExtra("data", gson.toJson(dzCountryData))
            return intent
        }
    }
    @Before
    fun init() {

    }

    @Test
    fun testing(){
        Espresso.onView(ViewMatchers.withId(R.id.confirmed_cases)).check(ViewAssertions.matches(ViewMatchers.withText("8400")))
        Espresso.onView(ViewMatchers.withId(R.id.active_cases)).check(ViewAssertions.matches(ViewMatchers.withText("5400")))
        Espresso.onView(ViewMatchers.withId(R.id.dead_cases)).check(ViewAssertions.matches(ViewMatchers.withText("1000")))
        Espresso.onView(ViewMatchers.withId(R.id.recovered_cases)).check(ViewAssertions.matches(ViewMatchers.withText("2000")))
        Espresso.onView(ViewMatchers.withId(R.id.unconfirmed_cases)).check(ViewAssertions.matches(ViewMatchers.withText("1200")))

    }

}
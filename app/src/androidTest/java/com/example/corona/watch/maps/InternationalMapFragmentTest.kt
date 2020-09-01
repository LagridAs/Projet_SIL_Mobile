package com.example.corona.watch.maps

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.example.corona.watch.R
import com.example.corona.watch.main.MainActivity
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InternationalMapFragmentTest {
/*
    @Rule
    @JvmField
    var mActivityTestRule : ActivityTestRule<MainActivity>? = ActivityTestRule(MainActivity::class.java)
    lateinit var fragment: InternationalMapFragment

    @Before
    fun setUp() {
        fragment = mActivityTestRule!!.activity
            .supportFragmentManager.findFragmentById(R.id.main_frame) as InternationalMapFragment
    }

    @Test
    fun getCountryNameTest() {
        assertEquals("الجزائر",fragment.getCountryName(LatLng(28.0339,1.6596)))
        assertEquals("Algeria",fragment.getCountryNameEnglish(LatLng(28.0339,1.6596)))
    }



    @Test
    fun jsonParseTest(){
        fragment.jsonParse("https://my-json-server.typicode.com/klugBoy/fakecoronawatchapi/international",
            null)
        while (fragment.dataCountries.isEmpty()){
            Thread.sleep(3000)
        }
        val dataset = fragment.dataCountries
        assertEquals(3,dataset.size)
        assertEquals("Algeria",dataset[0].country)
        assertEquals("Germany",dataset[1].country)
        assertEquals("United Kingdom",dataset[2].country)

    }

    @Test
    fun getDataCountryTest() {
        fragment.jsonParse("https://my-json-server.typicode.com/klugBoy/fakecoronawatchapi/international",
            null)
        while (fragment.dataCountries.isEmpty()){
            Thread.sleep(3000)
        }
        val data_germany = fragment.getDataCountry("Germany")
        assertEquals(650,data_germany?.nbPorteurs)
        assertEquals(50,data_germany?.nbSuspect)
        assertEquals(1117,data_germany?.nbConfirme)
        assertEquals(105,data_germany?.nbDeces)
        assertEquals(62,data_germany?.nbRetablie)
    }

    @Test
    fun infectedCountriesTest()  {
        fragment.jsonParse("https://my-json-server.typicode.com/klugBoy/fakecoronawatchapi/international",
            null)
        while (fragment.dataCountries.isEmpty()){
            Thread.sleep(3000)
        }
        val liste_infected = fragment.infectedCountries()
        assertEquals("Algeria",liste_infected[0])
        assertEquals("Germany",liste_infected[1])
        assertEquals("United Kingdom",liste_infected[2])
    }




    @Test
    fun onClickCountryTest()  {
        fragment.jsonParse(
            "https://my-json-server.typicode.com/klugBoy/fakecoronawatchapi/international",
            null)
        while (fragment.dataCountries.isEmpty()){
            Thread.sleep(3000)
        }
        fragment.onClickCountry(LatLng(28.0339,1.6596))
        onView(withId(R.id.name_country)).check(matches(withText("الجزائر")))
        onView(withId(R.id.confirmed_cases)).check(matches(withText("1117")))
        onView(withId(R.id.active_cases)).check(matches(withText("650")))
        onView(withId(R.id.dead_cases)).check(matches(withText("105")))
        onView(withId(R.id.recovered_cases)).check(matches(withText("62")))
        onView(withId(R.id.unconfirmed_cases)).check(matches(withText("50")))
        Thread.sleep(3000)
    }
*/}
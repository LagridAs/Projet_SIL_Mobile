package com.example.corona.watch.main


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.corona.watch.*
import com.example.corona.watch.articles.ArticleFragment
import com.example.corona.watch.healthcare.HealthcareFragment
import com.example.corona.watch.maps.InternationalMapFragment
import com.example.corona.watch.socialMedia.PostFragment
import com.example.corona.watch.videos.VideoFragment
import kotlinx.android.synthetic.main.fragment_main_menu.*

class BottomMenuFragment : Fragment() {
    var sp : SharedPreferences? =  null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences("notification", Context.MODE_PRIVATE)
        var main_fragment :Fragment =
            BlankFragment()
        var above_fragment : Fragment =
            BlankFragment()
        updateView()
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.maps -> {main_fragment =
                    InternationalMapFragment()
                    above_fragment =
                        UnderMenuFragment()
                    (activity as MainActivity).navigateMap()

                }
                R.id.articles -> {
                    above_fragment =
                        BlankFragment()
                    main_fragment = ArticleFragment()
                    (activity as MainActivity).navigateArticles()
                }
                R.id.social_media -> {
                    above_fragment =
                        BlankFragment()
                    main_fragment= PostFragment()
                    (activity as MainActivity).navigateHealthCare()
                }
                R.id.videos -> {
                    above_fragment =
                        BlankFragment()
                    main_fragment = VideoFragment()
                    (activity as MainActivity).navigateHealthCare()

                }

                R.id.healthcare -> {
                    above_fragment =
                        BlankFragment()
                    main_fragment = HealthcareFragment()
                    (activity as MainActivity).navigateHealthCare()

                }

                else -> { main_fragment =
                    BlankFragment()
                    above_fragment =
                        BlankFragment()

                }

            }
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.under_top_menu,above_fragment)?.commit()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_frame,main_fragment)?.commit()
            return@setOnNavigationItemSelectedListener true
        }
        val item = bottom_navigation.menu.findItem(R.id.maps)
        item.isCheckable = true
        item.isChecked = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }


    fun updateView(){
        updateMapUI()
        updateArticleUI()
    }

    fun updateMapUI(){
        var badge = bottom_navigation.getOrCreateBadge(R.id.maps)
        badge.isVisible = sp!!.getBoolean("map",false)

    }

    fun updateArticleUI(){
        var badge = bottom_navigation.getOrCreateBadge(R.id.articles)
        var nm = sp!!.getInt("article",0)
        if(nm==0){
            badge.isVisible = false
        } else {
            badge.isVisible = true
            badge.number = nm
        }


    }

    fun restoreMapUI(){
        var badge = bottom_navigation.getOrCreateBadge(R.id.maps)
        badge.isVisible = false
        var editor = sp!!.edit()
        editor.putBoolean("map", false)
        editor.commit()
    }

    fun restoreArticleUI(){
        var badge = bottom_navigation.getOrCreateBadge(R.id.articles)
        badge.isVisible = false
        badge.number = 0
        var editor = sp!!.edit()
        editor.putInt("article", 0)
        editor.commit()
    }

    fun clickVideos(){
        val item = bottom_navigation.menu.findItem(R.id.videos)
        bottom_navigation.selectedItemId = R.id.videos
        item.isCheckable = true
        item.isChecked = true
    }


}

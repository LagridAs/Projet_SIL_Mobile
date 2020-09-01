package com.example.corona.watch.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.corona.watch.maps.NationalMapFragment
import com.example.corona.watch.R
import com.example.corona.watch.maps.InternationalMapFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_under_menu.*

class UnderMenuFragment : Fragment()  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabs.selectTab(tabs.getTabAt(1))
        var main_fragment : Fragment
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(tab == tabs.getTabAt(1)) {
                    main_fragment = InternationalMapFragment()
                } else {
                    main_fragment =
                        NationalMapFragment()
                }
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_frame,main_fragment)?.commit()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_under_menu, container, false)



    }


}

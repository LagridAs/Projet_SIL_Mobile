package com.example.corona.watch.videos

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.articles.BaseFragment
import com.example.corona.watch.articles.Communicator
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.tools.LoadingDialog
import com.example.corona.watch.videos.jsonData.Video
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException


class VideoFragment : BaseFragment() {
    lateinit var comm: Communicator
    lateinit var loadingDialog : LoadingDialog
    //var urlVideos : String = "https://6365c919cb53.ngrok.io/api/videos"
    var urlVideos : String = "/api/videos/"
    lateinit var mRequestQueue : RequestQueue
    var dataVideo  : MutableList<Video> = mutableListOf<Video>()
    lateinit var videoAdapter: AdapterVideo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        urlVideos = (activity as MainActivity).domainName + urlVideos
        loadingDialog = LoadingDialog(requireActivity())
        videoAdapter= AdapterVideo(dataVideo)
        loadingDialog.startLoadingDialog()
        launch {
            try {

                mRequestQueue = Volley.newRequestQueue(context)


                jsonParse(object : VolleyCallBack {
                    override fun onSuccess() {
                        try {
                            loadVideos()
                        }catch (e:Exception){}
                    }
                })


            } catch (uriSyntaxException: URISyntaxException) {
                uriSyntaxException.printStackTrace()
            }
        }
        loadingDialog.dismissDialog()

    }

    fun loadVideos(){
        videoAdapter= AdapterVideo(dataVideo)
        Log.d(TAG,"dakhalll loadVideo : " + dataVideo.size)
        recycler_video_view.apply {
            adapter= videoAdapter
            layoutManager= LinearLayoutManager(activity)
        }

        comm = activity as Communicator
        videoAdapter.onItemClick = { videoId ->
            comm.passVideoId(videoId)
        }
    }

    fun jsonParse(callback: VolleyCallBack?)  {
        val mJsonObjectRequest = object : StringRequest(
            Method.GET,urlVideos,
            Response.Listener { response ->

                var jsonArray= JSONArray(response)
                var gson= Gson()
                var jsonObject= JSONObject()


                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    Log.d(TAG,"dakhalll boucle json  : " + gson.fromJson(jsonObject.toString(), Video::class.java))
                    dataVideo.add(gson.fromJson(jsonObject.toString(), Video::class.java))
                }

                callback?.onSuccess()

                //it : JSON Object
            }, Response.ErrorListener {
                it.printStackTrace()
            }) {}
        mJsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        mRequestQueue.add(mJsonObjectRequest)
    }




    companion object {

        fun newInstance(): VideoFragment {
            return VideoFragment()
        }

    }
    interface VolleyCallBack {
        fun onSuccess()
    }


}

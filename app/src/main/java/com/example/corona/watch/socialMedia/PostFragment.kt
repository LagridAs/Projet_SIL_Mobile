package com.example.corona.watch.socialMedia

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
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
import com.example.corona.watch.socialMedia.jsonData.Facebook
import com.example.corona.watch.socialMedia.jsonData.Website
import com.example.corona.watch.socialMedia.jsonData.Youtube
import com.example.corona.watch.tools.LoadingDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_details_art.*
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URISyntaxException


class PostFragment : BaseFragment() {
    private var dataPost: MutableList<Any> = mutableListOf()
    lateinit var comm : Communicator
    lateinit var loadingDialog : LoadingDialog
    var urlpost : String = "/api/posts/"
    //var urlpost =""
    lateinit var mRequestQueue : RequestQueue
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())

        urlpost = (activity as MainActivity).domainName + urlpost
        //urlpost ="https://6365c919cb53.ngrok.io/api/posts"
        postAdapter= PostAdapter(dataPost)
        comm= activity as Communicator
        loadingDialog.startLoadingDialog()
        launch {
            try {

                mRequestQueue = Volley.newRequestQueue(context)
                Log.d(TAG, "9balllll jsonParse")

                jsonParse(object : VolleyCallBack {
                    override fun onSuccess() {
                        try {
                            loadAPosts()
                        }catch (e:Exception){

                        }


                    }
                })

            } catch (uriSyntaxException: URISyntaxException) {
                uriSyntaxException.printStackTrace()
            }
        }
        loadingDialog.dismissDialog()

    }
    fun loadAPosts(){
        Log.d(TAG , "dakhal loadPosts" )
        Log.d(TAG , "les elements de dataPost : " )
        for (item in dataPost ){
            Log.d(TAG , item.toString() )
        }
        postAdapter= PostAdapter(dataPost)
        recycler_post_view.apply {
            adapter= postAdapter
            layoutManager= LinearLayoutManager(activity)
        }
        postAdapter.onItemClick = { urlSite ->
            val url = urlSite as String
            val viewIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse(url)
            )
            startActivity(viewIntent)
        }
        postAdapter.onItemClick = { obj ->
            when (obj) {
                is Youtube -> comm.passPostId(obj.id)
                is Facebook -> comm.passPostId(obj.id)
                is Website -> comm.passPostId(obj.id)
                is String -> {
                    val viewIntent = Intent(
                        "android.intent.action.VIEW",
                        Uri.parse(obj)
                    )
                    startActivity(viewIntent)

                }
            }
        }
    }

    private fun jsonParse(callback: VolleyCallBack)  {
        val mJsonObjectRequest = object : StringRequest(
            Method.GET,urlpost,
            Response.Listener { response ->

                Log.d(TAG , "dakhal jsonParse" )

                val jsonArray= JSONArray(response)
                val gson= Gson()
                var jsonObject= JSONObject()
                var detail= JSONObject()

                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    detail = jsonObject.getJSONObject("detail")
                    Log.d(TAG , "hada type : " + detail.getString("type") )

                    when(detail.getString("type")){
                        "WEBSITE" -> {
                            Log.d(TAG , "WEBSITE" )
                            dataPost.add(gson.fromJson(jsonObject.toString(), Website::class.java))
                        }
                        "YOUTUBE" -> {
                            Log.d(TAG, "YOUTUBE")
                            dataPost.add(gson.fromJson(jsonObject.toString(), Youtube::class.java))

                        }
                        "FACEBOOK" -> {
                            Log.d(TAG , "FACEBOOK")
                            dataPost.add(gson.fromJson(jsonObject.toString(), Facebook::class.java))
                        }
                    }
                    Log.d(TAG , dataPost.size.toString() )
                }


                callback.onSuccess()


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
        Log.d(TAG, "fin json parse")

    }

    companion object {
        fun newInstance(): PostFragment {
            return PostFragment()

        }
    }
    interface VolleyCallBack {
        fun onSuccess()
    }
}

package com.example.corona.watch.articles

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.R
import com.example.corona.watch.articles.json.DetailArt
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.tools.LoadingDialog
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details_art.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException

class DetailsArtFragment : BaseFragment() {
    var v:View?= null
    lateinit var loadingDialog : LoadingDialog
    lateinit var comm : Communicator
    lateinit var mRequestQueue : RequestQueue
    var urlArt=""
    var article: DetailArt = DetailArt(null,null,null,null,null,null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        v=inflater.inflate(R.layout.fragment_details_art, container, false)
        return v
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())
        mRequestQueue = Volley.newRequestQueue(context)
        comm= activity as Communicator
        val id = arguments?.getSerializable("article") as String

        urlArt = (activity as MainActivity).domainName+"/api/articles/$id"
        //urlArt = "https://6365c919cb53.ngrok.io/api/articles/$id"

        launch {   try {


            jsonParse(object : ArticleFragment.VolleyCallBack {
                override fun onSuccess() {
                    loadingDialog.startLoadingDialog()
                    try {
                        initDetails(article)
                        cmtButton?.setOnClickListener {
                            comm.passArtIdToComment(id)
                        }
                    }catch (e:Exception){}
                    loadingDialog.dismissDialog()
                }
            })

        } catch (uriSyntaxException: URISyntaxException) {
            uriSyntaxException.printStackTrace()
        }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initDetails(article:DetailArt){
        moharir.text = v?.resources?.getString(R.string.auteur)
        virgule.setImageResource(R.drawable.ic_quotationyell)
        picuserImage.setImageResource(R.drawable.user)
        redText?.text =article.author!!.name
        dateText?.text =article.date
        contenuText?.settings?.javaScriptEnabled=true
        contenuText?.settings?.loadWithOverviewMode=true
        contenuText?.settings?.builtInZoomControls=true
        contenuText?.settings?.loadsImagesAutomatically=true
        val html = article.contenu
        contenuText?.webViewClient=object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        contenuText?.loadDataWithBaseURL(null,html, "text/html", "utf-8",null)
        Picasso.get()
            .load(article.cover)
            .into(cov, object : Callback {
                override fun onSuccess() {
                    Log.d(ContentValues.TAG, "success")
                }
                override fun onError(e: Exception?) {
                    Log.d(ContentValues.TAG, "error")
                }
            })
        titreText?.text =article.title
    }
     fun jsonParse(callback: ArticleFragment.VolleyCallBack?)  {
        val mJsonObjectRequest = object : StringRequest(
            Method.GET,urlArt,
            Response.Listener { response ->
                var jsonObject= JSONObject(response)
                var gson= Gson()
                //var json= jsonObject.getJSONObject()
                article= gson.fromJson(jsonObject.toString(), DetailArt::class.java)
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
        fun newInstance(): DetailsArtFragment {
            return DetailsArtFragment()
        }
    }

}
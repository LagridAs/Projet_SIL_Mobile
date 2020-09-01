package com.example.corona.watch.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.corona.watch.*
import com.example.corona.watch.articles.json.Article
import com.example.corona.watch.main.MainActivity
import com.example.corona.watch.tools.LoadingDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException

class ArticleFragment : BaseFragment() {
    lateinit var comm: Communicator
    lateinit var loadingDialog : LoadingDialog

    var urlartcile : String = "/api/articles"
    //var urlartcile = ""
    lateinit var mRequestQueue : RequestQueue
    var dataarticle  : MutableList<Article> = mutableListOf<Article>()
    lateinit var artAdapter: AdapterArticle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())

        urlartcile = (activity as MainActivity).domainName+urlartcile
        //urlartcile= "https://6365c919cb53.ngrok.io/api/articles"

        artAdapter= AdapterArticle(dataarticle)

        launch {   try {

                mRequestQueue = Volley.newRequestQueue(context)


                jsonParse(object : VolleyCallBack {
                    override fun onSuccess() {
                        loadingDialog.startLoadingDialog()
                        try {

                            loadArticles()
                            (activity as MainActivity).restoreArticleNotification()

                        }catch (e:Exception){}
                        loadingDialog.dismissDialog()
                    }
                })


            } catch (uriSyntaxException: URISyntaxException) {
                uriSyntaxException.printStackTrace()
            }
          }

    }

    fun loadArticles(){
        artAdapter= AdapterArticle(dataarticle)
        recycler_articles_view.apply {
            adapter= artAdapter
            layoutManager= LinearLayoutManager(activity)
        }

        comm = activity as Communicator
        artAdapter.onItemClick = { articleId ->
            comm.passArtToDetails(articleId)
        }
    }

     fun jsonParse(callback: VolleyCallBack?)  {
        val mJsonObjectRequest = object : StringRequest(
            Method.GET,urlartcile,
            Response.Listener { response ->

                var jsonArray= JSONArray(response)
                var gson= Gson()
                var jsonObject= JSONObject()


                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    dataarticle.add(gson.fromJson(jsonObject.toString(), Article::class.java))
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

        fun newInstance(): ArticleFragment {
            return ArticleFragment()
        }

    }
    interface VolleyCallBack {
        fun onSuccess()
    }

}

package com.example.corona.watch.articles

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.articletest.jsonData.*
import com.example.corona.watch.R
import com.example.corona.watch.articles.json.Comment
import com.example.corona.watch.articles.json.Detail
import com.example.corona.watch.articles.json.User
import com.example.corona.watch.tools.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class CommentFragment : BaseFragment() {
    var urlcomment : String = ""
    lateinit var loadingDialog : LoadingDialog
    lateinit var mRequestQueue : RequestQueue
    var dataComment  : MutableList<Comment> = mutableListOf()
    var adaptercomment: AdapterComment = AdapterComment(dataComment)
    var mUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var token_ : String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadingDialog = LoadingDialog(requireActivity())

        getToken()
        urlcomment=arguments?.getString("urlcomment") as String

        launch {
            try {

                mRequestQueue = Volley.newRequestQueue(context)

                jsonParse(object : VolleyCallBack {
                    override fun onSuccess() {
                        loadingDialog.startLoadingDialog()
                        try{
                            loadComments()
                        }catch (e:Exception){}

                        loadingDialog.dismissDialog()
                    }
                })


            } catch (uriSyntaxException: URISyntaxException) {
                uriSyntaxException.printStackTrace()
            }
        }
        editCommentLayout.setStartIconOnClickListener {
            val comText:String =editComment.text.toString()
            val user = getUser()
            val sdf = SimpleDateFormat("HH:mm")
            val sdf_day = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = Date()
            val currentDateFormatted = sdf.format(currentDate)
            Log.d("time",sdf.format(currentDate))
            val comment= CommentToadd(comText,
                sdf_day.format(currentDate), sdf.format(currentDate),user)
            val detail= Detail(sdf_day.format(currentDate),sdf.format(currentDate),user,comText)
            val c = Comment(detail,"")
            dataComment.add(c)
            adaptercomment.updateData()
            editComment.text = Editable.Factory.getInstance().newEditable("")
            JsonAdd(comment)

        }
    }

    fun loadComments(){
        adaptercomment = AdapterComment(dataComment)
        commentRecycler.apply {
            adapter = adaptercomment
            layoutManager = LinearLayoutManager(activity)
        }

    }


    private fun JsonAdd(Cmnt:CommentToadd){
        val com= JSONObject()
        com.put("contenu",Cmnt.contenu)
        com.put("date",Cmnt.date)
        com.put("heure",Cmnt.heure)

        val user= JSONObject()
        user.put("id",Cmnt.user.id)
        user.put("name",Cmnt.user.name)

        com.put("user",user)
        Log.d("heureee",Cmnt.heure)

        // Volley post request with parameters
        val request = object : JsonObjectRequest(
            Request.Method.POST,urlcomment,com,
            Response.Listener { response ->
                // Process the json
                try {
                    Log.d(ContentValues.TAG,"Response: $response")
                }catch (e:Exception){
                    Log.d(ContentValues.TAG,"Exception: $e")
                }

            }, Response.ErrorListener{
                // Error in request
                it.printStackTrace()
            })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    var params : MutableMap<String, String> = mutableMapOf()
                    params["Content-Type"] = "application/json"
                    params["Authorization"] = "Bearer $token_"
                    return params
                }
            }

        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        mRequestQueue.add(request)
    }

    private fun getUser(): User {
        return User(mUser!!.displayName,mUser!!.uid)

    }
    fun jsonParse(callback: VolleyCallBack?)  {
        val mJsonObjectRequest = object : StringRequest(
            Method.GET,urlcomment,
            Response.Listener { response ->

                var jsonArray= JSONArray(response)
                var gson= Gson()
                var jsonObject= JSONObject()


                for (i in 0 until jsonArray.length()) {
                    jsonObject = jsonArray.getJSONObject(i)
                    dataComment.add(gson.fromJson(jsonObject.toString(), Comment::class.java))
                }
                Log.d("comments",response)
                Log.d("comments",dataComment.toString())

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
    private fun getToken() : String? {

        var token : String? = ""

        mUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val idToken: String? = it.result?.token
                Log.d("Real token",idToken)
                token_ = idToken
            } else {
                // Handle error -> task.getException();
            }
        }
        return token
    }

    companion object {
        fun newInstance(): CommentFragment {
            return CommentFragment()
        }

    }
    interface VolleyCallBack {
        fun onSuccess()
    }

    /***********************Adapter**********************/
    inner class AdapterComment(private val dataList:MutableList<Comment>): RecyclerView.Adapter<AdapterComment.HolderComment>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
            val inflater = LayoutInflater.from(parent.context)
            return HolderComment(inflater, parent)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: HolderComment, position: Int) {
            val data = dataList[position]
            holder.bind(data)
        }
        fun updateData(){
            adaptercomment!!.notifyDataSetChanged()
        }



        /***********************HolderView**********************/

        inner class HolderComment(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(
                inflater.inflate(
                    R.layout.comment_item_view,
                    parent, false
                )
            ) {

            private var userView: TextView? = null
            private var dateView: TextView? = null
            private var commentView: TextView? = null

            init {
                userView = itemView.findViewById(R.id.username)
                dateView = itemView.findViewById(R.id.dateComm)
                commentView = itemView.findViewById(R.id.commentContent)
            }

            @SuppressLint("SetTextI18n")
            fun bind(cmt:Comment) {
                Log.d("comment",cmt.toString())
                userView?.text = cmt.detail.user!!.name
                dateView?.text = cmt.detail.date + " " + cmt.detail.heure.toString()
                commentView?.text = cmt.detail.contenu
            }

        }
    }
}

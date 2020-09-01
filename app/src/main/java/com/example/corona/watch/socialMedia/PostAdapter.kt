package com.example.corona.watch.socialMedia

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.corona.watch.R
import com.example.corona.watch.socialMedia.jsonData.Facebook
import com.example.corona.watch.socialMedia.jsonData.Website
import com.example.corona.watch.socialMedia.jsonData.Youtube
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.regex.Matcher
import java.util.regex.Pattern


/********************Adapter**********************/

class PostAdapter(private val DataList:MutableList<Any>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((Any) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var holder: RecyclerView.ViewHolder=  YoutubeHolder(inflater, parent)
        when (viewType) {
            1 -> holder = YoutubeHolder(inflater, parent)
            2 -> holder = FacebookHolder(inflater, parent)
            3 -> holder = WebSiteHolder(inflater, parent)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return DataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = DataList[position]
        when (getItemViewType(position)) {
            1 -> {holder as YoutubeHolder
                data as Youtube
                holder.bind(data)
            }
            2 ->{holder as FacebookHolder
                data as Facebook
                holder.bind(data)
            }
            3 ->{holder as WebSiteHolder
                data as Website
                holder.bind(data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var type : Int = 0
        when (DataList[position]) {
            is Youtube -> type = 1
            is Facebook -> type = 2
            is Website -> type = 3
        }
        return type
    }

    /***********************HolderView**********************/

    inner class YoutubeHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.youtube_item_view,
                parent, false
            )
        ) {
        private var videoView: YouTubePlayerView? = null
        private var cmtBtnYt: ImageView? = null
        private var dateYt:TextView?= null


        init {
            videoView = itemView.findViewById(R.id.video)
            cmtBtnYt = itemView.findViewById(R.id.cmdBtnYt)
            dateYt = itemView.findViewById(R.id.dateYt)
        }

        fun bind(utb: Youtube) {
            dateYt?.text = utb.detail?.date
            videoView!!.getPlayerUiController().showFullscreenButton(true)
            videoView!!.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    val videoId = getId(utb.detail!!.src)
                    Log.d(ContentValues.TAG,videoId)
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            })

           cmtBtnYt?.setOnClickListener {
                Log.d(ContentValues.TAG,"dakhal on click")
                onItemClick?.invoke(utb)
            }
        }
        private fun getId(url:String):String{
            val pattern =
                "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
            var ubId=""
            val compiledPattern: Pattern = Pattern.compile(pattern)
            val matcher: Matcher =
                compiledPattern.matcher(url) //url is youtube url for which you want to extract the id.

            if (matcher.find()) {
                ubId= matcher.group()
            }
            return ubId
        }

    }
    inner class FacebookHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.facebook_item_view,
                parent, false
            )
        ) {
        private var fbView: WebView? = null
        private var cmtBtnFb: ImageView? = null

        init {
            fbView = itemView.findViewById(R.id.fcb)
            cmtBtnFb = itemView.findViewById(R.id.cmdBtnFb)

        }

        @SuppressLint("SetJavaScriptEnabled")
        fun bind(facebook: Facebook) {
            fbView?.settings?.javaScriptEnabled=true
            fbView?.settings?.loadWithOverviewMode=true
            fbView?.settings?.builtInZoomControls=true
            fbView?.settings?.loadsImagesAutomatically=true
            fbView?.settings?.javaScriptCanOpenWindowsAutomatically = true
            val html = "https://www.facebook.com/plugins/post.php?href="+java.net.URLEncoder.encode(facebook.detail?.src, "utf-8")
            Log.d(ContentValues.TAG, html)
            fbView?.webViewClient=object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url)
                    return true
                }
            }
            fbView?.loadUrl(html)
            cmtBtnFb?.setOnClickListener {
                Log.d(ContentValues.TAG,"dakhal on click")
                onItemClick?.invoke(facebook)
            }

        }

    }
    inner class WebSiteHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.website_item_view,
                parent, false
            )
        ) {
        private var cover: ImageView?= null
        private var titre: TextView?=null
        private var description : TextView?=null
        private var name:TextView?=null
        private var btn:ImageView?= null
        private var cmtBtnWs:ImageView?= null
        private var dateWt:TextView?= null

        init {
            cover = itemView.findViewById(R.id.coverWebsite)
            titre = itemView.findViewById(R.id.titreWebsite)
            description = itemView.findViewById(R.id.descWebsite)
            name = itemView.findViewById(R.id.nameWebsite)
            dateWt = itemView.findViewById(R.id.dateWt)
            btn = itemView.findViewById(R.id.passBtn)
            cmtBtnWs = itemView.findViewById(R.id.cmtBtnWs)
        }

        fun bind(website: Website) {
            Picasso.get()
                .load(website.detail.contenu.cover)
                .into(cover, object : Callback {
                    override fun onSuccess() {
                        Log.d(ContentValues.TAG, "success")
                    }
                    override fun onError(e: Exception?) {
                        Log.d(ContentValues.TAG, "error")
                    }
                })
            titre?.text = website.detail.contenu.title
            description?.text = website.detail.contenu.description
            name?.text = website.detail.contenu.siteName
            dateWt?.text = website.detail.date

            btn?.setOnClickListener {
                onItemClick?.invoke(website.detail.contenu.url)
            }
            cmtBtnWs?.setOnClickListener{
                onItemClick?.invoke(website)
            }

        }
    }
}
package com.example.corona.watch.articles

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.corona.watch.R
import com.example.corona.watch.articles.json.Article
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


/***********************Adapter**********************/
class AdapterArticle(private val dataList:MutableList<Article>): RecyclerView.Adapter<AdapterArticle.HolderArticle>() {
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderArticle {
        val inflater = LayoutInflater.from(parent.context)
        return HolderArticle(inflater, parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HolderArticle, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }



    /***********************HolderView**********************/

    inner class HolderArticle(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.item_article_view,
                parent, false
            )
        ) {

        private var dateView: TextView? = null
        private var contenuView: TextView? = null
        private var picView: ImageView? = null
        private var titreView: TextView? = null
        private var ReadMoreBtn: TextView? = null
        private var authorView: TextView?= null


        init {
            dateView = itemView.findViewById(R.id.dateArt)
            contenuView = itemView.findViewById(R.id.contenuArt)
            picView = itemView.findViewById(R.id.picArt)
            titreView = itemView.findViewById(R.id.titreArt)
            authorView = itemView.findViewById(R.id.authorArt)

        }

        fun bind(article: Article) {
            dateView?.text = article.detailArt.date
            contenuView?.text = article.detailArt.description

            Picasso.get()
                .load(article.detailArt.cover)
                .into(picView, object : Callback {
                    override fun onSuccess() {
                        Log.d(ContentValues.TAG, "success")
                    }

                    override fun onError(e: Exception?) {
                        Log.d(ContentValues.TAG, "error")
                    }
                })
            //picView?.clipToOutline=true
            titreView?.text = article.detailArt.title
            authorView?.text = article.detailArt.author?.name


            itemView.setOnClickListener {
                article.id.let { it1 -> onItemClick?.invoke(it1) }
            }
        }

    }
}

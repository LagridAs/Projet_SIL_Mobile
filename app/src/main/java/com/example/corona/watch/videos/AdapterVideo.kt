package com.example.corona.watch.videos

import android.content.ComponentName
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.corona.watch.R
import com.example.corona.watch.videos.jsonData.Video
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayerFactory.newSimpleInstance
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_form_video_users.*

/***********************Adapter**********************/
    class AdapterVideo(private val dataList:MutableList<Video>): RecyclerView.Adapter<AdapterVideo.HolderVideo>() {
        var onItemClick: ((String) -> Unit)? = null
        private lateinit var contxt: Context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderVideo {
            val inflater = LayoutInflater.from(parent.context)
            contxt= parent.context
            return HolderVideo(inflater, parent)
        }

        override fun getItemCount(): Int {
            Log.d(TAG,"dakhalll adapter : " + dataList.size)
            return dataList.size
        }

        override fun onBindViewHolder(holder: HolderVideo, position: Int) {
            val data = dataList[position]
            holder.bind(data)
        }




    /*override fun onViewAttachedToWindow(holder: HolderVideo) {
        super.onViewAttachedToWindow(holder)
        holder.playVideo()
    }

    override fun onViewDetachedFromWindow(holder: HolderVideo) {
        super.onViewDetachedFromWindow(holder)
        holder.pauseVideo()
    }*/

        /***********************HolderView**********************/

        inner class HolderVideo(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(
                inflater.inflate(
                    R.layout.item_video_view,
                    parent, false
                )
            ) {

            private var exoPlayer: ExoPlayer?= null
            private var videoView:PlayerView?= null
            private var cmtBtVd:ImageView?= null
            private var titlevideo:TextView?= null
            private var dateVd:TextView?= null

            private var playbackStateBuilder : PlaybackStateCompat.Builder? = null
            private var mediaSession: MediaSessionCompat? = null

            init {
                videoView = itemView.findViewById(R.id.videoView)
                cmtBtVd = itemView.findViewById(R.id.CmdBtnVd)
                titlevideo = itemView.findViewById(R.id.videoTitle)
                dateVd =itemView.findViewById(R.id.dateVideo)

            }

            fun bind(video: Video) {
                titlevideo?.text = video.detail.title
                dateVd?.text = video.detail.date
                //ExoPlayer
                val trackSelector = DefaultTrackSelector()
                exoPlayer = ExoPlayerFactory.newSimpleInstance(contxt, trackSelector)
                videoView?.player = exoPlayer

                val userAgent = Util.getUserAgent(contxt, "videosInternautes")
                val mediaUri = Uri.parse(video.detail.src)
                val mediaSource = ExtractorMediaSource(mediaUri, DefaultDataSourceFactory(contxt, userAgent), DefaultExtractorsFactory(), null, null)

                exoPlayer?.prepare(mediaSource)

                val componentName = ComponentName(contxt, "videosInternautes")
                mediaSession = MediaSessionCompat(contxt, "ExoPlayer", componentName, null)

                playbackStateBuilder = PlaybackStateCompat.Builder()

                playbackStateBuilder?.setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_FAST_FORWARD)

                mediaSession?.setPlaybackState(playbackStateBuilder?.build())
                mediaSession?.isActive = true
               //ExoPlayer


                cmtBtVd?.setOnClickListener {
                Log.d(ContentValues.TAG,"dakhal on click")
                onItemClick?.invoke(video.id)
                }
            }

            private fun initializePlayer() {

            }


        }

    }


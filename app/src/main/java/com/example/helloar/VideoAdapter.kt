package com.example.helloar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.helloar.databinding.ListVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class VideoAdapter(
    private val context: Context,
    private val videos: ArrayList<Video>,
    private val videoPreparedListener: OnVideoPreparedListener
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(
        val binding: ListVideoBinding,
        private val context: Context,
        private val videoPreparedListener: OnVideoPreparedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var exoPlayer: ExoPlayer
        private lateinit var mediaSource: MediaSource
        private var isFavorite: Boolean = false
        private var isPlaying: Boolean = false

        init {
            binding.favorites.setOnClickListener {
                toggleFavorite()
            }
            binding.share.setOnClickListener {
                shareVideo()
            }
        }

        private fun shareVideo() {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Video")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video: ${binding.tvTitle.text}")
            context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
        }

        fun setVideoPath(url: String) {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        binding.pbLoading.visibility = View.VISIBLE
                    } else if (playbackState == Player.STATE_READY) {
                        binding.pbLoading.visibility = View.GONE
                    }
                }
            })

            binding.playerView.player = exoPlayer
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)
            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
                isPlaying = true
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))

            // Hide play/pause button initially
            hidePlayPauseButton()

            // Set up touch listener to show/hide play/pause button
            binding.playerView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        togglePlayPause()
                    }
                }
                true
            }
        }

        private fun togglePlayPause() {
            if (isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            isPlaying = !isPlaying
            togglePlayPauseButtonVisibility()
        }

        private fun togglePlayPauseButtonVisibility() {
            if (isPlaying) {
                showPlayPauseButton()
            } else {
                hidePlayPauseButton()
            }
        }

        private fun showPlayPauseButton() {
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.GONE
        }

        private fun hidePlayPauseButton() {
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.GONE
        }

        private fun toggleFavorite() {
            if (isFavorite) {
                binding.favorites.setImageResource(R.drawable.favourate)
            } else {
                binding.favorites.setImageResource(R.drawable.faivouratefull)
            }
            isFavorite = !isFavorite
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = ListVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(view, context, videoPreparedListener)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val model = videos[position]
        holder.binding.tvTitle.text = model.title
        holder.setVideoPath(model.url)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }
}

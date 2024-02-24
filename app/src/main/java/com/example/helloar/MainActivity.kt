package com.example.helloar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.helloar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: VideoAdapter
    private val videos = ArrayList<Video>()
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }


        videos.add(
            Video(
                "Big Buck Bunny",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            )
        )

        videos.add(
            Video(
                "Elephant Dream",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            )
        )

        videos.add(
            Video(
                "For Bigger Blazes",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            )
        )

        videos.add(
            Video(
                "Solo Levelling",
                "https://aniwatchtv.to/watch/solo-leveling-18718?ep=114721"

            )
        )

        videos.add(
            Video(
                "Anime Goku Attitude WhatsApp Status Videos",
                "https://www.statushut.net/wp-content/uploads/2022/12/Anime-Goku-Attitude-WhatsApp-Status-Videos.mp4"
            )
        )

        videos.add(
            Video(
                "New Anime Best Status Videos In English",
                "https://www.statushut.net/wp-content/uploads/2022/12/New-Anime-Best-Status-Videos-In-English.mp4"
            )
        )

        videos.add(
            Video(
                "Indian Team Status Kehte Hain Humko Pyar Se India Wale",
                "https://mobstatus.com/wp-content/uploads/2023/09/Indian-team-status-kehte-Hain-humko-pyar-se-India-wale-_support-_trendingshorts-_viwes720P_HD.mp4"
            )
        )

        videos.add(
            Video(
                "Team India Now Vs Team India Then Status",
                "https://mobstatus.com/wp-content/uploads/2023/09/Team-India-Now-Vs-Team-India-Then-Status-_-Team-India-Whatsapp-Status-_-Old-Is-Gold-_cricket-_shorts720P_HD.mp4"
            )
        )

        videos.add(
            Video(
                "Cricket Lover Sad Status Video",
                "https://mobstatus.com/wp-content/uploads/2023/09/Cricket-Lover-Sad-Status-Video-__-4K-Full-Screen-WhatsApp-Sad-Status-Video-__720P_HD.mp4"
            )
        )

        videos.add(
            Video(
                "Introducing Indian Cricket Team",
                "https://mobstatus.com/wp-content/uploads/2022/09/Introducing-Indian-Cricket-Team-720P_HD.mp4"
            )
        )

        adapter = VideoAdapter(this, videos, object : VideoAdapter.OnVideoPreparedListener {
            override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                exoPlayerItems.add(exoPlayerItem)
            }
        })

        binding.viewPager2.adapter = adapter

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                if (previousIndex != -1) {
                    val player = exoPlayerItems[previousIndex].exoPlayer
                    player.pause()
                    player.playWhenReady = false
                }
                val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                if (newIndex != -1) {
                    val player = exoPlayerItems[newIndex].exoPlayer
                    player.playWhenReady = true
                    player.play()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager2.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager2.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayerItems.isNotEmpty()) {
            for (item in exoPlayerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
            }
        }
    }
}
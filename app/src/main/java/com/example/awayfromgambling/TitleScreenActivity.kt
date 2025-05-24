package com.example.awayfromgambling

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.VideoView
import android.view.ViewGroup
import android.util.DisplayMetrics

class TitleScreenActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnToggleAudio: ImageButton
    private var isMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.titlescreen)

        videoView = findViewById(R.id.videoBackground)
        btnToggleAudio = findViewById(R.id.btnToggleAudio)

        // Load mute state from SharedPreferences
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        isMuted = prefs.getBoolean("isMuted", false)

        // Setup and loop background video with proper scaling
        setupBackgroundVideo()

        // Setup background music using MusicManager
        MusicManager.play(this, R.raw.menuost, MusicManager.Screen.TITLE_SCREEN)
        if (isMuted) {
            MusicManager.mute()
        }

        // Set correct icon on load
        btnToggleAudio.setImageResource(if (isMuted) R.drawable.mute else R.drawable.unmute)

        // Toggle mute state on click
        btnToggleAudio.setOnClickListener {
            isMuted = !isMuted
            if (isMuted) {
                MusicManager.mute()
            } else {
                MusicManager.unmute(this, MusicManager.Screen.TITLE_SCREEN)
            }
            btnToggleAudio.setImageResource(if (isMuted) R.drawable.mute else R.drawable.unmute)
            prefs.edit().putBoolean("isMuted", isMuted).apply()
        }

        // Play Game
        findViewById<Button>(R.id.btnPlayGame).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("start_fragment", "home")
            startActivity(intent)
        }

        // My Cards
        findViewById<Button>(R.id.btnMyCardsTitle).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("start_fragment", "collection")
            startActivity(intent)
        }

        // Quit
        findViewById<Button>(R.id.btnQuitApp).setOnClickListener {
            finishAffinity()
        }
    }

    private fun setupBackgroundVideo() {
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.title}")
        videoView.setVideoURI(videoUri)
        
        videoView.setOnPreparedListener { mediaPlayer ->
            // Calculate the scaling needed to cover the screen
            val videoRatio = mediaPlayer.videoWidth.toFloat() / mediaPlayer.videoHeight.toFloat()
            val screenRatio = resources.displayMetrics.widthPixels.toFloat() / resources.displayMetrics.heightPixels.toFloat()
            
            val scale: Float
            if (videoRatio > screenRatio) {
                // Video is wider than screen, scale by height
                scale = resources.displayMetrics.heightPixels.toFloat() / mediaPlayer.videoHeight.toFloat()
            } else {
                // Video is taller than screen, scale by width
                scale = resources.displayMetrics.widthPixels.toFloat() / mediaPlayer.videoWidth.toFloat()
            }
            
            // Apply the scaling
            val layoutParams = videoView.layoutParams
            layoutParams.width = (mediaPlayer.videoWidth * scale).toInt()
            layoutParams.height = (mediaPlayer.videoHeight * scale).toInt()
            videoView.layoutParams = layoutParams
            
            mediaPlayer.isLooping = true
            videoView.start()
        }
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
        // Only play music if not muted
        if (!isMuted) {
            MusicManager.play(this, R.raw.menuost, MusicManager.Screen.TITLE_SCREEN)
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onStop() {
        super.onStop()
        MusicManager.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicManager.stop()
    }
}

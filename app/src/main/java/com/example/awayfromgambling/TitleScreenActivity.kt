package com.example.awayfromgambling

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TitleScreenActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnToggleAudio: ImageButton
    private var isMuted = false
    private var backgroundMusic: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.titlescreen)

        videoView = findViewById(R.id.videoBackground)
        btnToggleAudio = findViewById(R.id.btnToggleAudio)

        // Load mute state from SharedPreferences
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        isMuted = prefs.getBoolean("isMuted", false)

        // Setup and loop background video
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.titlebg}")
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener {
            it.isLooping = true
            videoView.start()
        }

        // Setup background music
        backgroundMusic = MediaPlayer.create(this, R.raw.menuost)
        backgroundMusic?.isLooping = true
        backgroundMusic?.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
        backgroundMusic?.start()

        // Set correct icon on load
        btnToggleAudio.setImageResource(if (isMuted) R.drawable.mute else R.drawable.unmute)

        // Toggle mute state on click
        btnToggleAudio.setOnClickListener {
            isMuted = !isMuted
            backgroundMusic?.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
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

    override fun onPause() {
        super.onPause()
        videoView.pause()
        backgroundMusic?.pause()
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
        if (!isMuted) {
            backgroundMusic?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic?.release()
        backgroundMusic = null
    }
}

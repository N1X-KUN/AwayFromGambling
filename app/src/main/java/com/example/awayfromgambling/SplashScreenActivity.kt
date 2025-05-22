package com.example.awayfromgambling

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoadingScreenActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 2 seconds splash
    }
}

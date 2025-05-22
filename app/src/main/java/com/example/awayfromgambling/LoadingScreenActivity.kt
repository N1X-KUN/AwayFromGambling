package com.example.awayfromgambling

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class LoadingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, TitleScreenActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 2 seconds loading
    }
}

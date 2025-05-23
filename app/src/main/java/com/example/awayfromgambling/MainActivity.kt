package com.example.awayfromgambling

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.awayfromgambling.CollectionTabFragment
import com.example.awayfromgambling.HomeTabFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)

        // Get the start fragment from intent
        val startFragment = intent.getStringExtra("start_fragment") ?: "home"
        
        // Show loading screen for 1 second before showing the actual content
        Handler(Looper.getMainLooper()).postDelayed({
            val fragment = when (startFragment) {
                "collection" -> CollectionTabFragment()
                else -> HomeTabFragment()
            }
            
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }, 1000)
    }

    fun switchToCollectionTab() {
        // Show loading screen
        setContentView(R.layout.loading)
        
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, CollectionTabFragment())
                .addToBackStack(null)
                .commit()
        }, 1000)
    }

    fun switchToHomeTab() {
        // Show loading screen
        setContentView(R.layout.loading)
        
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, HomeTabFragment())
                .addToBackStack(null)
                .commit()
        }, 1000)
    }

    fun goToTitleScreen() {
        // Show loading screen first
        setContentView(R.layout.loading)
        
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, TitleScreenActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}

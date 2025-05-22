package com.example.awayfromgambling

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.awayfromgambling.CollectionTabFragment
import com.example.awayfromgambling.HomeTabFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openCollection = intent.getBooleanExtra("openCollection", false)
        val fragment = if (openCollection) CollectionTabFragment() else HomeTabFragment()

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit()
    }

    fun switchToCollectionTab() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, CollectionTabFragment())
            .addToBackStack(null)
            .commit()
    }

    fun switchToHomeTab() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, HomeTabFragment())
            .addToBackStack(null)
            .commit()
    }
}

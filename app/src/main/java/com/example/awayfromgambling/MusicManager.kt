package com.example.awayfromgambling

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.awayfromgambling.CollectionTabFragment
import com.example.awayfromgambling.HomeTabFragment

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    var isMuted: Boolean = false
    var currentTrack: Int = -1
    private var currentScreen: Screen = Screen.NONE

    enum class Screen {
        TITLE_SCREEN,
        HOME_TAB,
        COLLECTION_TAB,
        NONE
    }

    // Define which music resources are allowed for each screen
    private val screenMusicMap = mapOf(
        Screen.TITLE_SCREEN to setOf(R.raw.menuost),
        Screen.HOME_TAB to setOf(R.raw.gameost),
        Screen.COLLECTION_TAB to setOf(R.raw.gachaost)
    )

    fun play(context: Context, musicResId: Int, screen: Screen) {
        // Validate if the music is allowed for this screen
        if (!isValidMusicForScreen(musicResId, screen)) {
            Log.w("MusicManager", "Attempted to play invalid music $musicResId for screen $screen")
            return
        }

        // If same track is already playing and not muted, don't restart
        if (musicResId == currentTrack && mediaPlayer?.isPlaying == true && !isMuted) return

        try {
            // Create new player before stopping old one to prevent gap
            val newPlayer = MediaPlayer.create(context, musicResId)
            newPlayer.isLooping = true
            newPlayer.setVolume(if (isMuted) 0f else 1.0f, if (isMuted) 0f else 1.0f)
            
            // Start new player
            newPlayer.start()
            
            // Stop and release old player
            stop()
            
            // Set new player as current
            mediaPlayer = newPlayer
            currentTrack = musicResId
            currentScreen = screen
        } catch (e: Exception) {
            Log.e("MusicManager", "Error playing music", e)
        }
    }

    private fun isValidMusicForScreen(musicResId: Int, screen: Screen): Boolean {
        return screenMusicMap[screen]?.contains(musicResId) ?: false
    }

    fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("MusicManager", "Error stopping music", e)
        }
        mediaPlayer = null
        currentTrack = -1
        currentScreen = Screen.NONE
    }

    fun mute() {
        isMuted = true
        mediaPlayer?.setVolume(0f, 0f)
    }

    fun unmute(context: Context, screen: Screen) {
        isMuted = false
        mediaPlayer?.setVolume(1.0f, 1.0f)
        if (mediaPlayer?.isPlaying != true) {
            // Restart music if it's not playing
            screenMusicMap[screen]?.firstOrNull()?.let { musicResId ->
                play(context, musicResId, screen)
            }
        }
    }

    fun getCurrentScreen(): Screen = currentScreen
}

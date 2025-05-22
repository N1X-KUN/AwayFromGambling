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

        if (musicResId == currentTrack) return
        stop()
        if (isMuted) return

        mediaPlayer = MediaPlayer.create(context, musicResId)
        currentTrack = musicResId
        currentScreen = screen
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun isValidMusicForScreen(musicResId: Int, screen: Screen): Boolean {
        return screenMusicMap[screen]?.contains(musicResId) ?: false
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrack = -1
        currentScreen = Screen.NONE
    }

    fun mute() {
        isMuted = true
        stop()
    }

    fun unmute(context: Context, screen: Screen) {
        isMuted = false
        // Restart the last played music for the current screen if available
        screenMusicMap[screen]?.firstOrNull()?.let { musicResId ->
            play(context, musicResId, screen)
        }
    }


    fun getCurrentScreen(): Screen = currentScreen
}

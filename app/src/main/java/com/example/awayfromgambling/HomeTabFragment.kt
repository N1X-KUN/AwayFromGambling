package com.example.awayfromgambling

import android.animation.*
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class HomeTabFragment : Fragment() {

    private lateinit var homeRoot: ConstraintLayout
    private lateinit var scoreText: TextView
    private lateinit var highScoreText: TextView
    private lateinit var cardStatus: TextView
    private lateinit var rainbowBg: ImageView

    private var score = 0
    private var highScore = 0

    private val handler = Handler(Looper.getMainLooper())
    private val decreaseScoreRunnable = object : Runnable {
        override fun run() {
            if (score > 0) {
                score--
                updateScoreText()
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.hometab, container, false)
        homeRoot = view.findViewById(R.id.home_root)
        scoreText = view.findViewById(R.id.scoreText)
        highScoreText = view.findViewById(R.id.highScoreText)
        cardStatus = view.findViewById(R.id.cardStatus)
        rainbowBg = view.findViewById(R.id.rainbowBg)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        highScore = loadHighScore()
        updateScoreText()
        highScoreText.text = "High Score: $highScore"

        startRainbowBackground()
        handler.post(decreaseScoreRunnable)

        homeRoot.setOnClickListener {
            score++
            if (score > highScore) {
                highScore = score
                saveHighScore(highScore)
                highScoreText.text = "High Score: $highScore"
            }
            updateScoreText()
            if (score >= 100) {
                triggerSpinEvent()
            }
        }
    }

    private fun updateScoreText() {
        scoreText.text = "Score: $score"
    }

    private fun triggerSpinEvent() {
        score = 0
        updateScoreText()

        val fadeOut = ObjectAnimator.ofFloat(homeRoot, "alpha", 1f, 0f).apply {
            duration = 700
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    showCardSpinWheel()
                }
            })
        }
        fadeOut.start()
    }

    private fun showCardSpinWheel() {
        Handler(Looper.getMainLooper()).postDelayed({
            val card = getRandomCard()
            showCardObtainedEffect(card)
        }, 1000)
    }

    private fun getRandomCard(): String {
        val cards = listOf("Naruto", "Luffy", "Tanjiro", "Gojo", "Levi", "Goku")
        return cards.random()
    }

    private fun showCardObtainedEffect(card: String) {
        homeRoot.alpha = 1f
        cardStatus.text = "🎴 You got: $card 🎴"

        val animator = ObjectAnimator.ofPropertyValuesHolder(
            cardStatus,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f, 1f),
            PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        )
        animator.duration = 1000
        animator.start()

        saveCardToCollection(card)
    }

    private fun saveCardToCollection(card: String) {
        val prefs = requireContext().getSharedPreferences("collection", Context.MODE_PRIVATE)
        val current = prefs.getStringSet("cards", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        current.add(card)
        prefs.edit().putStringSet("cards", current).apply()
    }

    private fun loadHighScore(): Int {
        val prefs = requireContext().getSharedPreferences("scores", Context.MODE_PRIVATE)
        return prefs.getInt("highScore", 0)
    }

    private fun saveHighScore(score: Int) {
        val prefs = requireContext().getSharedPreferences("scores", Context.MODE_PRIVATE)
        prefs.edit().putInt("highScore", score).apply()
    }

    private fun startRainbowBackground() {
        val animDrawable = rainbowBg.drawable as? AnimationDrawable
        animDrawable?.setEnterFadeDuration(400)
        animDrawable?.setExitFadeDuration(400)
        animDrawable?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(decreaseScoreRunnable)
    }
}

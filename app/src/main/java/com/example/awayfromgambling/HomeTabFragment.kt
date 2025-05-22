package com.example.awayfromgambling

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import kotlin.random.Random

class HomeTabFragment : Fragment() {

    private lateinit var player: ImageView
    private lateinit var goodItem: ImageView
    private lateinit var badItem: ImageView
    private lateinit var scoreText: TextView
    private lateinit var highScoreText: TextView
    private lateinit var unlockPopup: LinearLayout
    private lateinit var unlockedCardImage: ImageView
    private lateinit var continueButton: Button
    private lateinit var infoButton: ImageView
    private lateinit var prefs: SharedPreferences

    private val handler = Handler(Looper.getMainLooper())
    private var score = 0
    private var highScore = 0
    private var isPopupVisible = false

    private val characters = listOf(
        "Izuku Midoriya", "Ochako Uraraka", "Loid Forger", "Yor Forger",
        "Saitama", "Fubuki", "Ken Takakura", "Seiko Ayase",
        "Sukuna Yuji", "Nobara Kugisaki", "Wakana Gojou", "Marin Kitagawa",
        "Sakuta Azusagawa", "Mai Sakurajima", "Hanako", "Nene Yashiro",
        "Taro Sakamoto", "Osaragi", "Kazuya Kinoshita", "Ruka Sarashina",
        "Kafka Hibino", "Mina Ashiro", "Footsoldier D", "Yumeko Suzukiri",
        "Shinra Kusakabe", "Tamaki Kotatsu", "Kamado Tanjiro", "Mitsuri Kanroji",
        "Jinwoo Sung", "HaeIn Cha", "Goku", "Android 18",
        "Yoichi Isagi", "Anri Teieri", "Noko Shikanoko", "Torako Koshi",
        "Aquamarine Hoshino", "Ruby Hoshino", "Stark", "Frieren"
    )

    override fun onResume() {
        super.onResume()
        MusicManager.play(requireContext(), R.raw.gameost, MusicManager.Screen.HOME_TAB)
    }

    override fun onPause() {
        super.onPause()
        if (!MusicManager.isMuted) {
            MusicManager.stop()
        }
    }

    private fun normalizeName(name: String): String {
        return name.lowercase().replace(" ", "")
    }

    private fun getRandomCharacterDrawable(context: Context): Int {
        val character = characters.random()
        val resName = normalizeName(character)
        saveUnlockedCharacter(resName)
        return context.resources.getIdentifier(resName, "drawable", context.packageName)
    }

    private fun saveUnlockedCharacter(name: String) {
        val unlockedSet = prefs.getStringSet("unlockedCards", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        unlockedSet.add(name)
        prefs.edit().putStringSet("unlockedCards", unlockedSet).apply()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.hometab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player = view.findViewById(R.id.player)
        goodItem = view.findViewById(R.id.goodItem)
        badItem = view.findViewById(R.id.badItem)
        scoreText = view.findViewById(R.id.scoreText)
        highScoreText = view.findViewById(R.id.highScoreText)
        unlockPopup = view.findViewById(R.id.unlockPopup)
        unlockedCardImage = view.findViewById(R.id.unlockedCardImage)
        continueButton = view.findViewById(R.id.continueButton)
        infoButton = view.findViewById(R.id.switchTabsButton)

        prefs = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        highScore = prefs.getInt("highScore", 0)
        highScoreText.text = "High Score: $highScore"

        setupDragging()
        setupContinueButton()
        setupInfoButton()
        startGameLoop()
    }

    private fun setupDragging() {
        player.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    player.x = event.rawX - player.width / 2
                    player.y = event.rawY - player.height
                }
            }
            true
        }
    }

    private fun setupContinueButton() {
        continueButton.setOnClickListener {
            unlockPopup.visibility = View.GONE
            isPopupVisible = false
        }
    }

    private fun setupInfoButton() {
        infoButton.setOnClickListener {
            showNavigationPopup()
        }
    }

    private fun showNavigationPopup() {
        val popupView = layoutInflater.inflate(R.layout.popupblur, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )

        popupView.setOnClickListener { popupWindow.dismiss() }

        popupView.findViewById<Button>(R.id.btnTitleScreen).setOnClickListener {
            val intent = Intent(requireContext(), TitleScreenActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
            requireActivity().finish()
        }

        popupView.findViewById<Button>(R.id.btnMyCards).setOnClickListener {
            (activity as? MainActivity)?.switchToCollectionTab()
            popupWindow.dismiss()
        }

        popupView.findViewById<Button>(R.id.btnHomeGame).setOnClickListener {
            popupWindow.dismiss() // Already on Home, just dismiss
        }

        popupWindow.showAtLocation(requireActivity().window.decorView.rootView, Gravity.CENTER, 0, 0)
    }

    private fun startGameLoop() {
        handler.post(object : Runnable {
            override fun run() {
                if (!isPopupVisible) {
                    moveItems()
                    checkCollisions()
                }
                handler.postDelayed(this, 30)
            }
        })

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isPopupVisible) {
                    score += 1
                    updateScore()
                }
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun moveItems() {
        val rootView = view ?: return
        val viewWidth = rootView.width
        val viewHeight = rootView.height

        // Only run logic if layout is properly measured
        if (viewWidth == 0 || viewHeight == 0 || goodItem.width == 0 || badItem.width == 0) return

        goodItem.translationY += 10f
        badItem.translationY += 13f

        if (goodItem.y > viewHeight) {
            goodItem.y = -Random.nextInt(200, 600).toFloat()
            goodItem.x = Random.nextInt(0, viewWidth - goodItem.width).toFloat()
        }
        if (badItem.y > viewHeight) {
            badItem.y = -Random.nextInt(400, 800).toFloat()
            badItem.x = Random.nextInt(0, viewWidth - badItem.width).toFloat()
        }
    }


    private fun checkCollisions() {
        val playerRect = Rect(player.x.toInt(), player.y.toInt(), (player.x + player.width).toInt(), (player.y + player.height).toInt())
        val goodRect = Rect(goodItem.x.toInt(), goodItem.y.toInt(), (goodItem.x + goodItem.width).toInt(), (goodItem.y + goodItem.height).toInt())
        val badRect = Rect(badItem.x.toInt(), badItem.y.toInt(), (badItem.x + badItem.width).toInt(), (badItem.y + badItem.height).toInt())

        when {
            Rect.intersects(playerRect, goodRect) -> {
                score += 2
                goodItem.y = -Random.nextInt(300, 800).toFloat()
                goodItem.x = Random.nextInt(0, requireView().width - goodItem.width).toFloat()
                player.setImageResource(R.drawable.yes)
            }
            Rect.intersects(playerRect, badRect) -> {
                score -= 3
                badItem.y = -Random.nextInt(300, 800).toFloat()
                badItem.x = Random.nextInt(0, requireView().width - badItem.width).toFloat()
                player.setImageResource(R.drawable.no)
            }
            else -> {
                player.setImageResource(R.drawable.maybe)
            }
        }

        updateScore()
    }

    private fun updateScore() {
        scoreText.text = "Score: $score"
        if (score > highScore) {
            highScore = score
            highScoreText.text = "High Score: $highScore"
            prefs.edit().putInt("highScore", highScore).apply()
        }

        if (score >= 100 && !isPopupVisible) {
            showUnlockPopup()
            score = 0
        }
    }

    private fun showUnlockPopup() {
        isPopupVisible = true
        val imageRes = getRandomCharacterDrawable(requireContext())
        unlockedCardImage.setImageResource(imageRes)
        unlockPopup.visibility = View.VISIBLE
    }
}

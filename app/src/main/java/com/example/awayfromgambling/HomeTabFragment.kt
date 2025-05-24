package com.example.awayfromgambling

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import kotlin.random.Random

class HomeTabFragment : Fragment() {

    private lateinit var player: ImageView
    private lateinit var goodItem: ImageView
    private lateinit var badItem: ImageView
    private lateinit var badItem2: ImageView
    private lateinit var badItem3: ImageView
    private lateinit var scoreText: TextView
    private lateinit var highScoreText: TextView
    private lateinit var unlockPopup: LinearLayout
    private lateinit var unlockedCardImage: ImageView
    private lateinit var unlockedCardName: TextView
    private lateinit var viewInMyCards: Button
    private lateinit var continueButton: Button
    private lateinit var infoButton: ImageView
    private lateinit var countdownTimer: TextView
    private lateinit var scoreLine: View
    private lateinit var pointIndicator: TextView
    private lateinit var hpText: TextView
    private lateinit var hpBar: ProgressBar
    private lateinit var gameOverScreen: LinearLayout
    private lateinit var gameOverScore: TextView
    private lateinit var restartButton: Button
    private lateinit var prefs: SharedPreferences

    private val handler = Handler(Looper.getMainLooper())
    private var score = 0
    private var highScore = 0
    private var hp = 100
    private var isPopupVisible = false
    private var gameStarted = false
    private var initialPlayerY = 0f
    private var gamePhase = GamePhase.NOT_STARTED
    private var lastSpawnTime = 0L
    private var characterChangeTimer: Runnable? = null
    private var currentLevel = 1

    private enum class GamePhase {
        NOT_STARTED,
        FAST_PHASE,    // 0-3 seconds
        MEDIUM_PHASE,  // 3-12 seconds
        NORMAL_PHASE   // After 12 seconds
    }

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
        if (!gameStarted) {
            startCountdown()
        }
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

    private fun startCountdown() {
        countdownTimer.visibility = View.VISIBLE
        handler.post(object : Runnable {
            var countdown = 3
            override fun run() {
                if (countdown > 0) {
                    countdownTimer.text = countdown.toString()
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    countdownTimer.visibility = View.GONE
                    startGame()
                }
            }
        })
    }

    private fun startGame() {
        gameStarted = true
        gamePhase = GamePhase.FAST_PHASE
        MusicManager.play(requireContext(), R.raw.gameost, MusicManager.Screen.HOME_TAB)
        
        handler.postDelayed({
            gamePhase = GamePhase.MEDIUM_PHASE
            handler.postDelayed({
                gamePhase = GamePhase.NORMAL_PHASE
            }, 9000) // 9 seconds after medium phase starts (12 seconds total)
        }, 3000) // Switch to medium phase after 3 seconds

        startGameLoop()
    }

    private fun startGameLoop() {
        handler.post(object : Runnable {
            override fun run() {
                if (!isPopupVisible && gameStarted) {
                    moveItems()
                    checkCollisions()
                    spawnNewItems()
                }
                handler.postDelayed(this, 16) // ~60 FPS
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.hometab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player = view.findViewById(R.id.player)
        goodItem = view.findViewById(R.id.goodItem)
        badItem = view.findViewById(R.id.badItem)
        badItem2 = view.findViewById(R.id.badItem2)
        badItem3 = view.findViewById(R.id.badItem3)
        scoreText = view.findViewById(R.id.scoreText)
        highScoreText = view.findViewById(R.id.highScoreText)
        unlockPopup = view.findViewById(R.id.unlockPopup)
        unlockedCardImage = view.findViewById(R.id.unlockedCardImage)
        unlockedCardName = view.findViewById(R.id.unlockedCardName)
        viewInMyCards = view.findViewById(R.id.viewInMyCards)
        continueButton = view.findViewById(R.id.continueButton)
        infoButton = view.findViewById(R.id.switchTabsButton)
        countdownTimer = view.findViewById(R.id.countdownTimer)
        scoreLine = view.findViewById(R.id.scoreLine)
        pointIndicator = view.findViewById(R.id.pointIndicator)
        hpText = view.findViewById(R.id.hpText)
        hpBar = view.findViewById(R.id.hpBar)
        gameOverScreen = view.findViewById(R.id.gameOverScreen)
        gameOverScore = view.findViewById(R.id.gameOverScore)
        restartButton = view.findViewById(R.id.restartButton)

        prefs = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        highScore = prefs.getInt("highScore", 0)
        highScoreText.text = "High Score: $highScore"

        // Wait for layout to be ready
        view.post {
            // Initialize all items above the screen
            listOf(goodItem, badItem, badItem2, badItem3).forEach { item ->
                item.visibility = View.VISIBLE
                resetItem(item, view.width)
            }
        }

        setupDragging()
        setupUnlockPopup()
        setupInfoButton()
        setupGameOverScreen()
    }

    private fun setupDragging() {
        var initialX = 0f
        var initialTouchX = 0f

        player.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = v.x
                    initialTouchX = event.rawX
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val delta = event.rawX - initialTouchX
                    val newX = initialX + delta
                    val maxX = (v.parent as View).width - v.width
                    v.x = newX.coerceIn(0f, maxX.toFloat())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupUnlockPopup() {
        unlockPopup.setOnClickListener {
            if (!isPopupVisible) return@setOnClickListener
            unlockPopup.visibility = View.GONE
            isPopupVisible = false
            handler.postDelayed({
                gameStarted = true
                startGameLoop()
            }, 2000)
        }

        viewInMyCards.setOnClickListener {
            (activity as? MainActivity)?.switchToCollectionTab()
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
            (activity as? MainActivity)?.goToTitleScreen()
            popupWindow.dismiss()
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

    private fun setupGameOverScreen() {
        restartButton.setOnClickListener {
            gameOverScreen.visibility = View.GONE
            hp = 100
            score = 0
            currentLevel = 1
            gamePhase = GamePhase.NOT_STARTED
            
            // Reset all items to above the screen
            listOf(goodItem, badItem, badItem2, badItem3).forEach { item ->
                resetItem(item, requireView().width)
            }
            
            updateHPBar()
            startCountdown()
        }
    }

    private fun updateHPBar() {
        hpBar.progress = hp
        hpText.text = "HP: $hp/100"
    }

    private fun showPointIndicator(points: Int, x: Float, y: Float) {
        pointIndicator.text = if (points > 0) "+$points" else "$points"
        pointIndicator.setTextColor(if (points > 0) Color.GREEN else Color.RED)
        pointIndicator.x = x
        pointIndicator.y = y
        pointIndicator.visibility = View.VISIBLE

        val fadeOut = ObjectAnimator.ofFloat(pointIndicator, "alpha", 1f, 0f)
        fadeOut.duration = 1000
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.start()

        handler.postDelayed({
            pointIndicator.visibility = View.GONE
            pointIndicator.alpha = 1f
        }, 1000)
    }

    private fun changeCharacterTemporarily(resourceId: Int) {
        characterChangeTimer?.let { handler.removeCallbacks(it) }
        player.setImageResource(resourceId)
        characterChangeTimer = Runnable {
            player.setImageResource(R.drawable.maybe)
        }
        handler.postDelayed(characterChangeTimer!!, 2000) // Changed to 2 seconds
    }

    private fun spawnNewItems() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSpawnTime > 1500) { // Spawn items every 1.5 seconds
            val rootView = view ?: return
            val viewWidth = rootView.width
            
            // Determine number of items to spawn based on score
            val maxItems = when {
                score >= 300 -> 4
                score >= 200 -> 3
                score >= 100 -> 2
                else -> 1
            }

            // Spawn items
            repeat(maxItems) {
                // 50/50 chance for primo vs nrimo
                val isGood = Random.nextDouble() < 0.5 // Changed to 0.5 for 50% chance
                val item = when {
                    isGood -> goodItem
                    else -> when (Random.nextInt(3)) { // Randomly choose one of the bad items
                        0 -> badItem
                        1 -> badItem2
                        else -> badItem3
                    }
                }
                
                // Only spawn if the item is not already visible
                if (item.visibility != View.VISIBLE) {
                    spawnItem(item, viewWidth)
                }
            }
            lastSpawnTime = currentTime
        }
    }

    private fun resetItem(item: ImageView, viewWidth: Int) {
        // Reset to above the screen
        item.visibility = View.VISIBLE  // Ensure item is visible when reset
        item.y = -item.height.toFloat() - Random.nextInt(200, 800).toFloat()
        item.x = Random.nextInt(0, viewWidth - item.width).toFloat()
    }

    private fun spawnItem(item: ImageView, viewWidth: Int) {
        resetItem(item, viewWidth)
        item.visibility = View.VISIBLE
    }

    private fun moveItems() {
        val rootView = view ?: return
        val viewHeight = rootView.height
        val viewWidth = rootView.width
        val speedMultiplier = getSpeedMultiplier()

        if (viewWidth == 0 || viewHeight == 0) return

        // Get the absolute bottom position where items should fall to
        val bottomPosition = rootView.height - player.height - resources.getDimensionPixelSize(android.R.dimen.app_icon_size) / 8f

        val baseSpeed = 15f  // Increased base speed
        val items = listOf(goodItem, badItem, badItem2, badItem3)
        
        items.forEach { item ->
            if (item.visibility == View.VISIBLE) {
                // Move the item with consistent speed
                val movement = baseSpeed * speedMultiplier
                item.y += movement
                
                // Check if item has reached the bottom position
                if (item.y >= bottomPosition) {
                    if (item == goodItem) {
                        hp = (hp - 10).coerceAtLeast(0)
                        updateHPBar()
                        showPointIndicator(-10, item.x, bottomPosition)
                    }
                    // Make sure item stays visible until it's reset
                    item.visibility = View.VISIBLE
                    resetItem(item, viewWidth)
                }

                // If item somehow got way below screen, reset it
                if (item.y > rootView.height + item.height) {
                    resetItem(item, viewWidth)
                }
            }
        }

        if (hp <= 0) {
            showGameOver()
        }
    }

    private fun getSpeedMultiplier(): Float {
        return when (gamePhase) {
            GamePhase.FAST_PHASE -> 3.0f    // x3 speed (0-3 seconds)
            GamePhase.MEDIUM_PHASE -> 2.0f   // x2 speed (3-12 seconds)
            GamePhase.NORMAL_PHASE -> 1.5f   // x1.5 speed (after 12 seconds)
            GamePhase.NOT_STARTED -> 0f
        }
    }

    private fun showGameOver() {
        gameStarted = false
        gameOverScore.text = "Final Score: $score"
        gameOverScreen.visibility = View.VISIBLE
        if (score > highScore) {
            highScore = score
            highScoreText.text = "High Score: $highScore"
            prefs.edit().putInt("highScore", highScore).apply()
        }
    }

    private fun checkCollisions() {
        val playerRect = Rect(
            player.x.toInt(),
            player.y.toInt(),
            (player.x + player.width).toInt(),
            (player.y + player.height).toInt()
        )

        listOf(goodItem, badItem, badItem2, badItem3).forEach { item ->
            if (item.visibility == View.VISIBLE) {
                val itemRect = Rect(
                    item.x.toInt(),
                    item.y.toInt(),
                    (item.x + item.width).toInt(),
                    (item.y + item.height).toInt()
                )

                if (Rect.intersects(playerRect, itemRect)) {
                    when (item) {
                        goodItem -> {
                            score += 5
                            showPointIndicator(5, item.x, item.y - 50)
                            changeCharacterTemporarily(R.drawable.yes)
                        }
                        else -> {
                            score = (score - 3).coerceAtLeast(0)
                            hp = (hp - 5).coerceAtLeast(0)
                            updateHPBar()
                            showPointIndicator(-3, item.x, item.y - 50)
                            changeCharacterTemporarily(R.drawable.no)
                        }
                    }
                    // Keep item visible until reset
                    item.visibility = View.VISIBLE
                    resetItem(item, requireView().width)
                    updateScore()
                }
            }
        }
    }

    private fun updateScore() {
        scoreText.text = "Score: $score"
        if (score > highScore) {
            highScore = score
            highScoreText.text = "High Score: $highScore"
            prefs.edit().putInt("highScore", highScore).apply()
        }

        if (score >= currentLevel * 100 && !isPopupVisible) {
            showUnlockPopup()
            currentLevel++
        }
    }

    private fun showUnlockPopup() {
        isPopupVisible = true
        
        // Get current collection
        val collectionPrefs = requireContext().getSharedPreferences("collection", Context.MODE_PRIVATE)
        val currentCollection = collectionPrefs.getStringSet("cards", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        
        // Filter out already collected characters
        val availableCharacters = characters.filterNot { currentCollection.contains(it) }
        
        if (availableCharacters.isEmpty()) {
            // All characters collected!
            unlockPopup.visibility = View.GONE
            isPopupVisible = false
            return
        }
        
        // Get a random uncollected character
        val character = availableCharacters.random()
        val resName = normalizeName(character)
        val imageRes = requireContext().resources.getIdentifier(resName, "drawable", requireContext().packageName)
        
        // Add to collection
        currentCollection.add(character)
        collectionPrefs.edit().putStringSet("cards", currentCollection).apply()
        
        // Show popup
        unlockedCardImage.setImageResource(imageRes)
        unlockedCardName.text = "New Card Unlocked: $character"
        unlockPopup.visibility = View.VISIBLE
    }
}

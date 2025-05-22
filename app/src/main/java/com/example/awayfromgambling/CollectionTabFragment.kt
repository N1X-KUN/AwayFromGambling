package com.example.awayfromgambling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CollectionTabFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter

    private val allCharacters = listOf(
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
        MusicManager.play(requireContext(), R.raw.gachaost, MusicManager.Screen.COLLECTION_TAB)
    }

    override fun onPause() {
        super.onPause()
        if (!MusicManager.isMuted) {
            MusicManager.stop()
        }
    }

    private lateinit var ownedButton: Button
    private lateinit var missingButton: Button
    private lateinit var collected: Set<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.collectiontab, container, false)

        val prefs = requireContext().getSharedPreferences("collection", Context.MODE_PRIVATE)
        collected = prefs.getStringSet("cards", emptySet()) ?: emptySet()

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 4)

        ownedButton = view.findViewById(R.id.ownedButton)
        missingButton = view.findViewById(R.id.missingButton)

        // Default to showing owned
        updateDisplay(showOwned = true)
        ownedButton.isSelected = true
        missingButton.isSelected = false

        ownedButton.setOnClickListener {
            updateDisplay(showOwned = true)
            ownedButton.isSelected = true
            missingButton.isSelected = false
        }

        missingButton.setOnClickListener {
            updateDisplay(showOwned = false)
            ownedButton.isSelected = false
            missingButton.isSelected = true
        }

        val switchTabsButton: ImageView = view.findViewById(R.id.switchTabsButton)
        switchTabsButton.setOnClickListener {
            showNavigationPopup()
        }

        val footer: LinearLayout = view.findViewById(R.id.footer)
        footer.setOnClickListener {
            (activity as? MainActivity)?.switchToHomeTab()
        }

        return view
    }

    private fun updateDisplay(showOwned: Boolean) {
        val displayList = if (showOwned) {
            allCharacters.filter { collected.contains(it) }
        } else {
            allCharacters.filterNot { collected.contains(it) }
        }

        adapter = CollectionAdapter(displayList, collected)
        recyclerView.adapter = adapter
    }

    private fun showNavigationPopup() {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popupblur, null)
        val popup = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )

        popupView.findViewById<Button>(R.id.btnHomeGame).setOnClickListener {
            (activity as? MainActivity)?.switchToHomeTab()
            popup.dismiss()
        }

        popupView.findViewById<Button>(R.id.btnMyCards).setOnClickListener {
            popup.dismiss() // Already in this tab
        }

        popup.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
    }

    fun goToHomeTab(view: View) {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }
}

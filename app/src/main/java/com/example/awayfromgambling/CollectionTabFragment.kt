package com.example.awayfromgambling

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

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

        ownedButton = view.findViewById(R.id.ownedButton)
        missingButton = view.findViewById(R.id.missingButton)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = GridLayoutManager(context, 4)

        // Set default to show owned
        updateDisplay(showOwned = true)
        ownedButton.isSelected = true

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

        return view
    }

    private fun updateDisplay(showOwned: Boolean) {
        val displayList = if (showOwned) {
            allCharacters.filter { collected.contains(it) }
        } else {
            allCharacters.filterNot { collected.contains(it) }
        }

        adapter = CollectionAdapter(displayList.toList(), collected)
        recyclerView.adapter = adapter
    }
}

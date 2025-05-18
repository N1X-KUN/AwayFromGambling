package com.example.awayfromgambling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollectionAdapter(
    private val allCharacters: List<String>,
    private val collected: Set<String>
) : RecyclerView.Adapter<CollectionAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carditem, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val name = allCharacters[position]
        val context = holder.itemView.context

        if (collected.contains(name)) {
            // Unlocked card
            holder.cardName.text = name
            val normalized = name.lowercase().replace(" ", "").replace("-", "")
            val resId = context.resources.getIdentifier(normalized, "drawable", context.packageName)
            holder.cardImage.setImageResource(if (resId != 0) resId else R.drawable.locked)
        } else {
            // Locked card
            holder.cardName.text = "???"
            holder.cardImage.setImageResource(R.drawable.locked)
        }
    }

    override fun getItemCount(): Int = allCharacters.size

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardName: TextView = view.findViewById(R.id.cardName)
    }
}
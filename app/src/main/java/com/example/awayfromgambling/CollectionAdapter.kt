package com.example.awayfromgambling

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
            
            // Add click listener for enlarged view
            holder.itemView.setOnClickListener {
                showEnlargedCard(context, name, resId)
            }
        } else {
            // Locked card
            holder.cardName.text = "???"
            holder.cardImage.setImageResource(R.drawable.locked)
            holder.itemView.setOnClickListener(null)
        }
    }

    private fun showEnlargedCard(context: android.content.Context, name: String, resId: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        val imageView = ImageView(context).apply {
            setImageResource(if (resId != 0) resId else R.drawable.locked)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
        }
        
        dialog.setContentView(imageView)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        // Dismiss dialog when clicking anywhere
        imageView.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    override fun getItemCount(): Int = allCharacters.size

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardName: TextView = view.findViewById(R.id.cardName)
    }
}
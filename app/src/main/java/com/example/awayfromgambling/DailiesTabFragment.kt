package com.example.awayfromgambling

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView

class DailiesTabFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return TextView(requireContext()).apply {
            text = "Tab 3 content here"
            textSize = 24f
        }
    }
}

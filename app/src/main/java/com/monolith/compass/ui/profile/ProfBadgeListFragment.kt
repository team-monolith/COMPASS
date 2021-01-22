package com.monolith.compass.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.kittinunf.fuel.core.Progress
import com.monolith.compass.MainActivity
import com.monolith.compass.R


class ProfBadgeListFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_badge_list, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.imageView11).setOnClickListener {
            val back = resources.getIdentifier("badge_background_1", "drawable", "com.monolith.compass")
            val badge = resources.getIdentifier("badge_icon_0", "drawable", "com.monolith.compass")
            val fg = parentFragment as ProfEditFragment
            val test = fg.view?.findViewById<ImageView>(R.id.badge_img)
            test?.setBackgroundResource(back)
            test?.setImageResource(badge)
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}
package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.R

class ProfBadgeFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_badge, container, false)
        val back_btn: Button = root.findViewById(R.id.back_bt)

        back_btn.setOnClickListener{
            findNavController().navigate(R.id.navigation_profile)
        }

        return root
    }

}
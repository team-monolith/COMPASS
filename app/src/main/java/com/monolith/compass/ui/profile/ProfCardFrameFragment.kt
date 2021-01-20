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


class ProfCardFrameFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_card_frame, container, false)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.frame_btn).setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_card_frame_to_navigation_profile_card_background)
        }
        view.findViewById<Button>(R.id.back_bt).setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_card_frame_to_navigation_profile_edit)
        }
        view.findViewById<Button>(R.id.save_btn).setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_card_frame_to_navigation_profile_edit)
        }
    }

}
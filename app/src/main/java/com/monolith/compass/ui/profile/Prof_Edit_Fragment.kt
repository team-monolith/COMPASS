package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R


class Prof_Edit_Fragment : Fragment() {


    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        val textView: TextView = root.findViewById(R.id.name_txt)
        val okbtn : Button = root.findViewById(R.id.combtn)
        val name_text :EditText = root.findViewById(R.id.name_txtedit)
        //val card_img : ImageView = root.findViewById(R.id.frame_img)

        val ma = activity as MainActivity?
        var value =ma?.SharedValue

        textView.text = value

        /*card_img.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile_card)
        }*/

        okbtn.setOnClickListener{
            ma?.SharedValue = "こんにちは"
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile)
        }
        return root
    }
}
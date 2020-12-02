package com.monolith.compass.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
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
        val textView: TextView = root.findViewById(R.id.txt_profile)
        val okbtn : Button = root.findViewById(R.id.combtn)
        val name_text :EditText = root.findViewById(R.id.name_txt)

        val ma = activity as MainActivity?
        var value =ma?.SharedValue

        textView.text = value


        okbtn.setOnClickListener{
            ma?.SharedValue = "こんにちは"
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile)
        }
        return root
    }
}
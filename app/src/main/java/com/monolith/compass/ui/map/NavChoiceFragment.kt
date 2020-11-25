package com.monolith.compass.ui.map

import android.content.Context
import com.monolith.compass.ui.map.MapViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R

class NavChoiceFragment : Fragment() {

    private lateinit var navchoiceViewModel: NavChoiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navchoiceViewModel =
            ViewModelProvider(this).get(NavChoiceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_nav_choice, container, false)
        val textView: TextView = root.findViewById(R.id.txt_navchoice)
        navchoiceViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnMap).setOnClickListener{
            findNavController().navigate(R.id.action_navigation_navchoice_to_navigation_map)
        }
        view.findViewById<Button>(R.id.btnEvent).setOnClickListener {
            //遷移処理
        }

    }

}
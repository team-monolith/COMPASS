package com.monolith.compass.ui.map

import com.monolith.compass.ui.map.MapViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R

class EventFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        eventViewModel =
                ViewModelProvider(this).get(EventViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        //val textView: TextView = root.findViewById(R.id.txt_map)
        eventViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        return root
    }
}
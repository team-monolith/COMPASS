package com.monolith.compass.ui.fitness

import com.monolith.compass.ui.fitness.FitnessViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R

class FitnessFragment : Fragment() {

    private lateinit var fitnessViewModel: FitnessViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fitnessViewModel =
            ViewModelProvider(this).get(FitnessViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_fitness, container, false)
        val textView: TextView = root.findViewById(R.id.txt_fitness)
        fitnessViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
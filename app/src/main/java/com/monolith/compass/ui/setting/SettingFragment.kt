package com.monolith.compass.ui.setting

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.LocationService
import com.monolith.compass.MainActivity
import com.monolith.compass.MyApp
import com.monolith.compass.R

class SettingFragment : Fragment() {

    private lateinit var dashboardViewModel: SettingViewModel

    //フラグメント生成時処理
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        val textView: TextView = root.findViewById(R.id.txt_setting)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)

        view.findViewById<Button>(R.id.btnSTOP).setOnClickListener{
            MainActivity().stopLocationService(getContext() as Application)
        }

    }


}
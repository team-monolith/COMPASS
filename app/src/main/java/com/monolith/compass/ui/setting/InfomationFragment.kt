package com.monolith.compass.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp


class InfomationFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel

    val GLOBAL = MyApp.getInstance()


    //var _clickListener: SettingFragment.OnClickListener? = null


    //フラグメント生成時処理
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_infomation, container, false)
        //val textView: TextView = root.findViewById(R.id.txt_setting)
        return inflater.inflate(R.layout.fragment_infomation, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnPrevInfo).setOnClickListener {
            findNavController().navigate(R.id.action_infomationFragment_to_navigation_setting)
        }
    }
}
package com.monolith.compass.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R


class SettingFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel

    var _clickListener: OnClickListener? = null

    //フラグメント生成時処理
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        val textView: TextView = root.findViewById(R.id.txt_setting)
        settingViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)

        view.findViewById<Button>(R.id.btnSTART).setOnClickListener(View.OnClickListener {
            _clickListener!!.onClick_start()
        })

        view.findViewById<Button>(R.id.btnSTOP).setOnClickListener(View.OnClickListener {
            _clickListener!!.onClick_stop()
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            _clickListener = context as OnClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement OnArticleSelectedListener.")
        }
    }

    //Activityにイベントを通知
    interface OnClickListener {
        fun onClick_start()
        fun onClick_stop()
    }



}
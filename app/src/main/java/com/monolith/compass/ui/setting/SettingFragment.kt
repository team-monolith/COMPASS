package com.monolith.compass.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.monolith.compass.R
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.com.monolith.compass.MyApp


class SettingFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel

    val GLOBAL= MyApp.getInstance()


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
            _clickListener?.onClick_start()
        })

        view.findViewById<Button>(R.id.btnSTOP).setOnClickListener(View.OnClickListener {
            _clickListener?.onClick_stop()
        })

        view.findViewById<Button>(R.id.btnPOST).setOnClickListener(View.OnClickListener {
            datapost()
        })
        view.findViewById<Button>(R.id.btnUSRINFO).setOnClickListener (View.OnClickListener {
            //Toast.makeText(context,"テストメッセージ",Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_navigation_setting_to_usrinfoFragment)
        })
        view.findViewById<Button>(R.id.btnSystemSetting).setOnClickListener (View.OnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_systemSettingFragment)
        })
        view.findViewById<Button>(R.id.btnInfomation).setOnClickListener (View.OnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_infomationFragment)
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

    fun datapost(){

        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("data", "monolith")

        "https://ky-server.net/~monolith/system/send.php".httpPost(POSTDATA.toList()).response { _, response, result ->
            when (result) {
                is Result.Success -> {
                }
                is Result.Failure -> {
                }
            }
        }

    }


}
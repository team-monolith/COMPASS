package com.monolith.compass.ui.setting

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.R
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.com.monolith.compass.MyApp
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.HashMap


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
            MyApp().FileWrite("" +
                    "2021/02/01,10000,5622,0,230\n" +
                    "2021/02/02,10000,1243,0,89\n" +
                    "2021/02/03,10000,7184,0,400\n" +
                    "2021/02/04,10000,12432,0,300\n" +
                    "2021/02/05,10000,3451,0,89\n" +
                    "2021/02/06,10000,6243,0,120\n" +
                    "2021/02/07,10000,1567,0,46\n" +
                    "2021/02/08,10000,542,0,23\n" +
                    "2021/02/09,10000,3754,0,35\n" +
                    "2021/02/10,10000,9676,0,400\n" +
                    "2021/02/11,10000,6848,0,89\n" +
                    "2021/02/12,10000,3765,0,89\n" +
                    "2021/02/13,10000,3576,0,89\n" +
                    "2021/02/14,10000,9876,0,89\n" +
                    "2021/02/15,10000,6843,0,89\n" +
                    "2021/02/16,10000,7964,0,89\n" +
                    "2021/02/17,10000,7954,0,89\n" +
                    "2021/02/18,10000,3738,0,89\n" +
                    "2021/02/19,10000,12986,0,89\n" +
                    "2021/02/20,10000,10346,0,89\n" +
                    "2021/02/21,10000,10057,0,89\n" +
                    "2021/02/22,10000,9846,0,89\n" +
                    "2021/02/23,10000,6539,0,89\n" +
                    "2021/02/24,10000,10583,0,89\n" +
                    "2021/02/25,10000,15235,0,89\n" +
                    "2021/02/26,10000,644,0,89\n" +
                    "2021/02/27,10000,3264,0,89\n" +
                    "2021/02/28,10000,6243,0,89" +
                    "","ACTIVITYLOG.txt")
        })
        view.findViewById<Button>(R.id.btnUSRINFO).setOnClickListener (View.OnClickListener {
            //Toast.makeText(context,"テストメッセージ",Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_navigation_setting_to_usrinfoFragment)
        })
        view.findViewById<Button>(R.id.btnSystemSetting).setOnClickListener (View.OnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_systemSettingFragment)
        })
        view.findViewById<ImageButton>(R.id.btnInfomation).setOnClickListener (View.OnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_infomationFragment)
        })
        view.findViewById<Button>(R.id.btnTerms).setOnClickListener (View.OnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_termsFragment)
        })
        view.findViewById<Button>(R.id.btnFWA).setOnClickListener(View.OnClickListener {
            FileWriteAddTest("","GPSLOG.txt")
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

    override fun onDetach(){
        super.onDetach()
        _clickListener=null
    }

    //Activityにイベントを通知
    interface OnClickListener {
        fun onClick_start()
        fun onClick_stop()
    }

    fun datapost(){

        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("data", "monolith")

        "https://ky-server.net/~monolith/system/send.php".httpPost(POSTDATA.toList()).response { _, _, result ->
            when (result) {
                is Result.Success -> {
                }
                is Result.Failure -> {
                }
            }
        }

    }

    fun FileWriteAddTest(str:String,child:String){
        var buf:String=""
        //val GLOBAL= MyApp.getInstance()

        if(GLOBAL.DIRECTORY==null)return

        try{
            val file= File(GLOBAL.DIRECTORY+"/", child)
            val scan= Scanner(file)
            while(scan.hasNextLine()){
                buf+=scan.nextLine()+"\n"
            }
            buf+=str
            file.writeText("")
        }catch(e: FileNotFoundException){
            val file= File(GLOBAL.DIRECTORY+"/", child)
            file.writeText(str)
        }
    }

    /*ゴールの値を設定するダイアログを出力*/
    fun SettingGoal(DATA:MyApp.LOCAL_DC){
        var target = -1 //歩数をセット
        val myedit = EditText(activity)
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("目標歩数の設定")
            .setMessage("歩数")
            .setView(myedit)
            .setPositiveButton("確定") {_, _  ->
                DATA.TARGET = Integer.parseInt(myedit.getText().toString())
            }
            .setNegativeButton("キャンセル",null)
        dialog.show()
    }
}
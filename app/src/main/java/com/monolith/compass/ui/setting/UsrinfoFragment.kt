package com.monolith.compass.ui.setting

import android.app.ProgressDialog.show
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp


class UsrinfoFragment : Fragment() {
    private lateinit var settingViewModel: SettingViewModel

    val GLOBAL= MyApp.getInstance()


    //var _clickListener: SettingFragment.OnClickListener? = null


    //フラグメント生成時処理
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_usrinfo, container, false)
        //val textView: TextView = root.findViewById(R.id.txt_setting)
        return inflater.inflate(R.layout.fragment_usrinfo, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //コンボボックス生成処理・所持している線の色の読み込み処理が必要
        //現在設定中の色を取得ー＞nowSelectLine
        val spinner= view.findViewById<Spinner>(R.id.spinnerLineColor)
        val nowSelectLine="金色"
        val spinnerItems= arrayOf("色を選択してください","赤","青","緑","黄","黒")
        val adapter= context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_dropdown_item,spinnerItems) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View, position: Int, id: Long) {
                //選択項目をselectLineColorで保持
                val selectLineColor=parent?.selectedItem as String
                //Toast.makeText(context,selectLineColor,Toast.LENGTH_SHORT).show()
                //spinner.setBackgroundColor(Color.WHITE)
                /*val imgAlert=view.findViewById<ImageView>(R.id.imgAlert)
                if (imgAlert.visibility.toString()!="INVISIBLE") {
                    imgAlert.visibility = View.INVISIBLE
                }*/

            }
            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        view.findViewById<Button>(R.id.buttonUsrinfoSave).setOnClickListener {
            //保存ボタン

            val height=view.findViewById<EditText>(R.id.editHeight).text.toString()
            val weight=view.findViewById<EditText>(R.id.editWeight).text.toString()


            //色を選択してくださいの時の例外処理
            val lineColor=spinner.selectedItem as String
            if (lineColor=="色を選択してください"){
                /*val toast=Toast.makeText(context,"色が選択されていません",Toast.LENGTH_LONG)
                val toastView=toast.view
                toastView?.setBackgroundColor(Color.YELLOW)
                //toast.show()
                //spinner.setBackgroundColor(Color.RED)*/
                //val imgAlert=view.findViewById<ImageView>(R.id.imgAlert)
                //imgAlert.visibility=View.VISIBLE
            }else  {
                //身長・体重・線の色をサーバーに送信
                Toast.makeText(context,weight+"Kg:"+height+"cm:"+lineColor+"保存しました",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
            }



        }
        view.findViewById<Button>(R.id.buttonUsrinfoCancel).setOnClickListener {
            //キャンセルボタン・変更前の色再送信？
            Toast.makeText(context,"キャンセルしました"+nowSelectLine,Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
        }


    }





}
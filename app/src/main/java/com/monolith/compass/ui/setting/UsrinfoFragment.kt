package com.monolith.compass.ui.setting

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
        val spinner= view.findViewById<Spinner>(R.id.spinner)
        val spinnerItems= arrayOf("赤","青","緑","黄","黒")
        val adapter= context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_dropdown_item,spinnerItems) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapter

        /*spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View, position: Int, id: Long) {
                //選択項目をselectLineColorで保持
                val selectLineColor=parent?.selectedItem as String
            }
            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }*/


        view.findViewById<Button>(R.id.buttonUsrinfoSave).setOnClickListener {
            //保存ボタン
            val height=view.findViewById<EditText>(R.id.editHeight).text.toString()
            val weight=view.findViewById<EditText>(R.id.editWeight).text.toString()
            var selectLineColor=""

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                //　アイテムが選択された時
                override fun onItemSelected(parent: AdapterView<*>?,
                                            view: View, position: Int, id: Long) {
                    //選択項目をselectLineColorで保持
                    selectLineColor=parent?.selectedItem as String
                }
                //　アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }



            Toast.makeText(context,weight+"Kg:"+height+"cm:"+selectLineColor+"色:"+"保存しました",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
        }
        view.findViewById<Button>(R.id.buttonUsrinfoCancel).setOnClickListener {
            //キャンセルボタン
            Toast.makeText(context,"キャンセルしました",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
        }


    }





}
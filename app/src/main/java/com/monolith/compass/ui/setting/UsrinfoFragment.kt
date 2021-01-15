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

        //色を選択していない時の警告ボタンのID取得
        val imageAlertView=view.findViewById<ImageView>(R.id.imgAlert)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View, position: Int, id: Long) {
                //選択項目をselectLineColorで保持
                val selectLineColor=parent?.selectedItem as String
                if (imageAlertView.visibility!=View.INVISIBLE){
                    imageAlertView.visibility=View.INVISIBLE
                }

            }
            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        view.findViewById<Button>(R.id.buttonUsrinfoSave).setOnClickListener {

            //保存ボタン
            val height=view.findViewById<EditText>(R.id.editHeight).text.toString()
            val weight=view.findViewById<EditText>(R.id.editWeight).text.toString()
            val imgAlertView=view.findViewById<ImageView>(R.id.imgAlert)
            val textHeight=view.findViewById<TextView>(R.id.textHeight)
            val textWeight=view.findViewById<TextView>(R.id.textWeight)
            var CHW=0



            //入力、選択がない時の例外処理
            val lineColor=spinner.selectedItem as String
            if (lineColor=="色を選択してください"){
                imgAlertView.visibility=View.VISIBLE
                CHW+=1
            }
            if(height==""){
                textHeight.setBackgroundColor(Color.YELLOW)
                CHW+=3
            }
            if (weight==""){
                textWeight.setBackgroundColor(Color.YELLOW)
                CHW+=5
            }
            when(CHW) {
                1->{Toast.makeText(context,"色を選択してください",Toast.LENGTH_SHORT).show()}
                3->{Toast.makeText(context,"身長を入力してください",Toast.LENGTH_SHORT).show()}
                4->{Toast.makeText(context,"色の選択と身長を入力してください",Toast.LENGTH_SHORT).show()}
                5->{Toast.makeText(context,"体重を入力してください",Toast.LENGTH_SHORT).show()}
                6->{Toast.makeText(context,"色の選択と体重の入力してください",Toast.LENGTH_SHORT).show()}
                8->{Toast.makeText(context,"身長と体重を入力してください",Toast.LENGTH_SHORT).show()}
                9->{Toast.makeText(context,"全項目入力をしてください",Toast.LENGTH_SHORT).show()}
                else->{
                //身長・体重・線の色をサーバーに送信
                Toast.makeText(context,weight+"Kg:"+height+"cm:"+lineColor+"保存しました",Toast.LENGTH_SHORT).show()
                    //LOCALSETTINGにデータを保存(身長,体重,目標歩数 ※未実装,GPS取得設定 ※未実装,自宅座標 ※未実装,非取得範囲 ※未実装,マイカラー) val test = height.toString() + "," + weight.toString() + "," + "0" + "," + "0" + "," + "0.0" + "," + "0.0" + "," + "0" + lineColor.toString()
                    val str = height.toString() + "," + weight.toString() + "," + "0" + "," + "0" + "," + "0.0" + "," + "0.0" + "," + "0" + "," +lineColor.toString()
                    GLOBAL.FileWrite(str,"LOCALSETTING.txt")

                    findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)}
            }



        }
        view.findViewById<Button>(R.id.buttonUsrinfoCancel).setOnClickListener {
            //キャンセルボタン・変更前の色再送信？
            Toast.makeText(context,"キャンセルしました"+nowSelectLine,Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
        }




    }





}
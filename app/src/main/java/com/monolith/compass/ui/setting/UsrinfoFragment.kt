package com.monolith.compass.ui.setting

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
        val colorSpinner= view.findViewById<Spinner>(R.id.spinnerLineColor)
        val nowSelectLine="金色"
        val colorSpinnerItems= arrayOf("色を選択してください","赤","青","緑","黄","黒")
        val colorAdapter= context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_dropdown_item,colorSpinnerItems) }
        colorAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter=colorAdapter


        val GPSNotRangeSpinner=view.findViewById<Spinner>(R.id.GPSNotRangeSpinner)
        val defaultGPSNotRange:Int=100
        val GPSNotRangeItems= arrayOf(100,250,500,1000)
        val GPSNotRagneAdapter=context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_dropdown_item,GPSNotRangeItems) }
        GPSNotRagneAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        GPSNotRangeSpinner.adapter=GPSNotRagneAdapter



        //色を選択していない時の警告ボタンのID取得
        val imageAlertView=view.findViewById<ImageView>(R.id.imgAlert)


        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

            val editItemList= arrayOf(R.id.editHeight,R.id.editWeight,R.id.editWalk,R.id.editHomeGPSPositionN,R.id.editHomeGPSPositionE)
            val textItemList= arrayOf(R.id.textHeight,R.id.textWeight,R.id.textWalk,R.id.textHomeGPSPosition,R.id.textHomeGPSPosition)
            var toastText=""
            var fragItem=1

            for (i in 0..4){
                if(view.findViewById<EditText>(editItemList[i]).text.toString()==""){
                    view.findViewById<TextView>(textItemList[i]).setBackgroundColor(Color.YELLOW)
                    Toast.makeText(context,"空だよ",Toast.LENGTH_SHORT).show()
                    fragItem=0
                }
            }


            //保存ボタン
            val height=view.findViewById<EditText>(R.id.editHeight).text.toString()
            val weight=view.findViewById<EditText>(R.id.editWeight).text.toString()
            val Walk=view.findViewById<EditText>(R.id.editWalk).toString()



            val imgAlertView=view.findViewById<ImageView>(R.id.imgAlert)
            val textHeight=view.findViewById<TextView>(R.id.textHeight)
            val textWeight=view.findViewById<TextView>(R.id.textWeight)

            var CHW=0



            //入力、選択がない時の例外処理
            val lineColor=colorSpinner.selectedItem as String
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
                1 -> {
                    Toast.makeText(context, "色を選択してください", Toast.LENGTH_SHORT)
                }
                3 -> {
                    Toast.makeText(context, "身長を入力してください", Toast.LENGTH_SHORT)
                }
                4 -> {
                    Toast.makeText(context, "色の選択と身長を入力してください", Toast.LENGTH_SHORT)
                }
                5 -> {
                    Toast.makeText(context, "体重を入力してください", Toast.LENGTH_SHORT)
                }
                6 -> {
                    Toast.makeText(context, "色の選択と体重の入力してください", Toast.LENGTH_SHORT)
                }
                8 -> {
                    Toast.makeText(context, "身長と体重を入力してください", Toast.LENGTH_SHORT)
                }
                9 -> {
                    Toast.makeText(context, "全項目入力をしてください", Toast.LENGTH_SHORT)
                }
            }


                if (fragItem == 1) {
                    //身長・体重・線の色をサーバーに送信
                    Toast.makeText(context,weight + "Kg:" + height + "cm:" + lineColor + "保存しました",
                        Toast.LENGTH_SHORT
                    ).show()
                    //LOCALSETTINGにデータを保存(身長,体重,目標歩数 ※未実装,GPS取得設定 ※未実装,自宅座標 ※未実装,非取得範囲 ※未実装,マイカラー) val test = height.toString() + "," + weight.toString() + "," + "0" + "," + "0" + "," + "0.0" + "," + "0.0" + "," + "0" + lineColor.toString()
                    val str =
                        height.toString() + "," + weight.toString() + "," + "0" + "," + "0" + "," + "0.0" + "," + "0.0" + "," + "0" + "," + lineColor.toString()
                    GLOBAL.FileWrite(str, "LOCALSETTING.txt")

                    findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
                }

            }







        view.findViewById<Button>(R.id.buttonUsrinfoCancel).setOnClickListener {
            //キャンセルボタン・変更前の色再送信？
            Toast.makeText(context,"キャンセルしました"+nowSelectLine,Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
        }


        view.findViewById<Button>(R.id.buttonToGPS).setOnClickListener {
            findNavController().navigate(R.id.action_usrinfoFragment_to_mapSettingFragment  )
        }

    }


}


//メモ～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・～・
/*
URL一覧
https://blog.misatowater.com/entry/tech/kotlin/osmdroid-04
https://seesaawiki.jp/w/moonlight_aska/d/%C3%E6%BF%B4%B0%CC%C3%D6%A4%CE%B0%DE%C5%D9%A1%A6%B7%D0%C5%D9%A4%F2%C0%DF%C4%EA/%BC%E8%C6%C0%A4%B9%A4%EB
https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial?hl=ja
https://qiita.com/outerlet/items/36a9152c1cce46a9cfa6
https://qiita.com/outerlet/items/0d74061fa5d625a33dfb
検索候補：Kotlin Fragment マップ-Kotlin 緯度経度　選択
自宅座標、非取得範囲の設定画面の作成
 */
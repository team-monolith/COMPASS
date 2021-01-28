package com.monolith.compass.ui.setting

import android.annotation.SuppressLint
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
import org.w3c.dom.Text


class UsrinfoFragment : Fragment() {
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
        val root = inflater.inflate(R.layout.fragment_usrinfo, container, false)
        //val textView: TextView = root.findViewById(R.id.txt_setting)
        return inflater.inflate(R.layout.fragment_usrinfo, container, false)

    }


    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data: MyApp.LOCAL_DC =GLOBAL.LocalSettingRead("LOCAL.txt")

        val colorItems: Array<String> =arrayOf("赤", "青", "緑", "黄", "黒", "白")
        val colorId: Array<String> =arrayOf("#ff0000", "#0000ff", "#00ff00", "#ffff00", "#000000", "#ffffff")


        //コンボボックス生成処理・所持している線の色の読み込み処理が必要
        //現在設定中の色を取得ー＞nowSelectLine
        val colorSpinner = view.findViewById<Spinner>(R.id.spinnerLineColor)
        val nowSelectLine = "金色"
        val colorSpinnerItems = colorItems
        val colorAdapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item,
                colorSpinnerItems
            )
        }
        colorAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter = colorAdapter


        val GPSNotRangeSpinner = view.findViewById<Spinner>(R.id.GPSNotRangeSpinner)
        val defaultGPSNotRange: Int = 100
        val GPSNotRangeItems = arrayOf("100", "250", "500", "1000")
        val GPSNotRagneAdapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item,
                GPSNotRangeItems
            )
        }
        GPSNotRagneAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        GPSNotRangeSpinner.adapter = GPSNotRagneAdapter


        val GPSSettingSpinner = view.findViewById<Spinner>(R.id.GPSSettingSpinner)
        val GPSSettingList = arrayOf("常時取得", "アプリ使用中のみ取得", "取得しない")
        val GPSSettingAdapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item,
                GPSSettingList
            )
        }
        GPSSettingAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        GPSSettingSpinner.adapter = GPSSettingAdapter





        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View, position: Int, id: Long
            ) {
                //選択項目をselectLineColorで保持
                val selectLineColor = parent?.selectedItem as String

            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        //データをもとに再配置
        view.findViewById<TextView>(R.id.editWeight).text=data.WEIGHT.toString()
        view.findViewById<TextView>(R.id.editHeight).text=data.HEIGHT.toString()
        view.findViewById<TextView>(R.id.editWalk).text=data.TARGET.toString()
        view.findViewById<TextView>(R.id.editHomeGPSPositionE).text=data.HOME_Y.toString()
        view.findViewById<TextView>(R.id.editHomeGPSPositionN).text=data.HOME_X.toString()
        for(i in colorItems.indices){
            if(colorId[i] == data.MYCOLOR){
                colorSpinner.setSelection(i)
            }
        }
        GPSSettingSpinner.setSelection(data.GPSFLG)
        for(i in GPSNotRangeItems.indices){
            if(GPSNotRangeItems[i] == data.ACQUIED.toString()){
                GPSNotRangeSpinner.setSelection(i)
            }
        }


        //保存ボタン押下処理
        view.findViewById<Button>(R.id.buttonUsrinfoSave).setOnClickListener {

            val editItemList = arrayOf(
                R.id.editHeight,
                R.id.editWeight,
                R.id.editWalk,
                R.id.editHomeGPSPositionN,
                R.id.editHomeGPSPositionE
            )
            val textItemList = arrayOf(
                R.id.textHeight,
                R.id.textWeight,
                R.id.textWalk,
                R.id.textHomeGPSPosition,
                R.id.textHomeGPSPosition
            )
            var fragItem = 1


            //空白のところがないか判定
            for (i in 0..4) {
                if (view.findViewById<EditText>(editItemList[i]).text.toString() == "") {
                    view.findViewById<TextView>(textItemList[i]).setBackgroundColor(Color.YELLOW)
                    fragItem = 0
                }
            }


            //保存用にStringで持ってくる
            val height = view.findViewById<EditText>(R.id.editHeight).text.toString()
            val weight = view.findViewById<EditText>(R.id.editWeight).text.toString()
            val Walk = view.findViewById<EditText>(R.id.editWalk).text.toString()
            val latitude = view.findViewById<EditText>(R.id.editHomeGPSPositionN).text.toString()
            val longitude = view.findViewById<EditText>(R.id.editHomeGPSPositionE).text.toString()
            val lineColor = colorSpinner.selectedItem as String
            val gpsNotRange = GPSNotRangeSpinner.selectedItem as String
            val gpsSetting = GPSSettingSpinner.selectedItem as String
            var gpsSettingNum = ""

            //色をRGBに変換
            var colorRGB = ""
            for (c in 0..5) {
                if (lineColor == colorItems[c]) {
                    colorRGB = colorId[c]
                }
            }

            if (gpsSetting == "取得しない") {
                fragItem = 2
                gpsSettingNum = "2"
            } else if (gpsSetting == "常時取得") {
                gpsSettingNum = "0"
            } else if (gpsSetting == "アプリ使用中のみ取得") {
                gpsSettingNum = "1"
            }



            if (fragItem == 1) {
                //保存した旨の通知
                Toast.makeText(context, "保存しました", Toast.LENGTH_SHORT).show()
                GLOBAL.LocalSettingWrite(MyApp.LOCAL_DC(height.toFloat(),weight.toFloat(),Walk.toInt(),gpsSettingNum.toInt(),latitude.toFloat(),longitude.toFloat(),gpsNotRange.toInt(),colorRGB),"LOCAL.txt")

                findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
            } else if (fragItem == 2) {
                Toast.makeText(context, "'取得しない'は選択できません", Toast.LENGTH_SHORT).show()
            } else if (fragItem == 0) {
                Toast.makeText(context, "空白は認められません", Toast.LENGTH_SHORT).show()
            }

        }


        //キャンセルボタン
        view.findViewById<Button>(R.id.buttonUsrinfoCancel).setOnClickListener {
            //キャンセルした旨の通知
            Toast.makeText(context, "キャンセルしました", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_usrinfoFragment_to_navigation_setting)
        }


        //GoogleMAPに遷移(仮置き)
        view.findViewById<Button>(R.id.buttonToGPS).setOnClickListener {
            findNavController().navigate(R.id.action_usrinfoFragment_to_mapSettingFragment)
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
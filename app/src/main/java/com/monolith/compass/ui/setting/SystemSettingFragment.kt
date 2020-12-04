package com.monolith.compass.ui.setting

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.time.LocalDate
import java.util.*


class SystemSettingFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_system_setting, container, false)
        //val textView: TextView = root.findViewById(R.id.txt_setting)
        return inflater.inflate(R.layout.fragment_system_setting, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //初期化の確認ダイアログ表示
        view.findViewById<Button>(R.id.buttonInitialize).setOnClickListener {
           Initialize("初期化","本当に初期化しますか？\nこの動作は元に戻すことができません！\n下記の文字を入力してください。")
        }

        view.findViewById<Button>(R.id.buttonClearCache).setOnClickListener {
            //キャッシュクリア
            context?.cacheDir?.deleteRecursively()
            Toast.makeText(context,"キャッシュクリア",Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.buttonTakeover).setOnClickListener {
           makeTakeoverDialog("パスワードを発行してください")
        }
    }


    //初期化・
    fun Initialize(title:String,message:String){
        val day= LocalDate.now().dayOfWeek.name.toString()
        val initializeText=EditText(context)
        val initializeDialog=AlertDialog.Builder(activity)
        initializeDialog.setTitle(title)
        initializeDialog.setMessage(message+"\n\n"+day)
        initializeDialog.setView(initializeText)
        initializeDialog.setPositiveButton("OK") { dialog, which ->
            if (initializeText.text.toString()==day) {

                //初期化処理
                (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()

            }else{
                Initialize("もう一度入力してください","文字を入力してください。\n再度入力をお願いします。\n\n")
            }
        }
        initializeDialog.setNegativeButton("Cancel", null)
        initializeDialog.show()
    }



    fun makeTakeoverDialog(titleText: String){
        val passedit = EditText(context)
        passedit.inputType = InputType.TYPE_CLASS_TEXT or   InputType.TYPE_TEXT_VARIATION_PASSWORD
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(titleText)
        dialog.setView(passedit)
        dialog.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            takeover(passedit.text.toString())
        })
        dialog.setNegativeButton("キャンセル", null)
        dialog.show()
    }

    fun takeover(pass:String){
        if (pass!=""){
            val takeoverDialog = AlertDialog.Builder(context)
            takeoverDialog.setTitle("引継ぎID・パスワード確認画面")
            takeoverDialog.setMessage("このIDとパスワードで発行しますよろしいですか\n\n---------------------------------------------\n"+"ID:" + "1234567890" + "\n" + "パスワード:"+pass+"\n---------------------------------------------\n\n忘れた場合引き継ぐことができません！")
            takeoverDialog.setPositiveButton("発行") { takeoverdialog, which ->
                //パスワードをサーバーに送信&保存する



                Toast.makeText(context, "引継ぎID、パスワードを発行しました", Toast.LENGTH_SHORT).show()
            }
            takeoverDialog.setNegativeButton("キャンセル"){takeoverdialog,which ->
                makeTakeoverDialog("パスワードを入力してください")
            }
            takeoverDialog.show()
        }else{
            makeTakeoverDialog("空白は不可です。再入力をお願いします。")
        }
    }





}
package com.monolith.compass.com.monolith.compass

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlin.random.Random


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること

    val CENTRAL_LATITUDE:Int=1304088//X座標側、下四桁が少数
    val CENTRAL_LONGITUDE:Int=335841//Y座標側、下四桁が小数

    //日本は経度122-154,緯度20-46に存在する
    //y320000,x260000のデータで成り立つ
    //500x500でバッファリングする

    //開始時処理
    override fun onCreate(){
        super.onCreate()
    }

    companion object{
        private var instance: MyApp?=null
        fun getInstance(): MyApp {
            if(instance ==null)
                instance = MyApp()
            return instance!!
        }
    }

    //トースト生成関数、第一引数：実行画面コンテキスト、第二引数：表示メッセージ
    fun toastMake(context: Context,message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

}
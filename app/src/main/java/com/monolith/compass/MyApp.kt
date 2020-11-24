package com.monolith.compass.com.monolith.compass

import android.app.Application
import android.content.Context
import android.widget.Toast


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること
    val SERVER_URL:String="https://ky-server.net/~monolith/system/"
    val CENTRAL_LONGITUDE:Int=335841//Y座標側、下四桁が小数
    val CENTRAL_LATITUDE:Int=1304088//X座標側、下四桁が少数

    //開始時処理
    override fun onCreate(){
        super.onCreate()
        val GLOBAL= getInstance()
    }

    companion object{
        private var instance: MyApp?=null
        fun getInstance(): MyApp {
            if(instance ==null)
                instance = MyApp()
            return instance!!
        }
    }

    fun toastMake(context: Context,message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

}
package com.monolith.compass.com.monolith.compass

import android.app.Application
import android.widget.Toast


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること
    var maincontext:Application?=null

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



    fun toastMake(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }

}
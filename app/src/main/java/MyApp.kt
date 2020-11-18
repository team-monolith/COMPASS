package com.monolith.compass

import android.app.Application

class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること

    //開始時処理
    override fun onCreate(){
        super.onCreate()
        val GLOBAL=MyApp.getInstance()
    }

    companion object{
        private var instance:MyApp?=null
        fun getInstance():MyApp{
            if(instance==null)
                instance=MyApp()
            return instance!!
        }
    }

}
package com.monolith.compass

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


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



    fun toastMake(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }

}
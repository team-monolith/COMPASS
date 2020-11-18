package com.monolith.compass

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること

    //開始時処理
    override fun onCreate(){
        super.onCreate()
        val GLOBAL=MyApp.getInstance()
        RequestGPSPermission()
    }

    companion object{
        private var instance:MyApp?=null
        fun getInstance():MyApp{
            if(instance==null)
                instance=MyApp()
            return instance!!
        }
    }

    //GPSパーミッションを取得、trueが返されれば実行OK

    private fun RequestGPSPermission():Boolean{

        // Android 6, API 23以上でパーミッシンの確認
        if(Build.VERSION.SDK_INT >= 23){
            // 既に許可している
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
                return true
            }
            // 拒否していた場合
            else{
                //拒否時処理を記載する
            }
        }
        else{
            return true
        }

        return false
    }

    fun toastMake(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }

}
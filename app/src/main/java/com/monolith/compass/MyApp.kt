package com.monolith.compass.com.monolith.compass

import android.app.Application
import android.content.Context
import android.widget.Toast


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること
    val SERVER_URL:String="https://ky-server.net/~monolith/system/"
    val SERVER_MAP=Array(500,{arrayOfNulls<Int>(500)})//サーバーから取得した地図情報データ。要素内に-1が存在した時点で読み込みエラー

    val CENTRAL_LATITUDE:Int=1304088//X座標側、下四桁が少数
    val CENTRAL_LONGITUDE:Int=335841//Y座標側、下四桁が小数

    //日本は経度122-154,緯度20-46に存在する
    //y320000,x260000のデータで成り立つ
    //500x500でバッファリングする

    //開始時処理
    override fun onCreate(){
        super.onCreate()
        MAPLOAD()
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

    fun MAPLOAD(){
        MAPCLEAR()
        //読み込み処理を記述
    }

    fun MAPCLEAR(){
        for(y in 0..500){
            for(x in 0..500){
                SERVER_MAP[y][x]=-1
            }
        }
    }

}
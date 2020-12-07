package com.monolith.compass.com.monolith.compass

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.random.Random


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること

    val CENTRAL_LATITUDE:Int=1304090//X座標側、下四桁が少数
    val CENTRAL_LONGITUDE:Int=335840//Y座標側、下四桁が小数

    var DIRECTORY:String?=null

    data class GPSDATA(var GPS_X:Float?,var GPS_Y:Float?,var GPS_A:Float?,var GPS_S:Float?)

    data class MAPDATA(var MAP :Array<Array<Int?>>,var MAP_X:Float?,var MAP_Y:Float?)

    var GPS_LOG=mutableListOf<GPSDATA>()

    var GPS_BUF:GPSDATA=GPSDATA(null,null,null,null)

    //日本は経度122-154,緯度20-46に存在する
    //y320000,x260000のデータで成り立つ
    //500x500でバッファリングする

    //1単位当たり10mで計算

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

    fun FileWriteAdd(str:String,child:String){
        var buf:String=""
        val GLOBAL= getInstance()

        if(GLOBAL.DIRECTORY==null)return

        try{
            val file= File(GLOBAL.DIRECTORY+"/", child)
            val scan= Scanner(file)
            while(scan.hasNextLine()){
                buf+=scan.nextLine()+"\n"
            }
            buf+=str
            file.writeText(buf)
        }catch(e: FileNotFoundException){
            val file= File(GLOBAL.DIRECTORY+"/", child)
            file.writeText(str)
        }
    }

    fun FileRead(child:String){
        var buf:String=""
        val GLOBAL= getInstance()

        if(GLOBAL.DIRECTORY==null)return

        try{
            val file= File(GLOBAL.DIRECTORY+"/", child)
            val scan= Scanner(file)
            scan.useDelimiter("[,\n]")
            while(scan.hasNextLine()&&scan.hasNext()){
                val FILE_X:String=scan.next().substring(2)
                val FILE_Y:String=scan.next().substring(2)
                val FILE_A:String=scan.next().substring(2)
                val FILE_S:String=scan.next().substring(2)
                GLOBAL.GPS_LOG.add(MyApp.GPSDATA(FILE_X.toFloat(),FILE_Y.toFloat(),FILE_A.toFloat(),FILE_S.toFloat()))
            }
        }catch(e: FileNotFoundException){
        }
    }

}
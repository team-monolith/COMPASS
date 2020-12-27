package com.monolith.compass.com.monolith.compass

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること

    val CENTRAL_LATITUDE:Int=1304090//X座標側、下四桁が少数
    val CENTRAL_LONGITUDE:Int=335840//Y座標側、下四桁が小数

    var DIRECTORY:String?=null

    data class GPSDATA(var GPS_X:Float?,var GPS_Y:Float?,var GPS_A:Float?,var GPS_S:Float?)

    data class MAPDATA(var MAP :Array<Array<Int?>>,var MAP_X:Float?,var MAP_Y:Float?)

    data class STEPDATA(var DATE:Date, var TARGET:Int,var STEP:Int,var CAL:Int)

    var GPS_LOG=mutableListOf<GPSDATA>()

    var STEP_LOG=mutableListOf<STEPDATA>()

    var GPS_BUF:GPSDATA=GPSDATA(null,null,null,null)

    var Current:MAPDATA=MAPDATA(Array(500) { arrayOfNulls<Int>(500) },null,null)//ユーザ現在地周辺の地図データ

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

    fun FileRead(child:String):String{
        var buf:String=""
        val GLOBAL=getInstance()

        if(GLOBAL.DIRECTORY==null)return ""

        val file=File(GLOBAL.DIRECTORY+"/",child)

        if(!file.isFile)return ""

        val scan=Scanner(file)

        while(scan.hasNext()){
            buf+=scan.next()
            if(scan.hasNext())buf+="\n"
        }
        return buf
    }

    fun FileWrite(str:String,child:String){
        val GLOBAL= getInstance()

        if(GLOBAL.DIRECTORY==null)return

        try{
            val file= File(GLOBAL.DIRECTORY+"/", child)
            file.writeText(str)
        }catch(e: FileNotFoundException){
            val file= File(GLOBAL.DIRECTORY+"/", child)
            file.writeText(str)
        }
    }

    fun FileWriteAdd(str:String,child:String){
        var buf:String=""
        val GLOBAL= getInstance()

        try{
            val file= File(GLOBAL.DIRECTORY+"/", child)
            val scan= Scanner(FileRead(child))
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

    fun GPSFileRead(child:String){
        val GLOBAL= getInstance()

        GLOBAL.GPS_LOG.clear()

        try{
            val scan= Scanner(FileRead(child))
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

    @SuppressLint("SimpleDateFormat")
    fun STEPFileRead(child:String){
        val GLOBAL= getInstance()

        GLOBAL.STEP_LOG.clear()

        val pattern= SimpleDateFormat("yyyy/MM/dd")

        try{
            var scan= Scanner(FileRead(child))
            scan.useDelimiter("[,\n]")

            //ファイルが存在しない場合は新規で作る。目標値は仮置き
            if(!scan.hasNextLine()){
                FileWrite(pattern.format(Date()).toString()+",10000,0,0\n","STEPLOG.txt")
                scan=Scanner(FileRead(child))
            }

            while(scan.hasNextLine()&&scan.hasNext()){
                val DATE:Date?=pattern.parse(scan.next())
                val TARGET:Int=scan.nextInt()
                val STEP:Int=scan.nextInt()
                val CAL:Int=scan.nextInt()
                GLOBAL.STEP_LOG.add(MyApp.STEPDATA(DATE!!,TARGET,STEP,CAL))
            }

            if(pattern.format(GLOBAL.STEP_LOG[GLOBAL.STEP_LOG.lastIndex].DATE)!=pattern.format(Date())){
                FileWrite(pattern.format(Date()).toString()+",10000,0,0\n","STEPLOG.txt")
                val cl = Calendar.getInstance()
                cl.time = Date()

                //時刻データを破棄
                cl.clear(Calendar.MINUTE)
                cl.clear(Calendar.SECOND)
                cl.clear(Calendar.MILLISECOND)
                cl.set(Calendar.HOUR_OF_DAY, 0)

                //calendar型からdate型に変換
                val date = cl.time
                GLOBAL.STEP_LOG.add(MyApp.STEPDATA(date,10000,0,0))
            }

        }catch(e: FileNotFoundException){
        }
    }

    fun StepFileWrite(child:String){
        val GLOBAL=getInstance()
        var buf=""
        val pattern= SimpleDateFormat("yyyy/MM/dd")
        try{
            val file=File(GLOBAL.DIRECTORY+"/",child)
            for(i in GLOBAL.STEP_LOG.indices){
                buf+=pattern.format(GLOBAL.STEP_LOG[i].DATE)+","+GLOBAL.STEP_LOG[i].TARGET+","+GLOBAL.STEP_LOG[i].STEP+","+GLOBAL.STEP_LOG[i].CAL
                if(i!=GLOBAL.STEP_LOG.lastIndex)buf+="\n"
            }
            file.writeText(buf)
        } catch(e:FileNotFoundException){
            val file= File(GLOBAL.DIRECTORY+"/", child)
            file.writeText("")
        }
    }



    //マップを配列に保存する関数
    fun setMap(data: String) {
        val GLOBAL=getInstance()

        val scan = Scanner(data)
        scan.useDelimiter(",|\r\n")

        GLOBAL.Current.MAP_X = 130.4088f
        GLOBAL.Current.MAP_Y = 33.5841f

        for (fy in 0 until 500) {
            for (fx in 0 until 500) {
                if (scan.hasNextInt()) GLOBAL.Current.MAP[fy][fx] = scan.nextInt()
            }
        }

        val str="X="+GLOBAL.Current.MAP_X+",Y="+GLOBAL.Current.MAP_Y+"\n"+data

        MyApp().FileWrite(str,"MAPLOG.txt")
    }

    //フォルダ内マップデータをセットマップに流せる形式に変換
    fun convertMapFileData(data:String):String{
        val scan = Scanner(data)
        var str=""
        scan.next()
        while(scan.hasNext()){
            str+=scan.next()
            if(scan.hasNext())str+="\n"
        }
        return str
    }

}
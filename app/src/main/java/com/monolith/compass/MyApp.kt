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

    data class CARDDATA(var ID:Int,var NAME:String,var ICON:String?,var LV:Int,var DISTANCE:Int,var BADGE:Int,var BACKGROUND:Int,var FRAME:Int,var COMMENT:String,var STATE:Int)

    var GPS_LOG=mutableListOf<GPSDATA>()

    var STEP_LOG=mutableListOf<STEPDATA>()

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

/*
GLOBALSETTING------------------------------------

->サーバーとの通信に使用するデータ

ID(int)
名前(string)
アイコン(string)
総距離(int)
お気に入りバッジ(int)
ひとこと(string)
名刺背景(int)
名刺フレーム(int)
バッジ状態(int)
-------------------------------------------------





LOCALSETTING-------------------------------------

->ユーザ情報等、アプリ内の設定を保存

身長(float)
体重(float)
目標歩数(int)
GPS取得設定(int)
自宅座標(float,float)
非取得範囲(int)
マイカラー(rgb)
-------------------------------------------------





STEPLOG------------------------------------------

->歩数データを保存。Fitnessはこのファイルを参照

日付(date)
目標歩数(int)
歩数(int)
消費カロリー(int)
-------------------------------------------------





GPSLOG-------------------------------------------

->GPSの全履歴を保存。書き込みはBUFと並列

日付(date)
時間(time)
経度(float)
緯度(float)
範囲(float)
速度(float)
-------------------------------------------------





GPSBUF-------------------------------------------

->GPSのサーバー未送信分履歴を保存

日付(date)
時間(time)
経度(float)
緯度(float)
範囲(float)
速度(float)
-------------------------------------------------





FRIEND-------------------------------------------

->すれ違ったフレンドのIDを保存

ID(int)
-------------------------------------------------





FAVORITE-----------------------------------------

->お気に入りのユーザを保存

ID(int)
-------------------------------------------------





MAPLOG-------------------------------------------

->マップのバッファリングに使用（すると思われる)

-------------------------------------------------























名刺------------------------
・ID
・名前
・ユーザレベル
・アイコン
・総距離
・お気に入りバッジID
・ひとこと
・名刺背景色ID
・名刺フレームID
・バッジの状態（8桁の整数で管理）



バッチ処理時に通信する内容ーーーーーーーーーーーーー
・地図の変更（区間を経度緯度でx分割してフラグ管理、変更分のみアップデート）
 Lバイナリで管理する
・自分の現状の名刺データ

日付変更処理に通信する内容ーーーーーーーーーーーーー
・地図の更新

適宜更新時に取得する内容ーーーーーーーーーーーーーー
・他のユーザの名刺データ




アプリ終了時に処理
・地図更新情報
・すれちがい情報

変更時に処理
・ユーザ名刺データ

朝５時にバッチ処理
・地図更新情報
・ユーザ名刺データ
・すれちがい情報

起動時に処理
・


サーバで保持するべき情報ーーーーーーーーーーーーーー
・ID(8桁整数)
・名前(2バイト10文字）
・ユーザレベル(3桁整数）
・アイコン（300000文字)
・総距離(8桁整数）
・お気に入りバッジID(2桁整数）
・ひとこと（2バイト50文字)
・名刺背景色ID(2桁整数）
・名刺フレームID（2桁整数）
・バッジの状態（8桁の整数で管理）

・地図のマスタデータ200000x180000(csv)

各種ID----------------------------------------------

・バッジID

0ログイン日数
1レベル
2移動距離
3歩数
4開拓
5カロリー
6すれ違い
7イベント

・バッジ背景ID
0.Null
1.ブロンズ
2.シルバー
3.ゴールド
4.ダイヤ
------------------------------------------------------

  白　赤   青
|0123|456|789|


-------------------------------------------------------
 */
package com.monolith.compass.ui.map

import android.graphics.*
import androidx.fragment.app.Fragment
import com.monolith.compass.com.monolith.compass.MyApp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class CanvasDraw : Fragment() {

    private var anim_ringR: Float = 0f

    private var anim_GPSringR:Float=0f

    private var anim_GPSringFlg:Boolean=false

    fun Map(CurrentMAP: Array<Array<Int?>>): Bitmap {

        val output= Bitmap.createBitmap(5000,5000, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(output)

        val blockpaint: Array<Paint> = Array<Paint>(5) { Paint() }
        blockpaint[0].color = Color.parseColor("#FFFFFF")
        blockpaint[1].color = Color.parseColor("#CCCCCC")
        blockpaint[2].color = Color.parseColor("#666666")
        blockpaint[3].color = Color.parseColor("#000000")


        for (y in 0 until 500) {
            for (x in 0 until 500) {
                val rect = Rect(
                    x*10,y*10,x*10+10,y*10+10
                )
                if(CurrentMAP[y][x]!! >0)canvas.drawRect(rect, blockpaint[CurrentMAP[y][x]!!])
            }
        }

        return output
    }

    fun Log(
        Current: MyApp.MAPDATA,
        canvas: Canvas?
    ) {
        val GLOBAL = MyApp.getInstance()
        var paint = Paint()
        paint.strokeWidth = 5f
        paint.isAntiAlias=true
        //色はユーザ選択ができるようにする、いずれ引数で指定する
        paint.color = Color.parseColor("#00AAFF")

        //レコードが0件の場合は終了
        if (GLOBAL.GPS_LOG.size <= 0) return

        if(Current.MAP_X==null||Current.MAP_Y==null)return


        for(i in 0..GLOBAL.GPS_LOG.size-2){

            //座標の中心からのずれを計算
            val sX=((GLOBAL.GPS_LOG[i].GPS_X!!-Current.MAP_X!!)*10000).toInt()
            val sY=((Current.MAP_Y!!-GLOBAL.GPS_LOG[i].GPS_Y!!)*10000).toInt()

            //次のデータも計算しておく
            val sXn=((GLOBAL.GPS_LOG[i+1].GPS_X!!-Current.MAP_X!!)*10000).toInt()
            val sYn=((Current.MAP_Y!!-GLOBAL.GPS_LOG[i+1].GPS_Y!!)*10000).toInt()

            //点と点の差が30m（3ブロック）未満の場合は線を表示
            if (abs(GLOBAL.GPS_LOG[i].GPS_X!! - GLOBAL.GPS_LOG[i + 1].GPS_X!!) < 0.0005 && abs(
                    GLOBAL.GPS_LOG[i].GPS_Y!! - GLOBAL.GPS_LOG[i + 1].GPS_Y!!
                ) < 0.0005
            ) {
                canvas!!.drawLine(2500+sX*10f+5,2500+sY*10F+5,2500+sXn*10f+5,2500+sYn*10f+5,paint)
            }
            canvas!!.drawRect(2500+sX*10f,2500+sY*10f,2500+sX*10+10f,2500+sY*10+10f,paint)
        }
    }

    fun Current(
        Location: MyApp.GPSDATA,
        Current: MyApp.MAPDATA,
        canvas: Canvas?
    ) {

        val Circle = Paint()
        Circle.color = Color.parseColor("#FF0000")
        Circle.isAntiAlias = true

        //GPS誤差が15m以下の場合
        if (Location.GPS_A!! < 15f){

            //座標の中心からのずれを計算
            val sX=((Location.GPS_X!! - Current.MAP_X!!)*10000).toInt()
            val sY=((Current.MAP_Y!! - Location.GPS_Y!!)*10000).toInt()

            Circle.color = Color.parseColor("#00FF00")
            //座標中心円
            canvas!!.drawCircle(
                2500+sX*10f+5,
                2500+sY*10f+5,
                8f,
                Circle
            )
            Circle.style = Paint.Style.STROKE
            Circle.strokeWidth = 2f
            Circle.alpha= (255*(1-(anim_GPSringR/120))).toInt()
            //座標円周円
            canvas.drawCircle(
                2500+sX*10+5f,
                2500+sY*10+5f,
                anim_GPSringR,
                Circle
            )
            //リングサイズが小さい場合は大きくする
            if(anim_GPSringR<120)anim_GPSringR+=3f
            //GPSが更新されており、リングが大きくなりきった場合は小さくする
            else if(anim_GPSringFlg){
                anim_GPSringR=-30f
                anim_GPSringFlg=false
            }
        }
        //GPS誤差が15m以上の場合
        else{
            Circle.color = Color.parseColor("#FF0000")

            //座標の中心からのずれを計算
            val sX=((Location.GPS_X!! - Current.MAP_X!!)*10000).toInt()
            val sY=((Current.MAP_Y!! - Location.GPS_Y!!)*10000).toInt()

            //座標中心円
            canvas!!.drawCircle(
                2500+sX*10f+5,
                2500+sY*10f+5,
                8f,
                Circle
            )

            Circle.style = Paint.Style.STROKE
            Circle.strokeWidth = 2f


            //座標円周円
            canvas.drawCircle(
                2500+sX*10+5f,
                2500+sY*10+5f,
                120f,
                Circle
            )
            Circle.strokeWidth = 2f
            //座標円周円
            canvas.drawCircle(
                2500+sX*10+5f,
                2500+sY*10+5f,
                anim_GPSringR,
                Circle
            )
            //リングサイズが小さい場合は大きくする
            if(anim_GPSringR<120)anim_GPSringR+=3f
            else{
                anim_GPSringR=-30f
            }
        }

    }

    fun Loading(canvas: Canvas?,DLflg:Boolean,GPSflg:Boolean, size: Rect?) {
        val Circle = Paint()
        Circle.color = Color.parseColor("#FF0000")
        Circle.isAntiAlias = true
        Circle.strokeWidth = 2f
        //中心円
        canvas!!.drawCircle(size!!.width() / 2f, size.height() / 4 * 3f, 20f, Circle)
        //外周円
        if(!DLflg){
            canvas.drawCircle(
                size.width() / 2f + cos(anim_ringR) * 150f,
                size.height() / 4 * 3f + sin(anim_ringR) * 150f,
                15f, Circle
            )
        }
        if(!GPSflg){
            canvas.drawCircle(
                size.width() / 2f + cos(anim_ringR / -2) * 150f,
                size.height() / 4 * 3f + sin(anim_ringR / -2) * 150f,
                15f, Circle
            )
        }
        Circle.style = Paint.Style.STROKE
        //円周円
        canvas.drawCircle(size.width() / 2f, size.height() / 4 * 3f, 150f, Circle)
        anim_ringR += 0.2f
        if (anim_ringR >= 640) anim_ringR = 0f

    }

    fun updateGPSFlg(){
        anim_GPSringFlg=true
    }
}
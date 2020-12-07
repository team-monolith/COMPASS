package com.monolith.compass.ui.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.fragment.app.Fragment
import com.monolith.compass.com.monolith.compass.MyApp
import kotlin.math.cos
import kotlin.math.sin

class CanvasDraw : Fragment(){

    private var anim_ringR:Float=0f

    fun Map(posX:Int,posY:Int,scale:Float,CurrentMAP:Array<Array<Int?>>,canvas:Canvas?){

        val paint: Array<Paint> = Array<Paint>(5) { Paint() }
        paint[0].color = Color.parseColor("#FFFFFF")
        paint[1].color = Color.parseColor("#CCCCCC")
        paint[2].color = Color.parseColor("#666666")
        paint[3].color = Color.parseColor("#000000")

        for (y in 0 until 500) {
            for (x in 0 until 500) {
                val rect = Rect(
                    (x * scale + posX).toInt(),
                    (y * scale + posY).toInt(),
                    (x * scale + scale + posX).toInt(),
                    (y * scale + scale + posY).toInt()
                )
                if (CurrentMAP[y][x]!! > 0) canvas!!.drawRect(rect, paint[CurrentMAP[y][x]!!])
            }
        }
    }

    fun Current(posX:Int,posY:Int,scale:Float,Location:MyApp.GPSDATA,Current:MyApp.MAPDATA,canvas:Canvas?){

        val Circle = Paint()
        Circle.color = Color.parseColor("#FF0000")
        Circle.isAntiAlias = true

        //GPS誤差が70m以上ある場合は赤表示
        if (Location.GPS_A!! < 50f) Circle.color = Color.parseColor("#00FF00")
        else Circle.color = Color.parseColor("#FF0000")

        Circle.isAntiAlias = true
        //座標中心円
        canvas!!.drawCircle(
            (250 * scale + posX) + (((Location.GPS_X!! - Current.MAP_X!!) * 10000).toInt()) * scale - scale / 2,
            (250 * scale + posY) + (((Current.MAP_Y!!-Location.GPS_Y!!) * 10000).toInt()) * scale - scale / 2,
            20f,
            Circle
        )
        Circle.style = Paint.Style.STROKE
        Circle.strokeWidth = 5f
        //座標外周円
        canvas.drawCircle(
            (250 * scale + posX) + (((Location.GPS_X!! - Current.MAP_X!!) * 10000).toInt()) * scale - scale / 2,
            (250 * scale + posY) + (((Current.MAP_Y!! - Location.GPS_Y!!) * 10000).toInt()) * scale - scale / 2,
            Location.GPS_A!! / 10 * scale,
            Circle
        )
        Circle.strokeWidth = 2f
        //座標円周円
        canvas.drawCircle(
            (250 * scale + posX) + (((Location.GPS_X!! - Current.MAP_X!!) * 10000).toInt()) * scale - scale / 2,
            (250 * scale + posY) + (((Current.MAP_Y!! - Location.GPS_Y!!) * 10000).toInt()) * scale - scale / 2,
            anim_ringR,
            Circle
        )
        if (anim_ringR + (Location.GPS_A!! / 10 * scale)/20 <= Location.GPS_A!! / 10 * scale) anim_ringR += (Location.GPS_A!! / 10 * scale)/20
        else anim_ringR = -50f
    }

    fun Loading(canvas:Canvas?,size:Rect?){
        val Circle = Paint()
        Circle.color = Color.parseColor("#FF0000")
        Circle.isAntiAlias = true
        Circle.strokeWidth = 3f
        //中心円
        canvas!!.drawCircle(size!!.width() / 2f, size.height() / 4 * 3f, 25f, Circle)
        //外周円
        canvas.drawCircle(
            size.width() / 2f + cos(anim_ringR) * 150f,
            size.height() / 4 * 3f + sin(anim_ringR) * 150f,
            20f, Circle
        )
        canvas.drawCircle(
            size.width() / 2f + cos(anim_ringR/-2) * 150f,
            size.height() / 4 * 3f + sin(anim_ringR/-2) * 150f,
            20f, Circle
        )
        Circle.style = Paint.Style.STROKE
        //円周円
        canvas.drawCircle(size.width() / 2f, size.height() / 4 * 3f, 150f, Circle)
        anim_ringR += 0.2f
        if (anim_ringR >= 640) anim_ringR = 0f
    }

    fun Log(posX:Int,posY:Int,scale:Float,Location: MyApp.GPSDATA,Current:MyApp.MAPDATA,canvas:Canvas?){

        val GLOBAL= MyApp.getInstance()
        var paint=Paint()
        paint.strokeWidth=10f
        paint.color=Color.parseColor("#FF0000")

        //レコードが0件の場合は終了
        if(GLOBAL.GPS_LOG.size<=0)return

        //GPSレコードの件数分繰り返す
        for(i in  0..GLOBAL.GPS_LOG.size-2){
            val startX=(250 * scale + posX) + (((GLOBAL.GPS_LOG[i].GPS_X!! - Current.MAP_X!!) * 10000).toInt()) * scale - scale / 2
            val startY=(250 * scale + posY) + (((Current.MAP_Y!! - GLOBAL.GPS_LOG[i].GPS_Y!!) * 10000).toInt()) * scale - scale / 2
            val stopX=(250 * scale + posX) + (((GLOBAL.GPS_LOG[i+1].GPS_X!! - Current.MAP_X!!) * 10000).toInt()) * scale - scale / 2
            val stopY=(250 * scale + posY) + (((Current.MAP_Y!! - GLOBAL.GPS_LOG[i+1].GPS_Y!!) * 10000).toInt()) * scale - scale / 2
            canvas!!.drawLine(startX, startY, stopX, stopY,paint)

            val sX=(250 * scale + posX) + (((GLOBAL.GPS_LOG[i].GPS_X!! - Current.MAP_X!!) * 10000).toInt()) * scale
            val sY=(250 * scale + posY) + (((Current.MAP_Y!!-GLOBAL.GPS_LOG[i].GPS_Y!!) * 10000).toInt()) * scale
            val rect=Rect((sX-scale).toInt(),(sY-scale).toInt(),sX.toInt(),sY.toInt())
            canvas.drawRect(rect,paint)
        }

    }
}
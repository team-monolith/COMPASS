package com.monolith.compass.ui.fitness

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.fragment.app.Fragment

class CanvasDraw : Fragment(){

    var anim_meter:Int=0


    fun meter(height:Int,width:Int,Current:Int,Target:Int,canvas: Canvas?){

        val paint = Paint()

        //メーター内の表示
        paint.color = Color.parseColor("#00FF00")
        val rect= Rect(0,height/6*5,anim_meter,height)
        canvas!!.drawRect(rect,paint)

        if( anim_meter+5 <= (Current/Target)*width )anim_meter+=50

        //メータの枠の表示
        paint.color=Color.parseColor("#000000")
        paint.strokeWidth=5f
        canvas.drawLine(0f,height/6*5f,width.toFloat(),height/6*5f,paint)
        canvas.drawLine(0f,height.toFloat()-(paint.strokeWidth/2),width.toFloat(),height.toFloat()-(paint.strokeWidth/2),paint)
    }

}
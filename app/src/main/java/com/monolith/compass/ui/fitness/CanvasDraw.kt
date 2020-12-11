package com.monolith.compass.ui.fitness

import android.content.Context
import android.graphics.*
import androidx.fragment.app.Fragment


class CanvasDraw : Fragment() {

    var anim_meter: Int = -50

    var anim_walk: Int = 0
    var anim_walk_count:Int=0


    fun meter(height: Int, width: Int, Current: Int, Target: Int, canvas: Canvas?) {

        val paint = Paint()

        //メーター内の表示
        paint.color = Color.parseColor("#00FF00")
        val rect = Rect(0, height / 6 * 5, anim_meter, height)
        canvas!!.drawRect(rect, paint)

        if (anim_meter < (Current.toFloat() / Target.toFloat()) * width) anim_meter += 10

        //メータの枠の表示
        paint.color = Color.parseColor("#000000")
        paint.strokeWidth = 5f
        canvas.drawLine(0f, height / 6 * 5f, width.toFloat(), height / 6 * 5f, paint)
        canvas.drawLine(
            0f,
            height.toFloat() - (paint.strokeWidth / 2),
            width.toFloat(),
            height.toFloat() - (paint.strokeWidth / 2),
            paint
        )



    }

    fun human(walker:Array<Bitmap?>,height:Int,width:Int,canvas:Canvas?){
        if(walker[0]!=null){
            val paint=Paint()
            canvas!!.scale(0.5f,0.5f)
            canvas.drawBitmap(walker[anim_walk%3]!!,anim_meter.toFloat()*2-(walker[anim_walk%3]!!.width/2),((height/6*5)*2-walker[anim_walk%3]!!.height).toFloat(),null)
            canvas.scale(1f,1f)
        }

        anim_walk_count+=1
        if(anim_walk_count%8==0){
            anim_walk_count=0
            anim_walk+=1
        }
    }

}
package com.monolith.compass.ui.fitness

import android.graphics.*
import androidx.fragment.app.Fragment
import java.lang.Math.abs
import java.nio.FloatBuffer


class CanvasDraw : Fragment() {

    var anim_meter: Int = -50

    var anim_walk: Int = 0
    var anim_walk_count: Int = 0

    var anim_arrow: Int = -900

    fun anim_reset() {
        anim_meter = -50
        anim_walk = 0
        anim_walk_count = 0
    }


    fun meter(height: Int, width: Int, Current: Int, Target: Int, pos: Int, canvas: Canvas?) {

        val paint = Paint()
        paint.isAntiAlias = true
        //メーター内の表示
        paint.color = Color.parseColor("#00FF00")

        val rect = Rect( 0, (height / 6 * 5)+((abs(pos)*1f/width*1f)*(height/6f)).toInt(),  anim_meter, height)
        canvas!!.drawRect(rect, paint)

        if (anim_meter < (Current.toFloat() / Target.toFloat()) * width) anim_meter += 10
        if (anim_meter > (Current.toFloat() / Target.toFloat()) * width) anim_meter =
            ((Current.toFloat() / Target.toFloat()) * width).toInt()

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

        paint.textSize = 50f

        //相対表現が足りてない（X,Y軸）。余裕があったら再設計
        //文字サイズが50なので25のはずがずれるため20
        //x軸を200引いているのは7桁表示時にtextSize/2*7で余裕がある値
        paint.alpha=(255*(1-abs(pos)*1f/width*1f)).toInt()
        canvas.drawText(
            Target.toString(),
             width - 200f,
            (height + (height / 6 * 5)) / 2f + 20f,
            paint
        )

    }

    fun human(walker: Array<Bitmap?>, height: Int, width: Int, pos: Int, canvas: Canvas?) {
        if (walker[0] != null) {
            val paint = Paint()
            paint.isAntiAlias = true

            canvas!!.drawBitmap(
                walker[anim_walk % 3]!!,
                pos + anim_meter.toFloat() - (walker[anim_walk % 3]!!.width / 2),
                ((height / 6 * 5) - walker[anim_walk % 3]!!.height).toFloat(),
                null
            )

        }

        anim_walk_count += 1
        if (anim_walk_count % 8 == 0) {
            anim_walk_count = 0
            anim_walk += 1
        }
    }

    fun steps(
        height: Int,
        width: Int,
        Current: Int,
        Target: Int,
        walker: Array<Bitmap?>,
        pos: Int,
        canvas: Canvas?
    ) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = 50f
        paint.color = Color.parseColor("#000000")
        var steps =
            (anim_meter / ((Current.toFloat() / Target.toFloat()) * width) * Current).toInt()

        //小数点以下の誤差を修正
        if (abs(steps - Current) <= 100) steps = Current

        canvas!!.drawText(
            steps.toString(),
            pos + anim_meter.toFloat() - 50,
            height / 6 * 5 - walker[0]!!.height - paint.textSize,
            paint
        )
    }

    fun arrow(height: Int, width: Int, tapFlg: Boolean, canvas: Canvas?) {

        //桁落ち対策のためにフロートへ変換
        var w: Float = width.toFloat()

        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#606060")
        paint.strokeWidth = 5f

        paint.alpha = anim_arrow

        //操作中は矢印を表示しない
        if (tapFlg) {
            paint.alpha = 0
            anim_arrow = -900
        }
        //非操作時は矢印をフェードさせる（透過度を遷移させている）
        else {
            if (anim_arrow < 636) {
                if (anim_arrow <= 255) {
                    paint.alpha = anim_arrow
                } else paint.alpha = 255 - (anim_arrow - 255)
                anim_arrow += 6
            } else {
                anim_arrow = 0
                paint.alpha = 0
            }
        }


        // /32*32は同じのはずなのに*1fでは動かない、要検証
        val fb: FloatBuffer = FloatBuffer.allocate(8)

        fb.put(w / 64 * 61f)
        fb.put((height / 6 * 5) / 6f)
        fb.put(w / 64 * 63f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 63f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 61f)
        fb.put((height / 6 * 5) / 6 * 5f)
        canvas!!.drawLines(fb.array(), paint)

        fb.clear()
        fb.put(w / 64 * 60f)
        fb.put((height / 6 * 5) / 6f)
        fb.put(w / 64 * 62f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 62f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 60f)
        fb.put((height / 6 * 5) / 6 * 5f)
        canvas.drawLines(fb.array(), paint)

        fb.clear()
        fb.put(w / 64 * 3f)
        fb.put((height / 6 * 5) / 6f)
        fb.put(w / 64 * 1f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 1f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 3f)
        fb.put((height / 6 * 5) / 6 * 5f)
        canvas.drawLines(fb.array(), paint)

        fb.clear()
        fb.put(w / 64 * 4f)
        fb.put((height / 6 * 5) / 6f)
        fb.put(w / 64 * 2f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 2f)
        fb.put((height / 6 * 5) / 2f)
        fb.put(w / 64 * 4f)
        fb.put((height / 6 * 5) / 6 * 5f)
        canvas.drawLines(fb.array(), paint)

        //線と線の間に隙間が空く問題を解消
        paint.strokeWidth = 2f
        canvas.drawPoint(w / 64 * 63f, (height / 6 * 5) / 2f, paint)
        canvas.drawPoint(w / 64 * 62f, (height / 6 * 5) / 2f, paint)
        canvas.drawPoint(w / 64 * 1f, (height / 6 * 5) / 2f, paint)
        canvas.drawPoint(w / 64 * 2f, (height / 6 * 5) / 2f, paint)
    }

}
package com.monolith.compass.ui.fitness

import android.content.res.Resources
import android.graphics.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.Resource
import com.monolith.compass.R
import java.lang.Math.abs
import java.nio.FloatBuffer
import kotlin.random.Random
import kotlin.random.nextInt


class CanvasDraw : Fragment() {

    var anim_meter: Int = 0

    var anim_walk: Int = 0
    var anim_walk_count: Int = 0

    var anim_arrow: Int = -900

    var anim_graph_month: Int = 0
    var anim_graph_week: Int = -120

    var anim_graphline: Int = -300

    //アニメーション変数リセット関数
    fun anim_reset() {
        anim_meter = -50
        anim_walk = 0
        anim_walk_count = 0
        anim_graph_month = 0
        anim_graph_week = -120
        anim_graphline = -300
    }


    //歩行量メーター表示関数
    fun meter(
        height: Int,
        width: Int,
        Current: Int,
        Target: Int,
        pos: Int,
        canvas: Canvas?
    ) {

        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#00FF00")


        //メーター内ゲージの表示
        //スワイプによって高さが変動する
        val rect = Rect(
            0,
            (height / 6 * 5) + ((abs(pos) * 1f / width * 1f) * (height / 6f)).toInt(),
            anim_meter,
            height
        )
        canvas!!.drawRect(rect, paint)

        //メーターを増加させる処理。最大値を超えた場合は最大値で固定する
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

        paint.alpha = (255 * (1 - abs(pos) * 1f / width * 1f)).toInt()
        canvas.drawText(
            Target.toString(),
            width - paint.measureText(Target.toString()) - 20f,
            (height + (height / 6 * 5)) / 2f + 20f,
            paint
        )

    }

    //人間のアニメーションを表示する関数
    fun human(
        walker: Array<Bitmap?>,
        joyful: Array<Bitmap?>,
        height: Int,
        width: Int,
        Current: Int,
        Target: Int,
        pos: Int,
        canvas: Canvas?
    ) {

        //atache後であることを念のため確認。リソース抜け時クラッシュを回避するため要記載
        if (walker[5] != null && joyful[10] != null) {
            val paint = Paint()
            paint.isAntiAlias = true

            //表示上の歩数を管理する変数
            var steps =
                (anim_meter / ((Current.toFloat() / Target.toFloat()) * width) * Current).toInt()

            //小数点以下の誤差を修正
            if (abs(steps - Current) <= 1) steps = Current

            var meter = anim_meter
            if (anim_meter >= width - (walker[0]!!.width / 2)) {
                meter = width - (walker[0]!!.width / 2)
            }

            if (steps < Target) {
                canvas!!.drawBitmap(
                    walker[anim_walk % 6]!!,
                    pos + meter.toFloat() - (walker[0]!!.width / 2),
                    ((height / 6 * 5) - walker[0]!!.height).toFloat(),
                    null
                )
            } else {
                canvas!!.drawBitmap(
                    joyful[anim_walk % 11]!!,
                    pos + meter.toFloat() - (joyful[0]!!.width / 2),
                    ((height / 6 * 5) - joyful[0]!!.height).toFloat(),
                    null
                )
            }

        }

        //フレームごとに加算し8で割り切れる数だった場合は次のデザインに変える
        anim_walk_count += 1
        if (anim_walk_count % 4 == 0) {
            anim_walk_count = 0
            anim_walk += 1
        }
    }

    //人間の上に出る歩数表示用関数
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

        //表示上の歩数を管理する変数
        var steps =
            (anim_meter / ((Current.toFloat() / Target.toFloat()) * width) * Current).toInt()

        //小数点以下の誤差を修正
        if (abs(steps - Current) <= 1) steps = Current

        var meter = anim_meter
        if (anim_meter >= width - (walker[0]!!.width / 2)) {
            meter = width - (walker[0]!!.width / 2)
        }

        canvas!!.drawText(
            steps.toString(),
            pos + meter.toFloat() - paint.measureText(steps.toString()) / 3 * 2,
            (height / 6 * 5f) - walker[0]!!.height,
            paint
        )

    }

    //スワイプ用の左右矢印表示関数
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

        //矢印に必要な座標を指定し描画
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

    fun monthgraph(
        step: MutableList<Int>,
        target: Int,
        height: Int,
        width: Int,
        pos: Int,
        canvas: Canvas?
    ) {
        val day = step.size
        val paint = Paint()
        val line = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#00FF00")
        line.color = Color.parseColor("#000000")
        line.strokeWidth = 3f
        //5本目から描画するため+5する
        //月の日数が何日であれ動的に対応できるようにday+10で左右5本マージンで作成
        for (i in 0 until day) {

            var top: Float = 0f
            if (anim_graph_month - (i * 30) <= ((height / 3f * 2) * (step[i] * 1f / target * 1f))) {
                top = height - anim_graph_month + (i * 30).toFloat()
            } else {
                top = height - ((height / 3f * 2) * (step[i] * 1f / target * 1f))
            }

            val rect = RectF(
                pos + (width * 1f / (day + 10) * (i + 5)),
                top,
                pos + (width * 1f / (day + 10) * (i + 6)),
                height.toFloat()
            )
            canvas!!.drawRect(rect, paint)
            //左
            canvas.drawLine(
                pos + (width * 1f / (day + 10) * (i + 5)),
                top,
                pos + (width * 1f / (day + 10) * (i + 5)),
                width * 1f,
                line
            )
            //上
            canvas.drawLine(
                pos + (width * 1f / (day + 10) * (i + 5)),
                top,
                pos + (width * 1f / (day + 10) * (i + 6)),
                top,
                line
            )
            //右
            canvas.drawLine(
                pos + (width * 1f / (day + 10) * (i + 6)),
                top,
                pos + (width * 1f / (day + 10) * (i + 6)),
                height * 1f,
                line
            )
        }

        if (anim_graph_month < target * 2) anim_graph_month += 30


        //下線の表示
        canvas!!.drawLine(
            pos + (width * 1f / (day + 10) * 5),
            height * 1f,
            pos + (width * 1f / (day + 10) * (day + 5)),
            height * 1f,
            line
        )

        paint.color = Color.parseColor("#000000")
        paint.textSize = 30f
        paint.strokeWidth = 3f
        paint.alpha = (255 * (1 - abs(pos) * 1f / width * 1f)).toInt()
        //目標値の表示
        //ここをフェード表示するように修正
        canvas.drawText(target.toString(), 15f, (height / 3f) - 6f, paint)
        //目標ラインの表示
        if (anim_graphline > 0) canvas.drawLine(
            15f,
            (height / 3).toFloat(),
            anim_graphline * 1f,
            (height / 3).toFloat(),
            paint
        )
        if (anim_graphline + 30 <= width - 15) anim_graphline += 30
        else anim_graphline = width - 15
    }

    fun weekgraph(
        step: MutableList<Int>,
        target: Int,
        height: Int,
        width: Int,
        pos: Int,
        canvas: Canvas?
    ) {
        val day = step.size
        val paint = Paint()
        val line = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#00FF00")
        line.color = Color.parseColor("#000000")
        line.strokeWidth = 3f
        //5本目から描画するため+5する
        //一週間は7日のため7回ループ
        for (i in 0 until 7) {

            var top: Float = 0f
            if (anim_graph_week - (i * 30) <= ((height / 3f * 2) * (step[i] * 1f / target * 1f))) {
                top = height - anim_graph_week + (i * 30).toFloat()
            } else {
                top = height - ((height / 3f * 2) * (step[i] * 1f / target * 1f))
            }

            val rect = RectF(
                pos + (width * 1f / 19 * (i * 2 + 3)),
                top,
                pos + (width * 1f / 19 * (i * 2 + 4)),
                height.toFloat()
            )
            canvas!!.drawRect(rect, paint)
            //左
            canvas.drawLine(
                pos + (width * 1f / 19 * (i * 2 + 3)),
                top,
                pos + (width * 1f / 19 * (i * 2 + 3)),
                width * 1f,
                line
            )
            //上
            canvas.drawLine(
                pos + (width * 1f / 19 * (i * 2 + 3)),
                top,
                pos + (width * 1f / 19 * (i * 2 + 4)),
                top,
                line
            )
            //右
            canvas.drawLine(
                pos + (width * 1f / 19 * (i * 2 + 4)),
                top,
                pos + (width * 1f / 19 * (i * 2 + 4)),
                height * 1f,
                line
            )
        }

        if (anim_graph_week < target * 2) anim_graph_week += 30


        //下線の表示
        canvas!!.drawLine(
            pos + (width * 1f / 19 * (0 * 2 + 3)),
            height * 1f,
            pos + (width * 1f / 19 * (6 * 2 + 4)),
            height * 1f,
            line
        )

        paint.color = Color.parseColor("#000000")
        paint.textSize = 30f
        paint.strokeWidth = 3f
        paint.alpha = (255 * (1 - abs(pos) * 1f / width * 1f)).toInt()
        //目標値の表示
        //ここをフェード表示するように修正
        canvas.drawText(target.toString(), 15f, (height / 3f) - 6f, paint)
        //目標ラインの表示
        if (anim_graphline > 0) canvas.drawLine(
            15f,
            (height / 3).toFloat(),
            anim_graphline * 1f,
            (height / 3).toFloat(),
            paint
        )
        if (anim_graphline + 30 <= width - 15) anim_graphline += 60
        else anim_graphline = width - 15
    }

    fun CreateBack(height: Int, width: Int, res: Resources): Bitmap {
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()

        val nature = arrayOf(
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.nature_tree1),
                height / 2,
                height / 2,
                true
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.nature_tree2),
                height / 2,
                height / 2,
                true
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.nature_grass),
                height / 8,
                height / 8,
                true
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.nature_flower),
                height / 8,
                height / 8,
                true
            )
        )

        //枠の最下部、これより上に物を描画する
        canvas.drawLine(0f, height / 6 * 5f, width.toFloat(), height / 6 * 5f, paint)



        if (Random.nextInt(3) == 0) canvas.drawBitmap(
            nature[0],
            Random.nextInt(0..(width - nature[0].height)) * 1f,
            height / 6 * 5f - nature[0].height,
            paint
        )

        if (Random.nextInt(3) == 0) canvas.drawBitmap(
            nature[1],
            Random.nextInt(0..(width - nature[1].height)) * 1f,
            height / 6 * 5f - nature[1].height,
            paint
        )

        for (i in 0..Random.nextInt(4)) {
            canvas.drawBitmap(
                nature[2],
                Random.nextInt(0..(width - nature[2].height)) * 1f,
                height / 6 * 5f - nature[2].height,
                paint
            )
        }

        for (i in 0..Random.nextInt(3)) {
            canvas.drawBitmap(
                nature[3],
                Random.nextInt(0..(width - nature[3].height)) * 1f,
                height / 6 * 5f - nature[3].height,
                paint
            )
        }

        return output
    }

}
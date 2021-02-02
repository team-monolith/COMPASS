package com.monolith.compass.ui.fitness

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.util.*

class DayFragment : Fragment() {


    private val GLOBAL = MyApp.getInstance()    //グローバル変数宣言用

    val Draw = CanvasDraw()

    val handler = Handler()//メインスレッド処理用ハンドラ

    var height: Int = 0
    var width: Int = 0

    var moveview: DayFragment.MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var walker: Array<Bitmap?> = arrayOfNulls(6)

    var joyful: Array<Bitmap?> = arrayOfNulls(16)

    var scene: Array<Bitmap?> = arrayOfNulls(3)

    var sky: Bitmap? = null

    var posX: Int = 0  //表示座標管理用
    var logX: Int = 0  //タップ追従用

    var tapFlg: Boolean = false

    var accelerator: Int = 0

    var prevDate: Date = Date()

    var step: Int = 0
    var target: Int = 0

    var scene_sun: Bitmap? = null

    var todayFlg:Boolean=false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blank, container, false)
        val layout = view.findViewById<ConstraintLayout>(R.id.blanklayout)
        moveview = MoveView(this.activity)

        layout.addView(moveview)
        layout.setWillNotDraw(false)

        //フラグメントサイズの取得
        view.post(Runnable { // for instance
            height = view.measuredHeight
            width = view.measuredWidth

            scene = arrayOf(
                Draw.CreateBack(height, width, resources),
                Draw.CreateBack(height, width, resources),
                Draw.CreateBack(height, width, resources)
            )

            sky = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.nature_back
                ), (height / 6 * 5) / 6 * 16, height / 6 * 5, true
            )

            scene_sun = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.nature_sun
                ), height / 4, height / 4, true
            )
        })
        return view
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //アニメーションの実行
        HandlerDraw(moveview!!)

        //ビューにリスナーを設定
        view.setOnTouchListener { _, event ->
            onTouch(view, event)
        }

        setDate(0)
        (parentFragment as FitnessFragment).DataSet(prevDate)
    }


    //引数に指定された日付を変動させる関数

    fun setDate(Direction: Int) {

        step = 0

        //calendarのインスタンスを生成し引数日動かす
        val cl = Calendar.getInstance()
        cl.time = prevDate
        cl.add(Calendar.DAY_OF_YEAR, Direction)
        val today=Calendar.getInstance()
        today.time=Date()



        //時刻データを破棄
        cl.clear(Calendar.MINUTE)
        cl.clear(Calendar.SECOND)
        cl.clear(Calendar.MILLISECOND)
        cl.set(Calendar.HOUR_OF_DAY, 0)
        today.clear(Calendar.MINUTE)
        today.clear(Calendar.SECOND)
        today.clear(Calendar.MILLISECOND)
        today.set(Calendar.HOUR_OF_DAY, 0)


        todayFlg = cl.compareTo(today)==0

        //calendar型からdate型に変換
        prevDate = cl.time

        for (i in GLOBAL.ACTIVITY_LOG.indices) {
            //データとして存在する場合は値を取得
            if (prevDate == GLOBAL.ACTIVITY_LOG[i].DATE) {
                step = GLOBAL.ACTIVITY_LOG[i].STEP
                target = GLOBAL.ACTIVITY_LOG[i].TARGET
                break
            }
            //最後までフォルダを参照しても存在しない場合は0をセットする
            else if (i == GLOBAL.ACTIVITY_LOG.lastIndex) {
                step = 0
                target = GLOBAL.LocalSettingRead("LOCAL.txt").TARGET
            }
        }

        //FitnessFragment管轄のレイアウトに変更の命令を出す
        if (parentFragment != null) {
            (parentFragment as FitnessFragment).DataSet(prevDate)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        walker = arrayOf(
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.walk_1
                ), 256, 256, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.walk_2
                ), 256, 256, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.walk_3
                ), 256, 256, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.walk_4
                ), 256, 256, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.walk_5
                ), 256, 256, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.walk_6
                ), 256, 256, false
            )
        )

        joyful = arrayOf(
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun01
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun02
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun03
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun04
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun05
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun06
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun07
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun08
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun09
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun10
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun11
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun12
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun13
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun14
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun15
                ), 256, 512, false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    com.monolith.compass.R.drawable.fun16
                ), 256, 512, false
            )

        )

    }

    //タッチイベント実行時処理
    fun onTouch(view: View, event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                logX = event.x.toInt()
                tapFlg = true
            }
            MotionEvent.ACTION_MOVE -> {
                posX += event.x.toInt() - logX
                logX = event.x.toInt()
                tapFlg = true
            }
            MotionEvent.ACTION_UP -> {
                tapFlg = false
            }
        }

        return true
    }

    //描画関数　再描画用
    fun HandlerDraw(mv: DayFragment.MoveView) {
        handler.post(object : Runnable {
            override fun run() {
                //変数類再設定
                SystemReflesh()
                //再描画
                mv.invalidate()
                handler.postDelayed(this, 25)
            }
        })
    }

    fun SystemReflesh() {

        //タップされておらず、画面がずれている場合
        if (!tapFlg && posX != 0) {
            //右から左にスワイプ
            if (posX < 0) {
                //画面の1/3以上スワイプ時
                if (posX < width / -3) {
                    accelerator += 10
                    posX -= accelerator
                    //画面遷移完了時
                    if (posX <= -width) {
                        setDate(1)
                        posX = 0
                        scene[1] = scene[2]
                        scene[2] = Draw.CreateBack(height, width, resources)
                        Draw.anim_reset()
                    }
                }
                //1/3未満のスワイプの場合は元に戻す
                else {
                    accelerator += 10
                    posX += accelerator
                    if (posX >= 0) posX = 0
                }
            }
            //左から右にスワイプ
            if (posX > 0) {
                //画面の1/3以上スワイプ時
                if (posX > width / 3) {
                    accelerator += 10
                    posX += accelerator
                    //画面遷移完了時
                    if (posX >= width) {
                        setDate(-1)
                        posX = 0
                        scene[1] = scene[0]
                        scene[0] = Draw.CreateBack(height, width, resources)
                        Draw.anim_reset()
                    }
                }
                //1/3未満のスワイプの場合は元に戻す
                else {
                    accelerator += 10
                    posX -= accelerator
                    if (posX <= 0) posX = 0
                }
            }
            if (posX == 0) accelerator = 0
        }

    }

    inner class MoveView : View {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)

            val paint = Paint()

            canvas!!.save()

            if (sky != null) {
                canvas.drawBitmap(sky!!, 0f, 0f, paint)
            }

            if (scene_sun != null) canvas.drawBitmap(
                scene_sun!!,
                width / 5 * 4 - scene_sun!!.width / 2f,
                scene_sun!!.height / 5f,
                paint
            )

            if (scene[2] != null) {
                canvas.drawBitmap(scene[0]!!, posX * 1f - width, 0f, Paint())
                canvas.drawBitmap(scene[1]!!, posX * 1f, 0f, Paint())
                canvas.drawBitmap(scene[2]!!, posX * 1f + width, 0f, Paint())
            }

            Draw.arrow(height, width, tapFlg, canvas)
            Draw.meter(height, width, step, target, posX, canvas)
            Draw.steps(height, width, step, target, walker, posX, canvas)
            Draw.human(walker, joyful, height, width,step, target, posX,todayFlg, canvas)

        }
    }

}

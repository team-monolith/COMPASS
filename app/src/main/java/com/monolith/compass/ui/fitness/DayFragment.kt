package com.monolith.compass.ui.fitness

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
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

    private lateinit var dayViewModel: FitnessViewModel

    private val GLOBAL = MyApp.getInstance()    //グローバル変数宣言用

    val Draw = CanvasDraw()

    val handler = Handler()//メインスレッド処理用ハンドラ

    var height: Int = 0
    var width: Int = 0

    var moveview: DayFragment.MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var walker: Array<Bitmap?> = arrayOfNulls(3)

    var posX: Int = 0  //表示座標管理用
    var logX: Int = 0  //タップ追従用

    var tapFlg: Boolean = false

    var accelerator: Int = 0

    var prevDate: Date = Date()
    var prevCount: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dayViewModel =
            ViewModelProvider(this).get(FitnessViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_blank, container, false)
        val layout = view.findViewById<ConstraintLayout>(R.id.blanklayout)
        moveview = MoveView(this.activity)

        layout.addView(moveview)
        layout.setWillNotDraw(false)

        //フラグメントサイズの取得
        view.post(Runnable { // for instance
            height = view.measuredHeight
            width = view.measuredWidth
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

        prevCount = GLOBAL.STEP_LOG.lastIndex
        setDate(0)
        (parentFragment as FitnessFragment).DataSet(prevDate)
    }

    fun setDate(Direction: Int) {

        val cl = Calendar.getInstance()
        cl.time = prevDate
        cl.add(Calendar.DATE, Direction)

        cl.clear(Calendar.MINUTE)
        cl.clear(Calendar.SECOND)
        cl.clear(Calendar.MILLISECOND)
        cl.set(Calendar.HOUR_OF_DAY, 0)

        prevDate=cl.time

        for(i in GLOBAL.STEP_LOG.indices){
            if(prevDate==GLOBAL.STEP_LOG[i].DATE){
                prevCount=i
                return
            }
        }
        prevCount=null
        return
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        walker = arrayOf(
            BitmapFactory.decodeResource(resources, com.monolith.compass.R.drawable.walk1),
            BitmapFactory.decodeResource(resources, com.monolith.compass.R.drawable.walk2),
            BitmapFactory.decodeResource(resources, com.monolith.compass.R.drawable.walk3)
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
                posX += event.x.toInt() - logX!!
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
                        Draw.anim_reset()
                        (parentFragment as FitnessFragment).DataSet(prevDate)
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
                        Draw.anim_reset()
                        (parentFragment as FitnessFragment).DataSet(prevDate)
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

            var STEP = 0
            var TARGET = 33333
            //存在しない場合は0を表示する
            if (prevCount != null) {
                STEP = GLOBAL.STEP_LOG[prevCount!!].STEP
                TARGET = GLOBAL.STEP_LOG[prevCount!!].TARGET
            }

            Draw.arrow(height, width, tapFlg, canvas)
            Draw.meter(height, width, STEP, TARGET, posX, canvas)
            Draw.steps(height, width, STEP, TARGET, walker, posX, canvas)
            Draw.human(walker, height, width, posX, canvas)

        }
    }

}
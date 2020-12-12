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

class MonthFragment : Fragment() {

    val Draw = CanvasDraw()

    val handler = Handler()//メインスレッド処理用ハンドラ

    var height: Int = 0
    var width: Int = 0

    var moveview: MonthFragment.MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var walker: Array<Bitmap?> = arrayOfNulls(3)

    var posX: Int = 0  //表示座標管理用
    var logX: Int = 0  //タップ追従用

    var tapFlg: Boolean = false

    var accelerator: Int = 0

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

        when {
            event.action == MotionEvent.ACTION_DOWN -> {
                logX = event.x.toInt()
                tapFlg = true
            }
            event.action == MotionEvent.ACTION_MOVE -> {
                posX += event.x.toInt() - logX!!
                logX = event.x.toInt()
                tapFlg = true
            }
            event.action == MotionEvent.ACTION_UP -> {
                tapFlg = false
            }
        }

        return true
    }

    //描画関数　再描画用
    fun HandlerDraw(mv: MonthFragment.MoveView) {
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
                        posX = 0
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
                        posX = 0
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
            //Draw.arrow(height, width, tapFlg, canvas)
            //Draw.meter(height, width, 10000, 12000, posX, canvas)
            //Draw.steps(height, width, 10000, 12000, walker, posX, canvas)
            //Draw.human(walker, height, width, posX, canvas)
            var step=arrayOf(100,400,200,600,200,100,600,800,800,900,
                100,400,412,679,987,634,234,346,786,124,
            912,256,345,685,356,372,234,562,211,356,938)
        }
    }
}
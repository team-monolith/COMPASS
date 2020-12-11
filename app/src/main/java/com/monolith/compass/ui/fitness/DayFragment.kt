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
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R

class DayFragment : Fragment() {

    private lateinit var dayViewModel: FitnessViewModel

    val Draw = CanvasDraw()

    val handler = Handler()//メインスレッド処理用ハンドラ

    var height: Int = 0
    var width: Int = 0

    var moveview: DayFragment.MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var walker: Array<Bitmap?> = arrayOfNulls(3)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dayViewModel =
            ViewModelProvider(this).get(FitnessViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_day, container, false)
        val layout = view.findViewById<ConstraintLayout>(R.id.constDay)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        HandlerDraw(moveview!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        walker = arrayOf(
            BitmapFactory.decodeResource(resources, com.monolith.compass.R.drawable.walk1),
            BitmapFactory.decodeResource(resources, com.monolith.compass.R.drawable.walk2),
            BitmapFactory.decodeResource(resources, com.monolith.compass.R.drawable.walk3)
        )

    }

    //描画関数　再描画用
    fun HandlerDraw(mv: DayFragment.MoveView) {
        handler.post(object : Runnable {
            override fun run() {
                //再描画
                mv.invalidate()
                handler.postDelayed(this, 25)
            }
        })
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
            Draw.meter(height,width,7000,10000,canvas)
            Draw.human(walker,height,width,canvas)
        }
    }
}
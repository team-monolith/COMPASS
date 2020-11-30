package com.monolith.compass.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import com.monolith.compass.ui.setting.SettingFragment
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random


class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    private lateinit var mScaleDetector: ScaleGestureDetector

    val handler = Handler()//メインスレッド処理用ハンドラ

    var moveview: MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var scale: Float = 20F   //地図表示のスケール
    var posX: Int = 0    //地図表示の相対X座標
    var posY: Int = 0    //地図表示の絶対Y座標
    var logX: Int? = null  //タップ追従用X座標
    var logY: Int? = null  //タップ追従用Y座標

    var MAP = Array(500, { arrayOfNulls<Int>(500) }) //地図データ保持用変数


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val layout = view.findViewById<ConstraintLayout>(R.id.constmap)
        moveview = MoveView(this.activity)
        layout.addView(moveview)
        layout.setWillNotDraw(false)

        maploadtest()

        getMapData()

        HandlerDraw(moveview!!)

        return view
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab_current = view.findViewById<FloatingActionButton>(R.id.fab_current)

        view.findViewById<FloatingActionButton>(R.id.fabBig).setOnClickListener {
            scale += 5
            HandlerDraw(moveview!!)
        }
        view.findViewById<FloatingActionButton>(R.id.fabSmall).setOnClickListener {
            if (scale > 5) scale -= 5
            HandlerDraw(moveview!!)
        }
        fab_current.setOnClickListener {
            posX = -2500
            posY = -2500
            HandlerDraw(moveview!!)
        }

        //ビューにリスナーを設定
        view.setOnTouchListener { _, event ->
            mScaleDetector.onTouchEvent(event)
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    logX = event.x.toInt()
                    logY = event.y.toInt()
                }
                event.action == MotionEvent.ACTION_MOVE -> {
                    posX += event.x.toInt() - logX!!
                    posY += event.y.toInt()!! - logY!!
                    logX = event.x.toInt()
                    logY = event.y.toInt()
                    HandlerDraw(moveview!!)
                }
            }
            true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mScaleDetector = ScaleGestureDetector(context,
                object : OnScaleGestureListener {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        // ピンチイン・アウト中に継続して呼び出される
                        // getScaleFactor()は『今回の2点タッチの距離/前回の2点タッチの距離』を返す
                        scale *= detector.scaleFactor
                        return true
                    }

                    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                        Log.d("MyView", "MyView.onScaleBegin")
                        return true
                    }

                    override fun onScaleEnd(detector: ScaleGestureDetector) {
                        Log.d("MyView", "MyView.onScaleEnd")
                    }
                })
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement OnArticleSelectedListener.")
        }
    }


    //マップ情報仮読み込み関数
    fun maploadtest() {
        for (y in 0 until 500) {
            for (x in 0 until 500) {
                MAP[y][x] = 3
            }
        }
    }

    //描画関数　再描画用
    fun HandlerDraw(mv: MoveView) {
        handler.post(object : Runnable {
            override fun run() {
                mv.invalidate()
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
                    canvas!!.drawRect(rect, paint[MAP[y][x]!!])
                }
            }

        }
    }

    fun getMapData() {

        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("data", "monolith")

        "https://ky-server.net/~monolith/system/dev/test.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        setMap(String(response.data))
                        HandlerDraw(MoveView(this.activity))
                    }
                    is Result.Failure -> {
                    }
                }
            }

    }

    fun setMap(data: String) {
        val str = data.replace("\r\n", ",")
        val scan = Scanner(str)
        scan.useDelimiter(",")
        val n = 0
        for (fy in 0 until 500) {
            for (fx in 0 until 500) {
                if (scan.hasNextInt()) MAP[fy][fx] = scan.nextInt()
            }
        }
    }

    //ここの読み込みから

}
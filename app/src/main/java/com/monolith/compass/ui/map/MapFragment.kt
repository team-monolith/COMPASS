package com.monolith.compass.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.TextView
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

    var scale: Float = 30F   //地図表示のスケール
    var posX: Int = 0    //地図表示の相対X座標
    var posY: Int = 0    //地図表示の絶対Y座標
    var logX: Int? = null  //タップ追従用X座標
    var logY: Int? = null  //タップ追従用Y座標

    var centerX: Int = 0 //地図の中心点（現在地）の計算用
    var centerY: Int = 0 //地図の中心点（現在地）の計算用

    var CurrentMAP = Array(500, { arrayOfNulls<Int>(500) }) //現在地周辺地図データ保持用変数
    var OtherMAP=Array(500, { arrayOfNulls<Int>(500) }) //現在地以外の地図データ保持用変数

    var size: Rect? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //最優先でデータをダウンロード
        mapReset()
        getMapData()

        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val layout = view.findViewById<ConstraintLayout>(R.id.constmap)
        moveview = MoveView(this.activity)

        layout.addView(moveview)
        layout.setWillNotDraw(false)

        return view
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //FABボタンID取得
        val fab_current=view.findViewById<FloatingActionButton>(R.id.fab_current)

        //画面サイズ取得
        size = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(size)

        //画面の中心座標を取得
        centerX = (-250 * scale + (size!!.width() + scale) / 2).toInt()
        centerY = (-250 * scale + (size!!.height()) / 4 * 3 + (scale / 2)).toInt()

        //表示座標を中心に設定
        posX=centerX
        posY=centerY

        //FABボタンを水色に変更
        view.findViewById<FloatingActionButton>(R.id.fab_current).setColorFilter(Color.parseColor("#00AAFF"))

        //アニメーションループを再生
        HandlerDraw(moveview!!)


        //ビューにリスナーを設定
        view.setOnTouchListener { _, event ->
            onTouch(view,event)
        }

        //FABボタンリスナー
        fab_current.setOnClickListener {
            posX = centerX
            posY = centerY
            fab_current.setColorFilter(Color.parseColor("#00AAFF"))
            HandlerDraw(moveview!!)
        }

    }

    //タッチイベント実行時処理
    fun onTouch(view:View,event:MotionEvent):Boolean{

        //複数本タッチの場合はピンチ処理
        if (event.pointerCount > 1) {
            mScaleDetector.onTouchEvent(event)
        }

        //一本指タッチの場合は画面移動処理
        else {
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
                }
            }
        }

        //中心にいない場合はFABを黒色にする
        if (!isCenter(posX, posY)) {
            view.findViewById<FloatingActionButton>(R.id.fab_current)
                .setColorFilter(Color.parseColor("#000000"))
        }

        //描画処理
        HandlerDraw(moveview!!)
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mScaleDetector = ScaleGestureDetector(context,
            object : OnScaleGestureListener {

                //スケール変更処理
                override fun onScale(detector: ScaleGestureDetector): Boolean {

                    posX += (scale * scale / 2).toInt()
                    posY += (scale * scale / 2).toInt()

                    //スケールが15未満及び45以上にならないように設定
                    scale *= detector.scaleFactor
                    if (scale <= 15f) {
                        scale = 15f
                        //ここで地図の再ロード処理を記述
                    }
                    if (scale >= 45f) {
                        scale = 45f
                        //ここで地図の再ロード処理を記述
                    }

                    posX -= (scale * scale / 2).toInt()
                    posY -= (scale * scale / 2).toInt()

                    posX += detector.focusX.toInt() - logX!!
                    posY += detector.focusY.toInt() - logY!!
                    logX = detector.focusX.toInt()
                    logY = detector.focusY.toInt()

                    //画面中心に戻すための位置を再計算
                    centerX = (-250 * scale + (size!!.width() + scale) / 2).toInt()
                    centerY = (-250 * scale + (size!!.height()) / 4 * 3 + (scale / 2)).toInt()
                    return true
                }

                //ピンチ開始時処理
                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    logX = detector.focusX.toInt()
                    logY = detector.focusY.toInt()
                    return true
                }

                //ピンチ終了時処理
                override fun onScaleEnd(detector: ScaleGestureDetector) {
                }
            }
        )
    }


    //マップ情報リセット関数
    fun mapReset() {
        for (y in 0 until 500) {
            for (x in 0 until 500) {
                CurrentMAP[y][x] = -1
                OtherMAP[y][x]=-1
            }
        }
    }

    //描画関数　再描画用
    fun HandlerDraw(mv: MoveView) {
        handler.post(object : Runnable {
            override fun run() {
                //再描画
                mv.invalidate()
                //地図データが未完成の場合はリフレッシュし続ける
                if(CurrentMAP[499][499]==-1)handler.postDelayed(this, 1)
            }
        })
    }

    //センター検出用 trueで中心
    fun isCenter(x: Int, y: Int): Boolean {
        return centerX == x && centerY == y
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

            val Circle = Paint()
            Circle.color = Color.parseColor("#FF0000")


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
            canvas!!.drawCircle(size!!.width() / 2f, size!!.height() / 4 * 3f, 25f, Circle)
        }
    }

    //マップ情報ダウンロード関数
    fun getMapData() {
        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("data", "monolith")

        "https://ky-server.net/~monolith/system/dev/test.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        setMap(String(response.data))
                        MoveView(this.activity).invalidate()
                    }
                    is Result.Failure -> {
                        //通信エラー時に返却される
                    }
                }
            }
    }

    //マップを配列に保存する関数
    fun setMap(data: String) {
        val scan = Scanner(data)
        scan.useDelimiter(",|\r\n")
        for (fy in 0 until 500) {
            for (fx in 0 until 500) {
                if (scan.hasNextInt()) CurrentMAP[fy][fx] = scan.nextInt()
            }
        }
    }

}
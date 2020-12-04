package com.monolith.compass.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.cos
import kotlin.math.sin


class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    private lateinit var mScaleDetector: ScaleGestureDetector

    val GLOBAL = MyApp.getInstance()

    val handler = Handler()//メインスレッド処理用ハンドラ

    var moveview: MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var centerFlg = true

    var scale: Float = 30F   //地図表示のスケール
    var posX: Int = 0    //地図表示の相対X座標
    var posY: Int = 0    //地図表示の絶対Y座標
    var logX: Int? = null  //タップ追従用X座標
    var logY: Int? = null  //タップ追従用Y座標

    var centerX: Int = 0 //地図の中心点（現在地）の計算用
    var centerY: Int = 0 //地図の中心点（現在地）の計算用

    var CurrentMAP = Array(500, { arrayOfNulls<Int>(500) }) //現在地周辺地図データ保持用変数
    var OtherMAP = Array(500, { arrayOfNulls<Int>(500) }) //現在地以外の地図データ保持用変数

    var Current_X: Float? = null   //現在地マップの中心座標
    var Current_Y: Float? = null   //現在地マップの中心座標
    var Other_X: Float? = null     //現在地以外のマップの中心座標
    var Other_Y: Float? = null     //現在地以外のマップの中心座標

    var Location_X: Float? = null   //ユーザの現在地のX座標
    var Location_Y: Float? = null   //ユーザの現在地のY座標
    var Location_A: Float? = null   //ユーザの現在地のGPS精度
    var Location_S: Float? = null   //ユーザの現在地の移動速度

    var size: Rect? = null

    var anim_ringR: Float = 1f


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
        val fab_current = view.findViewById<FloatingActionButton>(R.id.fab_current)

        //画面サイズ取得
        size = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(size)

        //画面の中心座標を取得
        centerX = (-250 * scale + (size!!.width() + scale) / 2).toInt()
        centerY = (-250 * scale + (size!!.height()) / 4 * 3 + (scale / 2)).toInt()

        //表示座標を中心に設定
        posX = centerX
        posY = centerY

        //FABボタンを水色に変更
        view.findViewById<FloatingActionButton>(R.id.fab_current)
            .setColorFilter(Color.parseColor("#00AAFF"))

        //アニメーションループを再生
        HandlerDraw(moveview!!)

        //ビューにリスナーを設定
        view.setOnTouchListener { _, event ->
            onTouch(view, event)
        }

        //FABボタンリスナー
        fab_current.setOnClickListener {
            //表示座標を中心に設定
            if (Location_X != null && Current_X != null) {
                posX =
                    (-250 * scale - ((Location_X!! - Current_X!!) * 10000).toInt() * scale + (size!!.width() + scale) / 2).toInt()
                posY =
                    (-250 * scale - ((Location_Y!! - Current_Y!!) * 10000).toInt() * scale + (size!!.height()) / 4 * 3 + (scale / 2)).toInt()
            }
            centerFlg = true
            fab_current.setColorFilter(Color.parseColor("#00AAFF"))
        }

    }

    //タッチイベント実行時処理
    fun onTouch(view: View, event: MotionEvent): Boolean {

        centerFlg = false

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



        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //GPS取得用インターフェース

        //ピンチ処理関係
        mScaleDetector = ScaleGestureDetector(context,
            object : OnScaleGestureListener {

                //スケール変更処理
                override fun onScale(detector: ScaleGestureDetector): Boolean {


                    var LogScale = scale * 500 + scale

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

                    posX += ((LogScale - (scale * 500 + scale)) / 2).toInt()
                    posY += ((LogScale - (scale * 500 + scale)) / 2).toInt()

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

    override fun onDetach() {
        super.onDetach()
    }


    //マップ情報リセット関数
    fun mapReset() {
        for (y in 0 until 500) {
            for (x in 0 until 500) {
                CurrentMAP[y][x] = -1
                OtherMAP[y][x] = -1
            }
        }
    }

    //描画関数　再描画用
    fun HandlerDraw(mv: MoveView) {
        handler.post(object : Runnable {
            override fun run() {
                //変数類再設定
                SystemReflesh()
                //再描画
                mv.invalidate()
                handler.postDelayed(this, 1)
            }
        })
    }

    //変数類をリアルタイムで変更
    fun SystemReflesh() {


        if (Location_X != null && Current_X != null) {
            centerX =
                (-250 * scale - ((Location_X!! - Current_X!!) * 10000).toInt() * scale + (size!!.width() + scale) / 2).toInt()
            centerY =
                (-250 * scale - ((Current_Y!!-Location_Y!!) * 10000).toInt() * scale + (size!!.height()) / 4 * 3 + (scale / 2)).toInt()
        }
        //FABセンターを押下した場合は位置がずれても中心に追従する
        if (centerFlg) {
            view?.findViewById<FloatingActionButton>(R.id.fab_current)
                ?.setColorFilter(Color.parseColor("#0000FF"))
            //地図の中心に自分を置く
            if (Location_X != null && Current_X != null) {
                posX =
                    (-250 * scale - ((Location_X!! - Current_X!!) * 10000).toInt() * scale + (size!!.width() + scale) / 2).toInt()
                posY =
                    (-250 * scale - ((Current_Y!! - Location_Y!!) * 10000).toInt() * scale + (size!!.height()) / 4 * 3 + (scale / 2)).toInt()
            }
        } else {
            view?.findViewById<FloatingActionButton>(R.id.fab_current)
                ?.setColorFilter(Color.parseColor("#000000"))
        }

        //新規GPS情報取得時
        if (GLOBAL.GPS_LOG_X != null) {
            Location_X = GLOBAL.GPS_LOG_X
            Location_Y = GLOBAL.GPS_LOG_Y
            Location_A = GLOBAL.GPS_LOG_A
            Location_S = GLOBAL.GPS_LOG_S
            GLOBAL.GPS_LOG_X=null
            GLOBAL.GPS_LOG_Y=null
            GLOBAL.GPS_LOG_A=null
            GLOBAL.GPS_LOG_S=null
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

            val paint: Array<Paint> = Array<Paint>(5) { Paint() }
            paint[0].color = Color.parseColor("#FFFFFF")
            paint[1].color = Color.parseColor("#CCCCCC")
            paint[2].color = Color.parseColor("#666666")
            paint[3].color = Color.parseColor("#000000")

            val Circle = Paint()
            Circle.color = Color.parseColor("#FF0000")
            Circle.isAntiAlias = true


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

            //現在地等わかっている場合は現在地を表示
            if (Location_X != null && Current_X != null && CurrentMAP[499][499] != -1) {
                val Circle = Paint()

                //GPS誤差が70m以上ある場合は赤表示
                if (Location_A!! < 50f) Circle.color = Color.parseColor("#00FF00")
                else Circle.color = Color.parseColor("#FF0000")

                Circle.isAntiAlias = true
                //座標中心円
                canvas!!.drawCircle(
                    (250 * scale + posX) + (((Location_X!! - Current_X!!) * 10000).toInt()) * scale - scale / 2,
                    (250 * scale + posY) + (((Current_Y!!-Location_Y!!) * 10000).toInt()) * scale - scale / 2,
                    20f,
                    Circle
                )
                Circle.style = Paint.Style.STROKE
                Circle.strokeWidth = 5f
                //座標外周円
                canvas!!.drawCircle(
                    (250 * scale + posX) + (((Location_X!! - Current_X!!) * 10000).toInt()) * scale - scale / 2,
                    (250 * scale + posY) + (((Current_Y!!-Location_Y!!) * 10000).toInt()) * scale - scale / 2,
                    Location_A!! / 10 * scale,
                    Circle
                )
                Circle.strokeWidth = 2f
                //座標円周円
                canvas!!.drawCircle(
                    (250 * scale + posX) + (((Location_X!! - Current_X!!) * 10000).toInt()) * scale - scale / 2,
                    (250 * scale + posY) + (((Current_Y!!-Location_Y!!) * 10000).toInt()) * scale - scale / 2,
                    anim_ringR,
                    Circle
                )
                if (anim_ringR + (Location_A!! / 10 * scale)/20 <= Location_A!! / 10 * scale) anim_ringR += (Location_A!! / 10 * scale)/20
                else anim_ringR = -50f

            }
            //わかっていない場合はローディング表示をする
            else {
                val Circle = Paint()
                Circle.color = Color.parseColor("#FF0000")
                Circle.isAntiAlias = true
                Circle.strokeWidth = 5f
                //中心円
                canvas!!.drawCircle(size!!.width() / 2f, size!!.height() / 4 * 3f, 25f, Circle)
                //外周円
                canvas!!.drawCircle(
                    size!!.width() / 2f + cos(anim_ringR) * 150f,
                    size!!.height() / 4 * 3f + sin(anim_ringR) * 150f,
                    20f, Circle
                )
                Circle.style = Paint.Style.STROKE
                //円周円
                canvas!!.drawCircle(size!!.width() / 2f, size!!.height() / 4 * 3f, 150f, Circle)
                anim_ringR += 0.2f
                if (anim_ringR >= 360) anim_ringR = 0f
            }

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
                        getMapData()
                    }
                }
            }
    }

    //マップを配列に保存する関数
    fun setMap(data: String) {
        val scan = Scanner(data)
        scan.useDelimiter(",|\r\n")

        Current_X = 130.4088f
        Current_Y = 33.5841f


        for (fy in 0 until 500) {
            for (fx in 0 until 500) {
                if (scan.hasNextInt()) CurrentMAP[fy][fx] = scan.nextInt()
            }
        }
    }

    fun WriteFileTest(str:String){
        var buf:String=""
        try{
            val file= File(GLOBAL.DIRECTORY+"/", "GPS.txt")
            val scan= Scanner(file)
            while(scan.hasNextLine()){
                buf+=scan.nextLine()+"\n"
            }
            buf+="\n"+str
            file.writeText(buf)
        }catch(e: FileNotFoundException){
            val file= File(GLOBAL.DIRECTORY+"/", "GPS.txt")
            file.writeText("NEW CREATE FILE")
        }
    }


}
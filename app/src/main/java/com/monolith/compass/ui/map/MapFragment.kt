package com.monolith.compass.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.view.ScaleGestureDetector.OnScaleGestureListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.util.*
import kotlin.collections.HashMap


class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    private lateinit var mScaleDetector: ScaleGestureDetector

    val GLOBAL = MyApp.getInstance()    //グローバル変数呼出用

    val handler = Handler()//メインスレッド処理用ハンドラ

    var moveview: MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var centerFlg = true    //センターボタン押下認識フラッグ
    var layerFlg=true   //レイヤーボタン押下フラッグ、trueでマップ表示

    var scale: Float = 1F   //地図表示のスケール
    var posX: Int = 0    //地図表示の相対X座標
    var posY: Int = 0    //地図表示の絶対Y座標
    var logX: Int? = null  //タップ追従用X座標
    var logY: Int? = null  //タップ追従用Y座標

    var size: Rect? = null  //画面サイズ取得用

    var lastGPSTime:Long=0

    var Current:MyApp.MAPDATA=MyApp.MAPDATA(Array(500) { arrayOfNulls<Int>(500) },null,null,null)//ユーザ現在地周辺の地図データ

    var Other:MyApp.MAPDATA=MyApp.MAPDATA(Array(500) { arrayOfNulls<Int>(500) },null,null,null)//ユーザ現在地周辺の地図データ

    var Location:MyApp.GPSDATA=MyApp.GPSDATA(null,null,null,null,null) //ユーザの現在地

    val Draw = CanvasDraw()//描画処理関係


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

        return view
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //最優先でデータをダウンロード
        mapReset()
        getMapData()

        //FABボタンID取得
        val fab_current = view.findViewById<FloatingActionButton>(R.id.fab_current)
        val fab_layer=view.findViewById<FloatingActionButton>(R.id.fab_layer)

        //画面サイズ取得
        size = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(size)

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
            centerFlg = true
            fab_current.setColorFilter(Color.parseColor("#00AAFF"))
        }
        fab_layer.setOnClickListener{
            layerFlg=!layerFlg
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
                    posY += event.y.toInt() - logY!!
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

                    scale *= detector.scaleFactor


                    posX += detector.focusX.toInt() - logX!!
                    posY += detector.focusY.toInt() - logY!!
                    logX = detector.focusX.toInt()
                    logY = detector.focusY.toInt()

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

    //フラグメント破棄時処理
    override fun onDetach() {
        super.onDetach()

    }

    override fun onPause(){
        super.onPause()
        //GPS情報を次回も使いまわせるように上書き
        //しかし動かぬ
        GLOBAL.GPS_BUF.GPS_X = Location.GPS_X
        GLOBAL.GPS_BUF.GPS_Y = Location.GPS_Y
        GLOBAL.GPS_BUF.GPS_A = Location.GPS_A
        GLOBAL.GPS_BUF.GPS_S = Location.GPS_S
    }



    //マップ情報リセット関数
    fun mapReset() {
        for (y in 0 until 500) {
            for (x in 0 until 500) {
                Current.MAP[y][x] = -1
                Other.MAP[y][x] = -1
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
                handler.postDelayed(this, 25)
            }
        })
    }

    //変数類をリアルタイムで変更
    fun SystemReflesh() {

        //FABセンターを押下した場合は位置がずれても中心に追従する
        if (centerFlg) {
            view?.findViewById<FloatingActionButton>(R.id.fab_current)
                ?.setColorFilter(Color.parseColor("#0000FF"))
            //自分を中心に画面配置
            if (Location.GPS_X != null && Current.MAP_X != null) {


                //画面の中心に移動する処理を作成したら完成

                //座標の中心からのずれを計算
                val sX=((Location.GPS_X!! - Current.MAP_X!!)*10000).toInt()
                val sY=((Current.MAP_Y!! - Location.GPS_Y!!)*10000).toInt()

                posX =
                    (250+sX)*-10+size!!.width()/2
                posY =
                    (250+sY)*-10+size!!.height()/4*3
            }
        } else {
            view?.findViewById<FloatingActionButton>(R.id.fab_current)
                ?.setColorFilter(Color.parseColor("#000000"))
        }

        //新規GPS情報取得時
        if (GLOBAL.GPS_BUF.GPS_X != null&& Current.MAP[499][499]!=null) {
            Location.GPS_X = GLOBAL.GPS_BUF.GPS_X
            Location.GPS_Y = GLOBAL.GPS_BUF.GPS_Y
            Location.GPS_A = GLOBAL.GPS_BUF.GPS_A
            Location.GPS_S = GLOBAL.GPS_BUF.GPS_S
            GLOBAL.GPS_BUF.GPS_X = null
            GLOBAL.GPS_BUF.GPS_Y = null
            GLOBAL.GPS_BUF.GPS_A = null
            GLOBAL.GPS_BUF.GPS_S = null
            //アニメーションのループフラッグを上書き
            Draw.updateGPSFlg()
            //最終取得時刻を上書き
            lastGPSTime=System.currentTimeMillis()
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

            canvas!!.save()

            canvas.translate(posX*1f,posY*1f)

            canvas.scale(scale,scale)

            //マップの表示処理
            if(layerFlg&&Current.BITMAP!=null)canvas.drawBitmap(Current.BITMAP!!,0f,0f,Paint())

            //移動履歴を表示
            if(Current.MAP_X!=null)Draw.Log(Current,canvas)



            //現在地等わかっている場合（前回更新から30秒以下の場合）
            if (Location.GPS_X != null && Current.MAP_X != null && Current.MAP[499][499] != -1&&System.currentTimeMillis()-lastGPSTime<30000) {

                //現在地を表示
                Draw.Current(
                    Location,
                    Current,
                    canvas
                )

                canvas.restore()
            }

            //わかっていない場合はローディング表示をする
            else {
                canvas.restore()
                Draw.Loading(canvas, size)
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
                    }
                    is Result.Failure -> {
                        getMapData()
                    }
                }
            }
    }


    //マップを配列に保存する関数
    fun subsetMap(data: String) {

        val scan = Scanner(data)
        scan.useDelimiter(",|\r\n")

        Current.MAP_X = 130.4088f
        Current.MAP_Y = 33.5841f

        for (fy in 0 until 500) {
            for (fx in 0 until 500) {
                Current.MAP[fy][fx] = scan.nextInt()
            }
        }

        val str="X="+Current.MAP_X+",Y="+Current.MAP_Y+"\n"+data

        MyApp().FileWrite(str,"MAPLOG.txt")
    }

    //マップを配列に保存する関数
    fun setMap(data: String) {

        var origindata:String=data.replace(",","")
        origindata=origindata.replace("\r\n","")

        Current.MAP_X = 130.4088f
        Current.MAP_Y = 33.5841f

        for (fy in 0 until 500) {
            for (fx in 0 until 500) {
                Current.MAP[fy][fx]=origindata.get(fy*500+fx).toString().toInt()
            }
        }

        Current.BITMAP=Draw.Map(Current.MAP)

        val str="X="+Current.MAP_X+",Y="+Current.MAP_Y+"\n"+data

        MyApp().FileWrite(str,"MAPLOG.txt")
    }


}
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
import com.monolith.compass.MainActivity
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
    var multiFlg=false //マルチタッチ検出用フラグ

    var scale: Float = 3F   //地図表示のスケール,1.5-3.0-4.5

    var pos: MyApp.COORDINATE = MyApp.COORDINATE(0f, 0f)//地図表示の絶対座標
    var log: MyApp.COORDINATE = MyApp.COORDINATE(0f, 0f)//タップ追従用座標

    var size: Rect? = null  //画面サイズ取得用

    var lastGPSTime:Long=0

    var Current:MyApp.MAPDATA=MyApp.MAPDATA(Array(500) { arrayOfNulls<Int>(500) },null,null,null,null)//ユーザ現在地周辺の地図データ

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

        (activity as MainActivity).LoadStart()

        return view
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //最優先でデータをダウンロード
        mapReset()
        getMapData(130.4088f,33.5841f,1)

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
            multiFlg=true
            mScaleDetector.onTouchEvent(event)
        }

        //一本指タッチの場合は画面移動処理
        else {
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    multiFlg=false
                    log.X = event.x
                    log.Y = event.y
                }
                event.action == MotionEvent.ACTION_MOVE && !multiFlg -> {
                    pos.X = pos.X!!+event.x - log.X!!
                    pos.Y = pos.Y!!+event.y - log.Y!!
                    log.X = event.x
                    log.Y = event.y
                }
            }
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //ピンチ処理関係
        mScaleDetector = ScaleGestureDetector(context,
            object : OnScaleGestureListener {

                //スケール変更処理
                override fun onScale(detector: ScaleGestureDetector): Boolean {

                    scale *= detector.scaleFactor

                    if(scale<=1.5){
                        scale=1.5f
                    }
                    if(scale>=4.5){
                        scale=4.5f
                    }

                    return true
                }

                //ピンチ開始時処理
                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
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

        (activity as MainActivity).LoadStop()
    }

    //タッチしている場所の座標が、canvas上のどこの座標なのかを返す
    fun TouchCoordinate(touch: MyApp.COORDINATE): MyApp.COORDINATE {
        val x = -pos.X!! + touch.X!!
        val y = -pos.Y!! + touch.Y!!
        return MyApp.COORDINATE(x, y)
    }

    //マップ情報リセット関数
    fun mapReset() {
        for (y in 0 until 500) {
            for (x in 0 until 500) {
                Current.MAP[y][x] = -1
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
            if (Location.GPS_X != null && Current.MAP_X != null&&Location.GPS_Y != null && Current.MAP_Y != null) {

                //座標の中心からのずれを計算
                val sX=((Location.GPS_X!! - Current.MAP_X!!)*10000).toInt()
                val sY=((Current.MAP_Y!! - Location.GPS_Y!!)*10000).toInt()

                pos.X =
                    (((-(5000/2+(sX*10)+5)*scale)+size!!.width()/2))
                pos.Y =
                    (((-(5000/2+(sY*10)+5)*scale)+size!!.height()/4*3))

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

            //画面中心の座標を取得（仮置き）
            val Center: MyApp.COORDINATE = TouchCoordinate(
                MyApp.COORDINATE(
                    size!!.width() / 2f,
                    size!!.height() / 2f
                )
            )

            canvas!!.save()

            canvas.translate(pos.X!!,pos.Y!!)

            //地図の拡大縮小
            canvas.scale(scale, scale)//, Center.X!!,Center.Y!!)

            //マップの表示処理
            if(layerFlg&&Current.BITMAP!=null){
                canvas.drawBitmap(Current.BITMAP!!,0f,0f,Paint())
            }

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

                val DLflg=(Current.MAP[499][499]!=-1)
                val GPSflg=(Location.GPS_X!=null&&System.currentTimeMillis()-lastGPSTime<30000)
                Draw.Loading(canvas,DLflg,GPSflg, size)
            }

        }
    }

    //マップ情報ダウンロード関数
    fun getMapData(POS_X:Float,POS_Y:Float,SCALE:Int) {

        val POSTDATA = HashMap<String, String>()

        POSTDATA.put("load_x",POS_X.toString())
        POSTDATA.put("load_y",POS_Y.toString())
        POSTDATA.put("load_mags",SCALE.toString())

        //https://ky-server.net/~monolith/system/dev/test.php
        //https://a.compass-user.work/system/map/show_csv.php
        "https://a.compass-user.work/system/map/show_csv.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        setMap(String(response.data))
                    }
                    is Result.Failure -> {
                    }
                }
            }
    }

    //マップを配列に保存する関数
    fun setMap(data: String) {

        var origindata:String=data.replace(",","")
        origindata=origindata.replace("<br>","")
        origindata=origindata.replace("\r\n","")

        //Current.MAP_X = 130.3707f
        //Current.MAP_Y = 33.5886f

        Current.MAP_X = 130.4088f
        Current.MAP_Y = 33.5841f

        for (fy in 0 until 500) {
            for (fx in 0 until 500) {
                Current.MAP[fy][fx]= origindata[fy*500+fx].toString().toInt()
            }
        }

        //ここら辺修正
        Current.MAP_S=1
        Current.BITMAP=Draw.Map(Current.MAP)

        (activity as MainActivity).LoadStop()

        val str="X="+Current.MAP_X+",Y="+Current.MAP_Y+"\n"+data

        MyApp().FileWrite(str,"MAPLOG.txt")

        scale=3f
    }


}
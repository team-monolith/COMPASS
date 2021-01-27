package com.monolith.compass.ui.fitness

import android.annotation.SuppressLint
import android.content.Context
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

open class MonthFragment : Fragment() {

    private lateinit var dayViewModel: FitnessViewModel

    private val GLOBAL = MyApp.getInstance()    //グローバル変数宣言用

    val Draw = CanvasDraw()

    val handler = Handler()//メインスレッド処理用ハンドラ

    var height: Int = 0
    var width: Int = 0

    var moveview: MonthFragment.MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var posX: Int = 0  //表示座標管理用
    var logX: Int = 0  //タップ追従用

    var tapFlg: Boolean = false//矢印表示管理用

    var accelerator: Int = 0//アニメーション加速度用

    var prevDate: Date = Date()//日付保持用

    var steplist=mutableListOf<Int>()//歩数データ保持用

    var target=10000

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

        setDate(0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

    fun setDate(Direction: Int) {

        //リストを破棄
        steplist.clear()

        //calendarのインスタンスを生成し引数ヶ月動かす
        val cl = Calendar.getInstance()
        cl.time = prevDate
        cl.add(Calendar.MONTH, Direction)

        //時刻データを破棄
        cl.clear(Calendar.MINUTE)
        cl.clear(Calendar.SECOND)
        cl.clear(Calendar.MILLISECOND)
        cl.set(Calendar.HOUR_OF_DAY, 0)

        //calendar型からdate型に変換

        val lastday=cl.getActualMaximum(Calendar.DAY_OF_MONTH)//その月の最終日を取得

        for(i in 1..lastday){

            //日付をセットしDate型に変換
            cl.set(Calendar.DAY_OF_MONTH,i)
            prevDate=cl.time

            //STEPLOGを全件ループ
            for(x in GLOBAL.ACTIVITY_LOG.indices){
                //もしi日がデータとして存在する場合は値を取得
                if(prevDate==GLOBAL.ACTIVITY_LOG[x].DATE){
                    steplist.add(GLOBAL.ACTIVITY_LOG[x].STEP)
                    target=GLOBAL.ACTIVITY_LOG[x].TARGET
                    break
                }
                //最後までフォルダを参照しても存在しない場合は0をセットする
                else if(x == GLOBAL.ACTIVITY_LOG.lastIndex){
                    steplist.add(0)
                    target=GLOBAL.LocalSettingRead("LOCAL.txt").TARGET
                }
            }
        }

        //FitnessFragment管轄のレイアウトに変更の命令を出す
        if(parentFragment!=null){
            (parentFragment as FitnessFragment).DataSet(getFirstDay(prevDate),getLastDay(prevDate),lastday)
        }

    }

    fun getFirstDay(date:Date):Date{
        val cal=Calendar.getInstance()
        cal.time=date
        cal.set(Calendar.DAY_OF_MONTH,1)
        return cal.time
    }

    fun getLastDay(date:Date):Date{
        val cal=Calendar.getInstance()
        cal.time=date
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return cal.time
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
                        setDate(1)
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
                        setDate(-1)
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

        setDate(0)

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

            Draw.arrow(height, width, tapFlg, canvas)

            Draw.monthgraph(steplist,target,height,width,posX,canvas)

        }
    }

}
package com.monolith.compass.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
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
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.util.*
import kotlin.collections.HashMap


class EventFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel

    val GLOBAL = MyApp.getInstance()    //グローバル変数呼出用

    val handler = Handler()//メインスレッド処理用ハンドラ

    var moveview: EventFragment.MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var pos: MyApp.COORDINATE = MyApp.COORDINATE(0f, 0f)//地図表示の絶対座標
    var log: MyApp.COORDINATE = MyApp.COORDINATE(0f, 0f)//タップ追従用座標

    var cardbitmap:Bitmap?=null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventViewModel =
            ViewModelProvider(this).get(EventViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_event, container, false)
        val layout = view.findViewById<ConstraintLayout>(R.id.constlayout)
        moveview = MoveView(this.activity)
        layout.addView(moveview)
        layout.setWillNotDraw(false)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).LoadStart()

        getFriendData()

        //アニメーションループを再生
        HandlerDraw(moveview!!)

        //ビューにリスナーを設定
        view.setOnTouchListener { _, event ->
            onTouch(view, event)
        }


    }

    //タッチイベント実行時処理
    fun onTouch(view: View, event: MotionEvent): Boolean {

            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    log.X = event.x
                    log.Y = event.y
                }
                event.action == MotionEvent.ACTION_MOVE -> {
                    pos.X = pos.X!!+event.x - log.X!!
                    pos.Y = pos.Y!!+event.y - log.Y!!
                    log.X = event.x
                    log.Y = event.y
                }
            }

        return true
    }

    //描画関数　再描画用
    fun HandlerDraw(mv:EventFragment.MoveView) {
        handler.post(object : Runnable {
            override fun run() {
                //再描画
                mv.invalidate()
                handler.postDelayed(this, 0)
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

            canvas!!.translate(pos.X!!,0f)

            if(cardbitmap!=null)canvas.drawBitmap(cardbitmap!!,0f,0f,Paint())
        }
    }

    fun getFriendData() {

        val POSTDATA = HashMap<String, String>()

        POSTDATA.put("id","1,2,3,3,1,2,1,3,3,1,2,2,1,2,3,2,3,2,1,1,2,3,2,3,1,1,2,3,3")

        "https://a.compass-user.work/system/user/show_user.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        cardbitmap = view_create(setFriendData(String(response.data)))
                    }
                    is Result.Failure -> {
                    }
                }
            }
    }

    fun setFriendData(data:String):List<MyApp.CARDDATA>{

        var list=mutableListOf<MyApp.CARDDATA>()

        val scan= Scanner(data.replace("<br>",""))
        scan.useDelimiter(",|\n")

        while(scan.hasNext()){
            val ID:Int=scan.nextInt()
            val NAME:String=scan.next()
            val ICON:String=scan.next()
            val LEVEL:Int=scan.nextInt()
            val DISTANCE:Int=scan.nextInt()
            val BADGE:Int=scan.nextInt()
            val BACKGROUND:Int=scan.nextInt()
            val FRAME:Int=scan.nextInt()
            val COMMENT:String=scan.next()
            val STATE:Int=scan.nextInt()
            list.add(
                MyApp.CARDDATA(
                    ID,
                    NAME,
                    GLOBAL.IconBitmapCreate(ICON),
                    LEVEL,
                    DISTANCE,
                    BADGE,
                    BACKGROUND,
                    FRAME,
                    COMMENT,
                    STATE
                )
            )
        }

        return list
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    fun view_create(list:List<MyApp.CARDDATA>):Bitmap{

        val page=(list.size-(list.size%3))/3+1

        val paint= Paint()
        val output= Bitmap.createBitmap(GLOBAL.WIDTH*page,GLOBAL.HEIGHT,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(output)


        for(i in list.indices){
            //比率で近似値の0.6を入れています
            val image=Bitmap.createScaledBitmap(MyApp().CreateCardBitmap(list[i],resources),GLOBAL.WIDTH/24*22,
                (GLOBAL.WIDTH/24*22*0.6).toInt(),true)

            when(i%3){
                0->{
                    canvas.drawBitmap(image,GLOBAL.WIDTH/24f+GLOBAL.WIDTH*((i-(i%3))/3),(GLOBAL.HEIGHT/2-image.height/2f)/2-image.height/2,paint)
                }
                1->{
                    canvas.drawBitmap(image,GLOBAL.WIDTH/24f+GLOBAL.WIDTH*((i-(i%3))/3),GLOBAL.HEIGHT/2-image.height/2f,paint)
                }
                2->{
                    canvas.drawBitmap(image,GLOBAL.WIDTH/24f+GLOBAL.WIDTH*((i-(i%3))/3),((GLOBAL.HEIGHT/2+image.height/2f)+GLOBAL.HEIGHT)/2-image.height/2,paint)
                }
            }
        }

        (activity as MainActivity).LoadStop()

        return output
    }

}
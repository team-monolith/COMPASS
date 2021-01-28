package com.monolith.compass.ui.friend

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs


class CardViewFragment: Fragment() {

    private var gestureDetector: GestureDetector? = null

    private var scroller: Scroller? = null

    val GLOBAL = MyApp.getInstance()    //グローバル変数呼出用

    val handler = Handler()//メインスレッド処理用ハンドラ

    var moveview: CardViewFragment.MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    var pos: MyApp.COORDINATE = MyApp.COORDINATE(0f, 0f)//地図表示の絶対座標
    var log: MyApp.COORDINATE = MyApp.COORDINATE(0f, 0f)//タップ追従用座標

    var cardbitmap: Bitmap?=null

    var height:Int=0
    var width:Int=0

    var velocity:Int=0

    var tapFlg:Boolean=false

    var accelerator: Int = 0

    var list=mutableListOf<MyApp.CARDDATA>()

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

        (activity as MainActivity).LoadStart()

        getFriendData()

        //アニメーションループを再生
        HandlerDraw(moveview!!)

        //ビューにリスナーを設定
        view.setOnTouchListener { _, event ->
            onTouch(view, event)
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        scroller = Scroller(context)
        gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                //止まっているときのみ判定
                if(pos.X!!.toInt()%width==0){

                    //これを読んだらタップしたカードの番号がわかるよ
                    //onTouchCardNumber(e.x.toInt(),e.y.toInt())
                    val n=onTouchCardNumber(e.x.toInt(),e.y.toInt())
                    if(n>=list.size)return false
                    Toast.makeText(getContext(), "n=$n", Toast.LENGTH_SHORT).show()
                    val ma=activity as MainActivity
                    ma.cardDataList=list[n]
                    ma.FriendCardLoardStart(0)
                }
                return false
            }

            override fun onShowPress(e: MotionEvent) {
                // TODO Auto-generated method stub
            }

            override fun onScroll(
                e1: MotionEvent, e2: MotionEvent, distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (!scroller!!.isFinished) scroller!!.abortAnimation()
                pos.X = pos.X!!+e2.x - log.X!!
                log.X = e2.x


                return true
            }

            override fun onLongPress(e: MotionEvent) {
            }

            override fun onFling(
                e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                velocityY: Float
            ): Boolean {
                velocity=velocityX.toInt()/15
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                log.X=e.x
                velocity=0
                return true
            }

        })
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).LoadStop()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).LoadStop()
    }

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).LoadStop()
    }


    //タッチイベント実行時処理
    fun onTouch(view: View, event: MotionEvent): Boolean {

        tapFlg=true
        gestureDetector!!.onTouchEvent(event)

        if(event.action== MotionEvent.ACTION_UP&&event.pointerCount==1){
            tapFlg=false
        }

        return true
    }

    //描画関数　再描画用
    fun HandlerDraw(mv: CardViewFragment.MoveView) {
        handler.post(object : Runnable {
            override fun run() {

                SystemReflesh()
                //再描画
                mv.invalidate()
                handler.postDelayed(this, 10)
            }
        })
    }

    fun SystemReflesh(){

        if(width==0)return

        //velocityの値を動かして慣性を作る
        if(velocity>0){
            velocity-=10
            if(velocity<0)velocity=0
        }
        if(velocity<0){
            velocity+=10
            if(velocity>0)velocity=0
        }

        //慣性分動かす
        pos.X=pos.X!!+velocity/10

        if(pos.X!! <-((list.size-(list.size%3))/3)*width-width/3f){
            pos.X=-((list.size-(list.size%3))/3f)*width-width/3f
            velocity=0
        }
        else if(pos.X!!>width/3){
            pos.X=width/3f
            velocity=0
        }

        if(velocity==0&&!tapFlg){
            if(pos.X!!.toInt()%width!=0){

                if(abs(pos.X!!.toInt()%width) >0){
                    if(abs(pos.X!!.toInt()%width) <width/2&&pos.X!!<0){
                        pos.X= pos.X!! +accelerator
                    }
                    else{
                        pos.X=pos.X!!-accelerator
                    }
                    accelerator+=2
                }

                if(abs(pos.X!!.toInt()%width) <=accelerator){
                    pos.X=pos.X!!-(pos.X!!.toInt()%width)
                    accelerator=0
                }
            }
        }
        else{
            accelerator=0
        }
    }

    //タッチしたカードが何番目かを返す関数
    fun onTouchCardNumber(x:Int,y:Int):Int{

        if(y<height/3){
            return (abs(pos.X!!.toInt()) /width)*3
        }
        else if(y<height/3*2){
            return (abs(pos.X!!.toInt()) /width)*3+1
        }
        else{
            return (abs(pos.X!!.toInt()) /width)*3+2
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

            canvas.translate(pos.X!!,0f)

            if(cardbitmap!=null)canvas.drawBitmap(cardbitmap!!,0f,0f, Paint())

            canvas.restore()

            val paint= Paint()
        }
    }

    fun getFriendData() {

        val POSTDATA = HashMap<String, String>()

        POSTDATA.put("id","1,2,3,4,5")

        "https://b.compass-user.work/system/user/show_user.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        setFriendData(String(response.data))
                        cardbitmap = view_create()
                    }
                    is Result.Failure -> {
                    }
                }
            }
    }

    fun setFriendData(data:String){

        val scan= Scanner(data.replace("<br>",""))
        scan.useDelimiter(",|\n")

        val ma=activity as MainActivity

        while(scan.hasNext()){
            var buf:String=scan.next()

            val ID:Int
            if(buf!="")ID=buf.toInt()

            else ID=0

            buf=scan.next()
            val NAME:String
            if(buf!="")NAME=buf
            else NAME="NAME"

            buf=scan.next()
            val ICON:String
            if(buf!="")ICON=buf
            else ICON="ICON"

            buf=scan.next()
            val LEVEL:Int
            if(buf!="")LEVEL=buf.toInt()
            else LEVEL=0

            buf=scan.next()
            val DISTANCE:Int
            if(buf!="")DISTANCE=buf.toInt()
            else DISTANCE=0

            buf=scan.next()
            val BADGE:Int
            if(buf!="")BADGE=buf.toInt()
            else BADGE=0

            buf=scan.next()
            val BACK:Int
            if(buf!="")BACK=buf.toInt()
            else BACK=0

            buf=scan.next()
            val FRAME:Int
            if(buf!="")FRAME=buf.toInt()
            else FRAME=0

            buf=scan.next()
            val COMMENT:String
            if(buf!="")COMMENT=buf
            else COMMENT=""

            val STATE:Int
            if(scan.hasNext()){
                buf=scan.next()
                if(buf!="")STATE=buf.toInt()
                else STATE=0
            }
            else{
                STATE=0
            }



            list.add(
                MyApp.CARDDATA(
                    ID,
                    NAME,
                    GLOBAL.IconBitmapCreate(ICON),
                    LEVEL,
                    DISTANCE,
                    BADGE,
                    BACK,
                    FRAME,
                    COMMENT,
                    STATE
                )
            )
            ma.cardIDs.add(ID)
        }
    }



    fun view_create(): Bitmap {

        val page=(list.size-(list.size%3))/3+1

        val paint= Paint()
        val output= Bitmap.createBitmap(width*page,height, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(output)


        for(i in list.indices){
            //比率で近似値の0.6を入れています
            val image= Bitmap.createScaledBitmap(
                MyApp().CreateCardBitmap(list[i],resources),width/24*22,
                (width/24*22*0.6).toInt(),true)

            when(i%3){
                0->{
                    canvas.drawBitmap(image,width/24f+width*((i-(i%3))/3),(height/2-image.height/2f)/2-image.height/2,paint)
                }
                1->{
                    canvas.drawBitmap(image,width/24f+width*((i-(i%3))/3),height/2-image.height/2f,paint)
                }
                2->{
                    canvas.drawBitmap(image,width/24f+width*((i-(i%3))/3),((height/2+image.height/2f)+height)/2-image.height/2,paint)
                }
            }
        }

        pos.X=0f

        (activity as MainActivity).LoadStop()

        return output
    }







}
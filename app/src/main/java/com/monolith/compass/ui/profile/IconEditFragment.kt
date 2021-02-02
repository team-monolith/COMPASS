package com.monolith.compass.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Base64
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp

class fbgdfgbdIconEditFragment:Fragment(){

    val GLOBAL = MyApp.getInstance()

    val handler = Handler()//メインスレッド処理用ハンドラ
    var moveview: MoveView? = null //キャンバスリフレッシュ用インスタンス保持変数

    private lateinit var mScaleDetector: ScaleGestureDetector

    var scale: Float = 1F   //地図表示のスケール
    var posX: Int = 0    //地図表示の相対X座標
    var posY: Int = 0    //地図表示の絶対Y座標
    var logX: Int? = null  //タップ追従用X座標
    var logY: Int? = null  //タップ追従用Y座標

    var size: Rect? = null  //画面サイズ取得用

    var multiFlg:Boolean=false  //マルチタッチ検出用

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_iconedit, container, false)
        val layout = view.findViewById<ConstraintLayout>(R.id.constfrag)
        moveview = MoveView(this.activity)

        layout.addView(moveview)
        layout.setWillNotDraw(false)

        (activity as MainActivity).open_gallery()

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab_rotate=view.findViewById<FloatingActionButton>(R.id.fab_rotate)
        val fab_flip=view.findViewById<FloatingActionButton>(R.id.fab_flip)
        val fab_close=view.findViewById<FloatingActionButton>(R.id.fab_close)
        val fab_save=view.findViewById<FloatingActionButton>(R.id.fab_save)


        //画面サイズ取得
        size = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(size)


        HandlerDraw(moveview!!)

        fab_rotate.setOnClickListener{
            if(GLOBAL.ImageBuffer!=null){
                val matrix=Matrix()
                matrix.setRotate(90f,GLOBAL.ImageBuffer!!.width/2f,GLOBAL.ImageBuffer!!.height/2f)
                GLOBAL.ImageBuffer=Bitmap.createBitmap(GLOBAL.ImageBuffer!!,0,0,GLOBAL.ImageBuffer!!.width, GLOBAL.ImageBuffer!!.height,matrix,false)
            }
        }

        fab_flip.setOnClickListener{
            if(GLOBAL.ImageBuffer!=null){
                val matrix=Matrix()
                matrix.preScale(-1f,1f)
                GLOBAL.ImageBuffer=Bitmap.createBitmap(GLOBAL.ImageBuffer!!,0,0,GLOBAL.ImageBuffer!!.width, GLOBAL.ImageBuffer!!.height,matrix,false)
            }
        }

        fab_close.setOnClickListener{
            findNavController().navigate(R.id.navigation_profile_edit)
        }

        fab_save.setOnClickListener{
            ImageCreate()
            findNavController().navigate(R.id.navigation_profile_edit)
        }


        view.setOnTouchListener { _, event ->
            onTouch(view, event)
            return@setOnTouchListener true
        }



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //ピンチ処理関係
        mScaleDetector = ScaleGestureDetector(context,
            object : ScaleGestureDetector.OnScaleGestureListener {

                //スケール変更処理
                override fun onScale(detector: ScaleGestureDetector): Boolean {

                    scale *= detector.scaleFactor
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


    fun onTouch(view: View, event: MotionEvent) {
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
                    logX = event.x.toInt()
                    logY = event.y.toInt()
                }
                event.action == MotionEvent.ACTION_MOVE && !multiFlg -> {
                    posX += event.x.toInt() - logX!!
                    posY += event.y.toInt() - logY!!
                    logX = event.x.toInt()
                    logY = event.y.toInt()
                }
            }
        }
    }


    fun ImageCreate() {
        //https://qiita.com/yuukiw00w/items/6fc0af6ac829b8a5af45を参考に作成

        val paint = Paint()
        val output = Bitmap.createBitmap(size!!.width() / 3 * 2, size!!.width() / 3 * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val FrameRect: RectF = RectF(size!!.width() / 2 - size!!.width() / 3f, size!!.height() / 2 - size!!.width() / 3f, size!!.width() / 2 + size!!.width() / 3f, size!!.height() / 2 + size!!.width() / 3f)

        paint.isAntiAlias = true

        //左上始点にするため、フレームの左上角が0,0に来るようにオフセットする
        canvas.translate(-(size!!.width() / 2 - size!!.width() / 3f), -(size!!.height() / 2 - size!!.width() / 3f))

        //フレームサイズの四角を描画する
        canvas.drawRect(FrameRect, paint)

        //フレームの四角と重なった画像部分を表示するように設定
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        //画像を表示
        canvas.save()
        canvas.scale(scale, scale)

        if (GLOBAL.ImageBuffer != null) {
            paint.color=Color.WHITE
            canvas.drawRect(FrameRect, paint)
            canvas.drawBitmap(GLOBAL.ImageBuffer!!, posX * 1f, posY * 1f, paint)
        }

        canvas.restore()


        //画像を250x250にリサイズし出力
        val file = File(GLOBAL.DIRECTORY, "icon.png")
        FileOutputStream(file).use { fileOutputStream ->
            Bitmap.createScaledBitmap(
                output,
                250,
                250,
                true
            ).compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream)
            fileOutputStream.flush()
        }


        /*val baos = ByteArrayOutputStream()
        output.compress(Bitmap.CompressFormat.PNG, 50, baos)
        Connect(Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP))
*/
    }


    //描画関数　再描画用
    fun HandlerDraw(mv: MoveView) {
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
            val paint = Paint()
            val FramePaint = Paint()
            val BackPaint = Paint()

            var buttonHeight:Int=0

            if(view!=null){
                buttonHeight=view!!.findViewById<LinearLayout>(R.id.linearlayout).top
            }

            //canvas!!.drawColor(Color.BLACK)

            FramePaint.strokeWidth = 15f
            FramePaint.style = Paint.Style.STROKE
            FramePaint.color = Color.parseColor("#00FF00")

            BackPaint.color = Color.parseColor("#000000")
            BackPaint.alpha = 128

            //フレーム及び背景に必要な座標を計算
            val FrameRect: RectF = RectF(size!!.width() / 2 - size!!.width() / 3f, size!!.height() / 2 - size!!.width() / 3f, size!!.width() / 2 + size!!.width() / 3f, size!!.height() / 2 + size!!.width() / 3f)
            val TopRect: RectF = RectF(0f, 0f, size!!.width() * 1f, size!!.height() / 2 - size!!.width() / 3f)
            val BottomRect: RectF = RectF(0f, size!!.height() / 2 + size!!.width() / 3f, size!!.width() * 1f, buttonHeight*1f)
            val RightRect: RectF = RectF(size!!.width() / 2 + size!!.width() / 3f, size!!.height() / 2 - size!!.width() / 3f, size!!.width() * 1f, size!!.height() / 2 + size!!.width() / 3f)
            val LeftRect: RectF = RectF(0f, size!!.height() / 2 - size!!.width() / 3f, size!!.width() / 2 - size!!.width() / 3f, size!!.height() / 2 + size!!.width() / 3f)


            //キャンバスのスケールを保存しておく
            canvas!!.save()
            canvas.scale(scale, scale)

            if (GLOBAL.ImageBuffer != null) {
                canvas.drawBitmap(GLOBAL.ImageBuffer!!, posX * 1f, posY * 1f, paint)
            }

            //キャンバスのスケールをもとに戻す
            canvas.restore()

            //フレーム及び半透明の背景処理
            canvas.drawRect(TopRect, BackPaint)
            canvas.drawRect(BottomRect, BackPaint)
            canvas.drawRect(RightRect, BackPaint)
            canvas.drawRect(LeftRect, BackPaint)
            canvas.drawRect(FrameRect, FramePaint)

        }
    }
}
package com.monolith.compass.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.monolith.compass.ui.map.MapViewModel

import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    var moveview:MoveView?=null

    var tapX:Float? = null
    var tapY:Float? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)
        val view=inflater.inflate(R.layout.fragment_map,container,false)
        val layout=view.findViewById<ConstraintLayout>(R.id.constmap)
        moveview=MoveView(this.activity)
        layout.addView(moveview)
        layout.setWillNotDraw(false)

        HandlerDraw(moveview!!)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ビューにリスナーを設定
        view.setOnTouchListener { _, event ->
            if (event.action== MotionEvent.ACTION_DOWN||event.action == MotionEvent.ACTION_MOVE) {
                //MoveView(this.activity).postInvalidate()
                tapX=event.x
                tapY=event.y

                HandlerDraw(moveview!!)
            }
            true
        }
    }


    fun HandlerDraw(mv:MoveView){
        val handler = Handler()
        handler.post(object : Runnable{
            override fun run() {
                mv.invalidate()
            }
        })
    }

    inner class MoveView: View {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas : Canvas?){
            super.onDraw(canvas)

            val paint = Paint()
            paint.color = Color.RED

            if(tapX!=null&&tapY!=null){
                val rect = Rect((tapX!!-100).toInt(), (tapY!!-100).toInt(), (tapX!!+100).toInt(), (tapY!!+100).toInt())
                canvas!!.drawRect(rect, paint)
            }

        }
    }

}
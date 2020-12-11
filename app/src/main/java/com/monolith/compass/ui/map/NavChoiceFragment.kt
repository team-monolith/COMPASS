package com.monolith.compass.ui.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.monolith.compass.R


class NavChoiceFragment : Fragment() {

    private lateinit var navchoiceViewModel: NavChoiceViewModel

    lateinit var  navView:BottomNavigationView

    var _clickListener: NavChoiceFragment.OnClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navchoiceViewModel =
            ViewModelProvider(this).get(NavChoiceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_nav_choice, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val iv_Back=view.findViewById<ImageView>(R.id.ivBack)
        val iv_Map=view.findViewById<ImageView>(R.id.ivMap)
        val iv_Event=view.findViewById<ImageView>(R.id.ivEvent)

        startAnimation(iv_Map,iv_Event)

        iv_Back.setOnClickListener{
            _clickListener?.onClick_Back()
        }
        iv_Map.setOnClickListener{
            _clickListener?.onClick_Map()
        }
        iv_Event.setOnClickListener {
            _clickListener?.onClick_Event()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            _clickListener = context as OnClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement OnArticleSelectedListener.")
        }
    }

    override fun onDetach(){
        super.onDetach()
        _clickListener=null
    }

    interface OnClickListener{
        fun onClick_Back()
        fun onClick_Map()
        fun onClick_Event()
    }

    fun startAnimation(iv1:ImageView,iv2:ImageView){

        var translateAnimation1= TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.2f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.2f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        translateAnimation1.duration = 100
        translateAnimation1.repeatCount=0
        translateAnimation1.fillAfter=true

        var translateAnimation2= TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -0.2f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.2f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        translateAnimation2.duration = 150
        translateAnimation2.repeatCount=0
        translateAnimation2.fillAfter=true

        iv1.startAnimation(translateAnimation1)

        iv2.startAnimation(translateAnimation2)
    }

}
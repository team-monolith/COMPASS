package com.monolith.compass.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.MainActivity
import com.monolith.compass.R


class ProfCardBackFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_card_background, container, false)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ma = activity as MainActivity

        chenge_color(ma.profView[0])

        view.findViewById<ImageView>(R.id.frame_img0).setOnClickListener{

            reset_color()
            chenge_color(0)
            ma.profView[0]= 0
            ma.profsave[0]= 0
        }
        view.findViewById<ImageView>(R.id.frame_img1).setOnClickListener{
            reset_color()
            chenge_color(1)
            ma.profView[0]= 1
            ma.profsave[0]= 1
        }
        view.findViewById<ImageView>(R.id.frame_img2).setOnClickListener{
            reset_color()
            chenge_color(2)
            ma.profView[0]= 2
            ma.profsave[0]= 2
        }
        view.findViewById<ImageView>(R.id.frame_img3).setOnClickListener{
            reset_color()
            chenge_color(3)
            ma.profView[0]= 3
            ma.profsave[0]= 3
        }
        view.findViewById<ImageView>(R.id.frame_img4).setOnClickListener{
            reset_color()
            chenge_color(4)
            ma.profView[0]= 4
            ma.profsave[0]= 4
        }
        view.findViewById<ImageView>(R.id.frame_img5).setOnClickListener{
            reset_color()
            chenge_color(5)
            ma.profView[0]= 5
            ma.profsave[0]= 5
        }
        view.findViewById<ImageView>(R.id.frame_img6).setOnClickListener{
            reset_color()
            chenge_color(6)
            ma.profView[0]= 6
            ma.profsave[0]= 6
        }
        view.findViewById<ImageView>(R.id.frame_img7).setOnClickListener{
            reset_color()
            chenge_color(7)
            ma.profView[0]= 7
            ma.profsave[0]= 7
        }
        view.findViewById<ImageView>(R.id.frame_img8).setOnClickListener{
            reset_color()
            chenge_color(8)
            ma.profView[0]= 8
            ma.profsave[0]= 8
        }
        view.findViewById<ImageView>(R.id.frame_img9).setOnClickListener{
            reset_color()
            chenge_color(9)
            ma.profView[0]= 9
            ma.profsave[0]= 9
        }
        view.findViewById<ImageView>(R.id.frame_img10).setOnClickListener{
            reset_color()
            chenge_color(10)
            ma.profView[0]= 10
            ma.profsave[0]= 10
        }
        view.findViewById<ImageView>(R.id.frame_img11).setOnClickListener{
            reset_color()
            chenge_color(11)
            ma.profView[0]= 11
            ma.profsave[0]= 11
        }
        view.findViewById<ImageView>(R.id.frame_img12).setOnClickListener{
            reset_color()
            chenge_color(12)
            ma.profView[0]= 12
            ma.profsave[0]= 12
        }
        view.findViewById<ImageView>(R.id.frame_img13).setOnClickListener{
            reset_color()
            chenge_color(13)
            ma.profView[0]= 13
            ma.profsave[0]= 13
        }
    }

    /*指定されたIDの色赤にセット*/
    fun chenge_color(ID:Int){
        when(ID){
            0-> view?.findViewById<FrameLayout>(R.id.s_layout0)?.setBackgroundColor(Color.rgb(255, 0, 0))
            1-> view?.findViewById<FrameLayout>(R.id.s_layout1)?.setBackgroundColor(Color.rgb(255, 0, 0))
            2-> view?.findViewById<FrameLayout>(R.id.s_layout2)?.setBackgroundColor(Color.rgb(255, 0, 0))
            3-> view?.findViewById<FrameLayout>(R.id.s_layout3)?.setBackgroundColor(Color.rgb(255, 0, 0))
            4-> view?.findViewById<FrameLayout>(R.id.s_layout4)?.setBackgroundColor(Color.rgb(255, 0, 0))
            5-> view?.findViewById<FrameLayout>(R.id.s_layout5)?.setBackgroundColor(Color.rgb(255, 0, 0))
            6-> view?.findViewById<FrameLayout>(R.id.s_layout6)?.setBackgroundColor(Color.rgb(255, 0, 0))
            7-> view?.findViewById<FrameLayout>(R.id.s_layout7)?.setBackgroundColor(Color.rgb(255, 0, 0))
            8-> view?.findViewById<FrameLayout>(R.id.s_layout8)?.setBackgroundColor(Color.rgb(255, 0, 0))
            9-> view?.findViewById<FrameLayout>(R.id.s_layout9)?.setBackgroundColor(Color.rgb(255, 0, 0))
            10-> view?.findViewById<FrameLayout>(R.id.s_layout10)?.setBackgroundColor(Color.rgb(255, 0, 0))
            11-> view?.findViewById<FrameLayout>(R.id.s_layout11)?.setBackgroundColor(Color.rgb(255, 0, 0))
            12-> view?.findViewById<FrameLayout>(R.id.s_layout12)?.setBackgroundColor(Color.rgb(255, 0, 0))
            13-> view?.findViewById<FrameLayout>(R.id.s_layout13)?.setBackgroundColor(Color.rgb(255, 0, 0))
        }
    }

    /*すべての色を白にセット*/
    fun reset_color(){
        val change0 = view?.findViewById<FrameLayout>(R.id.s_layout0)?.setBackgroundColor(Color.rgb(255,255,255))
        val change1 = view?.findViewById<FrameLayout>(R.id.s_layout1)?.setBackgroundColor(Color.rgb(255,255,255))
        val change2 = view?.findViewById<FrameLayout>(R.id.s_layout2)?.setBackgroundColor(Color.rgb(255,255,255))
        val change3 = view?.findViewById<FrameLayout>(R.id.s_layout3)?.setBackgroundColor(Color.rgb(255,255,255))
        val change4 = view?.findViewById<FrameLayout>(R.id.s_layout4)?.setBackgroundColor(Color.rgb(255,255,255))
        val change5 = view?.findViewById<FrameLayout>(R.id.s_layout5)?.setBackgroundColor(Color.rgb(255,255,255))
        val change6 = view?.findViewById<FrameLayout>(R.id.s_layout6)?.setBackgroundColor(Color.rgb(255,255,255))
        val change7 = view?.findViewById<FrameLayout>(R.id.s_layout7)?.setBackgroundColor(Color.rgb(255,255,255))
        val change8 = view?.findViewById<FrameLayout>(R.id.s_layout8)?.setBackgroundColor(Color.rgb(255,255,255))
        val change9 = view?.findViewById<FrameLayout>(R.id.s_layout9)?.setBackgroundColor(Color.rgb(255,255,255))
        val change10 = view?.findViewById<FrameLayout>(R.id.s_layout10)?.setBackgroundColor(Color.rgb(255,255,255))
        val change11 = view?.findViewById<FrameLayout>(R.id.s_layout11)?.setBackgroundColor(Color.rgb(255,255,255))
        val change12 = view?.findViewById<FrameLayout>(R.id.s_layout12)?.setBackgroundColor(Color.rgb(255,255,255))
        val change13 = view?.findViewById<FrameLayout>(R.id.s_layout13)?.setBackgroundColor(Color.rgb(255,255,255))
    }
}
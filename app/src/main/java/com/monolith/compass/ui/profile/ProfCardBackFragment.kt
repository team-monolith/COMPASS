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
import com.monolith.compass.com.monolith.compass.MyApp


class ProfCardBackFragment : Fragment() {
    val GLOBAL= MyApp.getInstance()
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

        chenge_color(GLOBAL.cardData.BACKGROUND)

        view.findViewById<ImageView>(R.id.frame_img0).setOnClickListener{
            reset_color()
            chenge_color(0)
            GLOBAL.cardData.BACKGROUND = 0
        }
        view.findViewById<ImageView>(R.id.frame_img1).setOnClickListener{
            reset_color()
            chenge_color(1)
            GLOBAL.cardData.BACKGROUND = 1
        }
        view.findViewById<ImageView>(R.id.frame_img2).setOnClickListener{
            reset_color()
            chenge_color(2)
            GLOBAL.cardData.BACKGROUND = 2
        }
        view.findViewById<ImageView>(R.id.frame_img3).setOnClickListener{
            reset_color()
            chenge_color(3)
            GLOBAL.cardData.BACKGROUND = 3
        }
        view.findViewById<ImageView>(R.id.frame_img4).setOnClickListener{
            reset_color()
            chenge_color(4)
            GLOBAL.cardData.BACKGROUND = 4
        }
        view.findViewById<ImageView>(R.id.frame_img5).setOnClickListener{
            reset_color()
            chenge_color(5)
            GLOBAL.cardData.BACKGROUND = 5
        }
        view.findViewById<ImageView>(R.id.frame_img6).setOnClickListener{
            reset_color()
            chenge_color(6)
            GLOBAL.cardData.BACKGROUND = 6
        }
        view.findViewById<ImageView>(R.id.frame_img7).setOnClickListener{
            reset_color()
            chenge_color(7)
            GLOBAL.cardData.BACKGROUND = 7
        }
        view.findViewById<ImageView>(R.id.frame_img8).setOnClickListener{
            reset_color()
            chenge_color(8)
            GLOBAL.cardData.BACKGROUND = 8
        }
        view.findViewById<ImageView>(R.id.frame_img9).setOnClickListener{
            reset_color()
            chenge_color(9)
            GLOBAL.cardData.BACKGROUND = 9
        }
        view.findViewById<ImageView>(R.id.frame_img10).setOnClickListener{
            reset_color()
            chenge_color(10)
            GLOBAL.cardData.BACKGROUND = 10
        }
        view.findViewById<ImageView>(R.id.frame_img11).setOnClickListener{
            reset_color()
            chenge_color(11)
            GLOBAL.cardData.BACKGROUND = 11
        }
        view.findViewById<ImageView>(R.id.frame_img12).setOnClickListener{
            reset_color()
            chenge_color(12)
            GLOBAL.cardData.BACKGROUND = 12
        }
        view.findViewById<ImageView>(R.id.frame_img13).setOnClickListener{
            reset_color()
            chenge_color(13)
            GLOBAL.cardData.BACKGROUND = 13
        }
        view.findViewById<ImageView>(R.id.frame_img14).setOnClickListener{
            reset_color()
            chenge_color(14)
            GLOBAL.cardData.BACKGROUND = 14
        }
        view.findViewById<ImageView>(R.id.frame_img15).setOnClickListener{
            reset_color()
            chenge_color(15)
            GLOBAL.cardData.BACKGROUND = 15
        }
        view.findViewById<ImageView>(R.id.frame_img16).setOnClickListener{
            reset_color()
            chenge_color(16)
            GLOBAL.cardData.BACKGROUND = 16
        }
        view.findViewById<ImageView>(R.id.frame_img17).setOnClickListener{
            reset_color()
            chenge_color(17)
            GLOBAL.cardData.BACKGROUND = 17
        }
        view.findViewById<ImageView>(R.id.frame_img18).setOnClickListener{
            reset_color()
            chenge_color(18)
            GLOBAL.cardData.BACKGROUND = 18
        }
        view.findViewById<ImageView>(R.id.frame_img19).setOnClickListener{
            reset_color()
            chenge_color(19)
            GLOBAL.cardData.BACKGROUND = 19
        }
        view.findViewById<ImageView>(R.id.frame_img20).setOnClickListener{
            reset_color()
            chenge_color(20)
            GLOBAL.cardData.BACKGROUND = 20
        }
        view.findViewById<ImageView>(R.id.frame_img21).setOnClickListener{
            reset_color()
            chenge_color(21)
            GLOBAL.cardData.BACKGROUND = 21
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
            14-> view?.findViewById<FrameLayout>(R.id.s_layout14)?.setBackgroundColor(Color.rgb(255, 0, 0))
            15-> view?.findViewById<FrameLayout>(R.id.s_layout15)?.setBackgroundColor(Color.rgb(255, 0, 0))
            16-> view?.findViewById<FrameLayout>(R.id.s_layout16)?.setBackgroundColor(Color.rgb(255, 0, 0))
            17-> view?.findViewById<FrameLayout>(R.id.s_layout17)?.setBackgroundColor(Color.rgb(255, 0, 0))
            18-> view?.findViewById<FrameLayout>(R.id.s_layout18)?.setBackgroundColor(Color.rgb(255, 0, 0))
            19-> view?.findViewById<FrameLayout>(R.id.s_layout19)?.setBackgroundColor(Color.rgb(255, 0, 0))
            20-> view?.findViewById<FrameLayout>(R.id.s_layout20)?.setBackgroundColor(Color.rgb(255, 0, 0))
            21-> view?.findViewById<FrameLayout>(R.id.s_layout21)?.setBackgroundColor(Color.rgb(255, 0, 0))
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
        val change14 = view?.findViewById<FrameLayout>(R.id.s_layout14)?.setBackgroundColor(Color.rgb(255,255,255))
        val change15 = view?.findViewById<FrameLayout>(R.id.s_layout15)?.setBackgroundColor(Color.rgb(255,255,255))
        val change16 = view?.findViewById<FrameLayout>(R.id.s_layout16)?.setBackgroundColor(Color.rgb(255,255,255))
        val change17 = view?.findViewById<FrameLayout>(R.id.s_layout17)?.setBackgroundColor(Color.rgb(255,255,255))
        val change18 = view?.findViewById<FrameLayout>(R.id.s_layout18)?.setBackgroundColor(Color.rgb(255,255,255))
        val change19 = view?.findViewById<FrameLayout>(R.id.s_layout19)?.setBackgroundColor(Color.rgb(255,255,255))
        val change20 = view?.findViewById<FrameLayout>(R.id.s_layout20)?.setBackgroundColor(Color.rgb(255,255,255))
        val change21 = view?.findViewById<FrameLayout>(R.id.s_layout21)?.setBackgroundColor(Color.rgb(255,255,255))
    }
}
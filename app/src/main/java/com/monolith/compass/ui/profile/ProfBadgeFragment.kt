package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import org.w3c.dom.Text

class ProfBadgeFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    val GLOBAL = MyApp.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_badge, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iv_black = view?.findViewById<ImageView>(R.id.ivBlack)
        iv_black.setOnClickListener {
            val fg = parentFragment as ProfileFragment
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        //プログレスバー
        val copper_pb = view?.findViewById<ProgressBar>(R.id.copper_pb)
        val silver_pb = view?.findViewById<ProgressBar>(R.id.silver_pb)
        val gold_pb = view?.findViewById<ProgressBar>(R.id.gold_pb)
        val platinum_pb = view?.findViewById<ProgressBar>(R.id.platinum_pb)

        //テキスト
        val copper_txt= view?.findViewById<TextView>(R.id.copper_txt)
        val silver_txt = view?.findViewById<TextView>(R.id.silver_txt)
        val gold_txt = view?.findViewById<TextView>(R.id.gold_txt)
        val platinum_txt = view?.findViewById<TextView>(R.id.platinum_txt)

        //画像
        val copper_img = view?.findViewById<ImageView>(R.id.copper_img)
        copper_img.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        val silver_img = view?.findViewById<ImageView>(R.id.silver_img)
        silver_img.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        val gold_img = view?.findViewById<ImageView>(R.id.gold_img)
        gold_img.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        val platinum_img = view?.findViewById<ImageView>(R.id.platinum_img)
        platinum_img.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))

        when(GLOBAL.click_badge){
            "day" ->
            {
                val work = GLOBAL.progressData.LOGIN_DAY
                copper_txt.setText("7日ログインしよう\n" + work.toString() + "/7" )
                copper_pb.setMax(7)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))

                silver_txt.setText("30日ログインしよう\n" + work.toString() + "/30")
                silver_pb.setMax(30)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))

                gold_txt.setText("180日ログインしよう\n" + work.toString() + "/180")
                gold_pb.setMax(180)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))


                platinum_txt.setText("365日ログインしよう\n" + work.toString() + "/365")
                platinum_pb.setMax(365)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))
            }

            "level" ->
            {
                val work = GLOBAL.progressData.BADGE_LEVEL
                copper_txt.setText("10レベルに上げよう\n" + work.toString() + "/10" )
                copper_pb.setMax(10)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_1","drawable","com.monolith.compass"))

                silver_txt.setText("25レベルにあげよう\n" + work.toString() + "/25" )
                silver_pb.setMax(25)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_1","drawable","com.monolith.compass"))

                gold_txt.setText("50レベルに上げよう\n" + work.toString() + "/50" )
                gold_pb.setMax(50)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_1","drawable","com.monolith.compass"))

                platinum_txt.setText("100レベルに上げよう\n" + work.toString() + "/100" )
                platinum_pb.setMax(100)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_1","drawable","com.monolith.compass"))
            }

            "distance" ->
            {
                val work = GLOBAL.progressData.BADGE_DISTANCE / 1000
                copper_txt.setText("10km 進めよう\n" + work.toString() + "/10")
                copper_pb.setMax(10)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_2","drawable","com.monolith.compass"))

                silver_txt.setText("100km 進めよう\n" + work.toString() + "/100")
                silver_pb.setMax(100)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_2","drawable","com.monolith.compass"))

                gold_txt.setText("1000km 進めよう\n" + work.toString() + "/1000")
                gold_pb.setMax(1000)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_2","drawable","com.monolith.compass"))

                platinum_txt.setText("10000km 進めよう\n" + work.toString() + "/10000")
                platinum_pb.setMax(10000)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_2","drawable","com.monolith.compass"))
            }

            "step" ->
            {
                val work = GLOBAL.progressData.STEPS
                copper_txt.setText("10万歩 歩こう\n" + (work / 10000).toString() + "/10")
                copper_pb.setMax(100000)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_3","drawable","com.monolith.compass"))

                silver_txt.setText("100万歩 歩こう\n" + (work / 10000).toString() + "/100")
                silver_pb.setMax(1000000)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_3","drawable","com.monolith.compass"))

                gold_txt.setText("500万歩 歩こう\n" + (work / 10000).toString() + "/500")
                gold_pb.setMax(5000000)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_3","drawable","com.monolith.compass"))

                platinum_txt.setText("1000万歩 歩こう\n" + (work / 10000).toString() + "/1000")
                platinum_pb.setMax(10000000)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_3","drawable","com.monolith.compass"))
            }

            "dev" ->
            {
                val work = GLOBAL.progressData.DEV_DISTANCE
                copper_txt.setText("1km 新規開拓しよう\n" + work.toString() + "/1")
                copper_pb.setMax(1)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_4","drawable","com.monolith.compass"))

                silver_txt.setText("10km 新規開拓しよう\n" + work.toString() + "/10")
                silver_pb.setMax(10)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_4","drawable","com.monolith.compass"))

                gold_txt.setText("100km 新規開拓しよう\n" + work.toString() + "/100")
                gold_pb.setMax(100)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_4","drawable","com.monolith.compass"))

                platinum_txt.setText("500km 新規開拓しよう\n" + work.toString() + "/500")
                platinum_pb.setMax(500)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_4","drawable","com.monolith.compass"))
            }

            "calo" ->
            {
                val work = GLOBAL.progressData.CONS_CAL

                copper_txt.setText("1000kcal 消費しよう\n" + work.toString() + "/1000")
                copper_pb.setMax(1000)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_5","drawable","com.monolith.compass"))

                silver_txt.setText("5000kcal 消費しよう\n" + work.toString() + "/5000")
                silver_pb.setMax(5000)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_5","drawable","com.monolith.compass"))

                gold_txt.setText("25000kcal 消費しよう\n" + work.toString() + "/25000")
                gold_pb.setMax(25000)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_5","drawable","com.monolith.compass"))

                platinum_txt.setText("50000kcal 消費しよう\n" + work.toString() + "/50000")
                platinum_pb.setMax(50000)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_5","drawable","com.monolith.compass"))
            }

            "friend" ->
            {
                val work = GLOBAL.progressData.PASSING

                copper_txt.setText("1人とすれ違う\n" + work.toString() + "/1")
                copper_pb.setMax(1)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_6","drawable","com.monolith.compass"))

                silver_txt.setText("10人とすれ違う\n" + work.toString() + "/10")
                silver_pb.setMax(10)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_6","drawable","com.monolith.compass"))

                gold_txt.setText("50人とすれ違う\n" + work.toString() + "/50")
                gold_pb.setMax(50)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_6","drawable","com.monolith.compass"))

                platinum_txt.setText("100人とすれ違う\n" + work.toString() + "/100")
                platinum_pb.setMax(100)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_6","drawable","com.monolith.compass"))
            }

            "event" ->
            {
                val work = GLOBAL.progressData.EVENT

                copper_txt.setText("1回イベントに参加しよう\n" + work.toString() + "/1")
                copper_pb.setMax(1)
                copper_pb.setProgress(work)
                copper_img.setImageResource(resources.getIdentifier("badge_icon_7","drawable","com.monolith.compass"))

                silver_txt.setText("3回イベントに参加しよう\n" + work.toString() + "/3")
                silver_pb.setMax(3)
                silver_pb.setProgress(work)
                silver_img.setImageResource(resources.getIdentifier("badge_icon_7","drawable","com.monolith.compass"))

                gold_txt.setText("6回イベントに参加しよう\n" +  work.toString() + "/6")
                gold_pb.setMax(6)
                gold_pb.setProgress(work)
                gold_img.setImageResource(resources.getIdentifier("badge_icon_7","drawable","com.monolith.compass"))

                platinum_txt.setText("10回イベントに参加しよう\n" + work.toString() + "/10")
                platinum_pb.setMax(10)
                platinum_pb.setProgress(work)
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_7","drawable","com.monolith.compass"))
            }


        }



    }
}
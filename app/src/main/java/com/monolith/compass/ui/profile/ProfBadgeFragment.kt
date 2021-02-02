package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
                copper_txt.setText("7日ログインしよう")
                copper_img.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))
                silver_txt.setText("30日ログインしよう")
                silver_img.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))
                gold_txt.setText("180日ログインしよう")
                gold_img.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))
                platinum_txt.setText("365日ログインしよう")
                platinum_img.setImageResource(resources.getIdentifier("badge_icon_2","drawable","com.monolith.compass"))
            }
        }



    }
}
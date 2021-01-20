package com.monolith.compass.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R


class ProfEditFragment : Fragment() {


    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        val okbtn : Button = root.findViewById(R.id.combtn)
        val name_edittext :EditText = root.findViewById(R.id.name_txtedit)
        val badge_img : ImageView = root.findViewById(R.id.badge_img)
        val card_img :ImageView = root.findViewById(R.id.card_img)
        val frame :FrameLayout = root.findViewById(R.id.frame)
        val phrase :EditText = root.findViewById(R.id.phrase_txtedit)
        val back_btn :Button = root.findViewById(R.id.back_bt)
        val name_txt :TextView = root.findViewById(R.id.name_txt)

        val ma = activity as MainActivity?

        name_edittext.setText(ma!!.profString[0])
        phrase.setText(ma!!.profString[2])


        card_img.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile_card)
        }

        back_btn.setOnClickListener{
            findNavController().navigate(R.id.navigation_profile)
        }


        badge_img.setOnClickListener{
            frame.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfileBlackFragment())
            transaction.add(R.id.frame,ProfBadgeListFragment())
            transaction.commit()
        }

        okbtn.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile)
        }

        val back_id =ma!!.profInt[2]/10
        val badge_id = ma!!.profInt[2] %10
        val background ="badge_background_$back_id"
        val favbadge ="badge_icon_$badge_id"
        val back = resources.getIdentifier(background,"drawable","com.monolith.compass")
        val badge = resources.getIdentifier(favbadge,"drawable","com.monolith.compass")
        badge_img.setBackgroundResource(back)
        badge_img.setImageResource(badge)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.name_txt)
    }
}

/*
バッジ名:B,S,G,P
ログイン日数 :7,30,180,365
            :30,100,500,1000
レベル:10,50,100,150
      :10,25,50,100
総移動距離(km):10,100,1000,10000
総歩数(万):10,100,500,1000
新規開拓距離:1,10,100,500
消費カロリー:1000,5000,25000,50000
すれ違い人数:1,10,50,100
イベント参加回数:1,3,6,10

 いつか
 https://qiita.com/takahirom/items/97818748e2c2059a2536
 */
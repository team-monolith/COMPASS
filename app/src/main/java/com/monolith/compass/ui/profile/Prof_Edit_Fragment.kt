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


class Prof_Edit_Fragment : Fragment() {


    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        val textView: TextView = root.findViewById(R.id.name_txt)
        val okbtn : Button = root.findViewById(R.id.combtn)
        val name_text :EditText = root.findViewById(R.id.name_txtedit)
        val badge_img : ImageView = root.findViewById(R.id.badge_img)
        val frame :FrameLayout = root.findViewById(R.id.frame)

        val ma = activity as MainActivity?
        //var value =ma?.profileValues

        //textView.text = value

        /*card_img.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile_card)
        }*/

        badge_img.setOnClickListener{
            frame.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfileBlackFragment())
            transaction.add(R.id.frame,Prof_Badge_List_Fragment())
            transaction.commit()
        }

        okbtn.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_edit_to_navigation_profile)
        }
        return root
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
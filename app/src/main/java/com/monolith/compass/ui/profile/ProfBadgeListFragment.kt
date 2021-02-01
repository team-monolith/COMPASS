package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.MainActivity
import com.monolith.compass.R


class ProfBadgeListFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_badge_list, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.ivBlack).setOnClickListener{
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        //1行目
        view.findViewById<ImageView>(R.id.badge01).setOnClickListener {
            setBadge(1,0)
        }
        view.findViewById<ImageView>(R.id.badge02).setOnClickListener {
            setBadge(2,0)
        }
        view.findViewById<ImageView>(R.id.badge03).setOnClickListener {
            setBadge(3,0)
        }
        view.findViewById<ImageView>(R.id.badge04).setOnClickListener {
            setBadge(4,0)
        }
        //2行目
        view.findViewById<ImageView>(R.id.badge05).setOnClickListener {
            setBadge(1,1)
        }
        view.findViewById<ImageView>(R.id.badge06).setOnClickListener {
            setBadge(2,1)
        }
        view.findViewById<ImageView>(R.id.badge07).setOnClickListener {
            setBadge(3,1)
        }
        view.findViewById<ImageView>(R.id.badge08).setOnClickListener {
            setBadge(4,1)
        }
        //3行目
        view.findViewById<ImageView>(R.id.badge09).setOnClickListener {
            setBadge(1,2)
        }
        view.findViewById<ImageView>(R.id.badge10).setOnClickListener {
            setBadge(2,2)
        }
        view.findViewById<ImageView>(R.id.badge11).setOnClickListener {
            setBadge(3,2)
        }
        view.findViewById<ImageView>(R.id.badge12).setOnClickListener {
            setBadge(4,2)
        }
        //4行目
        view.findViewById<ImageView>(R.id.badge13).setOnClickListener {
            setBadge(1,3)
        }
        view.findViewById<ImageView>(R.id.badge14).setOnClickListener {
            setBadge(2,3)
        }
        view.findViewById<ImageView>(R.id.badge15).setOnClickListener {
            setBadge(3,3)
        }
        view.findViewById<ImageView>(R.id.badge16).setOnClickListener {
            setBadge(4,3)
        }
        //5行目
        view.findViewById<ImageView>(R.id.badge17).setOnClickListener {
            setBadge(1,4)
        }
        view.findViewById<ImageView>(R.id.badge18).setOnClickListener {
            setBadge(2,4)
        }
        view.findViewById<ImageView>(R.id.badge19).setOnClickListener {
            setBadge(3,4)
        }
        view.findViewById<ImageView>(R.id.badge20).setOnClickListener {
            setBadge(4,4)
        }
        //6行目
        view.findViewById<ImageView>(R.id.badge21).setOnClickListener {
            setBadge(1,5)
        }
        view.findViewById<ImageView>(R.id.badge22).setOnClickListener {
            setBadge(2,5)
        }
        view.findViewById<ImageView>(R.id.badge23).setOnClickListener {
            setBadge(3,5)
        }
        view.findViewById<ImageView>(R.id.badge24).setOnClickListener {
            setBadge(4,5)
        }
        //7行目
        view.findViewById<ImageView>(R.id.badge25).setOnClickListener {
            setBadge(1,6)
        }
        view.findViewById<ImageView>(R.id.badge26).setOnClickListener {
            setBadge(2,6)
        }
        view.findViewById<ImageView>(R.id.badge27).setOnClickListener {
            setBadge(3,6)
        }
        view.findViewById<ImageView>(R.id.badge28).setOnClickListener {
            setBadge(4,6)
        }
        //8行目
        view.findViewById<ImageView>(R.id.badge29).setOnClickListener {
            setBadge(1,7)
        }
        view.findViewById<ImageView>(R.id.badge30).setOnClickListener {
            setBadge(2,7)
        }
        view.findViewById<ImageView>(R.id.badge31).setOnClickListener {
            setBadge(3,7)
        }
        view.findViewById<ImageView>(R.id.badge32).setOnClickListener {
            setBadge(4,7)
        }
    }

    /*バッチのセット(background:背景,icon:アイコン)*/
    fun setBadge(background:Int,badge_num:Int){
        val ma = activity as MainActivity
        val back = resources.getIdentifier("badge_background_" + background.toString(), "drawable", "com.monolith.compass")
        val badge = resources.getIdentifier("badge_icon_" + badge_num.toString(), "drawable", "com.monolith.compass")
        val fg = parentFragment as ProfEditFragment
        val badge_img = fg.view?.findViewById<ImageView>(R.id.badge_img)
        ma.profBadge[0] = badge_num
        ma.profBadge[1] = background
        badge_img?.setBackgroundResource(back)
        badge_img?.setImageResource(badge)
        parentFragmentManager.beginTransaction().remove(this).commit()
    }

}
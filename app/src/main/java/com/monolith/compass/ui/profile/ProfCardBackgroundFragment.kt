package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.ui.friend.CardViewFragment


class ProfCardBackgroundFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_selectcard, container, false)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, ProfCardBackFragment())
        transaction.commit()

        val ma = activity as MainActivity

        if(ma.profsave[0]==-1){
            ma.profsave[0] = ma.profInt[3]
            ma.profsave[1] = ma.profInt[4]
        }


        val tab=view.findViewById<TabLayout>(R.id.tab)
        tab.getTabAt(0)?.select()
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            var transaction = childFragmentManager.beginTransaction()


            // タブが選択された際に呼ばれる
            override fun onTabSelected(tab: TabLayout.Tab) {

                transaction = childFragmentManager.beginTransaction()

                //現在のモードに合わせて画面を入れ変える
                when (tab.position) {
                    0 -> {
                        transaction.replace(R.id.frame, ProfCardBackFragment()).commit()
                    }
                    1 -> {
                        transaction.replace(R.id.frame, ProfCardFrameFragment()).commit()
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })



        /*
        view.findViewById<Button>(R.id.background_btn).setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_card_background_to_navigation_profile_card_frame)
        }
        */

        view.findViewById<ImageView>(R.id.iv_close).setOnClickListener{
            ma.profsave[0] = -1
            ma.profsave[1] = -1
            findNavController().navigate(R.id.action_navigation_profile_card_to_navigation_profile_edit)
        }

        view.findViewById<ImageView>(R.id.iv_save).setOnClickListener{
            ma.profInt[3] = ma.profsave[0]
            ma.profInt[4] = ma.profsave[1]
            findNavController().navigate(R.id.action_navigation_profile_card_to_navigation_profile_edit)
        }


    }

}
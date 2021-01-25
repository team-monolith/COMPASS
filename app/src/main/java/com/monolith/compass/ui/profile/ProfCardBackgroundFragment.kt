package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R


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
        /*val ma = activity as MainActivity
        if(ma.profsave[0]==-1){
            ma.profsave[0] = ma.profInt[3]
            ma.profsave[1] = ma.profInt[4]
        }
        view.findViewById<ImageView>(R.id.back_img0).setOnClickListener{//これはbackground_0
            ma.profsave[0] = 0
        }
        view.findViewById<ImageView>(R.id.back_img1).setOnClickListener{
            ma.profsave[0] = 1
        }
        view.findViewById<ImageView>(R.id.back_img2).setOnClickListener{
            ma.profsave[0] = 2
        }
        view.findViewById<ImageView>(R.id.back_img3).setOnClickListener{
            ma.profsave[0] = 3
        }
        view.findViewById<ImageView>(R.id.back_img4).setOnClickListener{
            ma.profsave[0] = 4
        }
        view.findViewById<ImageView>(R.id.back_img5).setOnClickListener{
            ma.profsave[0] = 5
        }

        view.findViewById<Button>(R.id.background_btn).setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_card_background_to_navigation_profile_card_frame)
        }
        view.findViewById<Button>(R.id.back_bt).setOnClickListener{
            ma.profsave[0] = -1
            ma.profsave[1] = -1
            findNavController().navigate(R.id.action_navigation_profile_card_to_navigation_profile_edit)
        }
        view.findViewById<Button>(R.id.save_btn).setOnClickListener{
            ma.profInt[3] = ma.profsave[0]
            ma.profInt[4] = ma.profsave[1]
            findNavController().navigate(R.id.action_navigation_profile_card_to_navigation_profile_edit)
        }*/


    }

}
package com.monolith.compass.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        view.findViewById<ImageView>(R.id.frame_img0).setOnClickListener{
            ma.profsave[1]= 0
        }
        view.findViewById<ImageView>(R.id.frame_img1).setOnClickListener{
            ma.profsave[1]= 1
        }
        view.findViewById<ImageView>(R.id.frame_img2).setOnClickListener{
            ma.profsave[1]= 2
        }
        view.findViewById<ImageView>(R.id.frame_img3).setOnClickListener{
            ma.profsave[1]= 3
        }
        view.findViewById<ImageView>(R.id.frame_img4).setOnClickListener{
            ma.profsave[1]= 4
        }
        view.findViewById<ImageView>(R.id.frame_img5).setOnClickListener{
            ma.profsave[1]= 5
        }
        view.findViewById<ImageView>(R.id.frame_img6).setOnClickListener{
            ma.profsave[1]= 6
        }
        view.findViewById<ImageView>(R.id.frame_img7).setOnClickListener{
            ma.profsave[1]= 7
        }
        view.findViewById<ImageView>(R.id.frame_img8).setOnClickListener{
            ma.profsave[1]= 8
        }
        view.findViewById<ImageView>(R.id.frame_img9).setOnClickListener{
            ma.profsave[1]= 9
        }
        view.findViewById<ImageView>(R.id.frame_img10).setOnClickListener{
            ma.profsave[1]= 10
        }



    }

}
package com.monolith.compass.ui.friend

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp


class FriendCardFragment : Fragment() {


    private lateinit var friendViewModel: FriendViewModel
    var count=0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        friendViewModel =
            ViewModelProvider(this).get(FriendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_friend_card, container, false)
        friendViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        val ma = activity as MainActivity
        //メインアクティビティのcardDataListから名刺データを受け取り生成
        val card=view.findViewById<ImageView>(R.id.cardImage)
        card.setImageBitmap(MyApp().CreateCardBitmap(ma.cardDataList,resources))
        view.findViewById<ImageView>(R.id.cardImage).setOnClickListener {
            //名刺拡大画面を閉じる
            //ma.FriendCardLoadStop()
        }
    }
}
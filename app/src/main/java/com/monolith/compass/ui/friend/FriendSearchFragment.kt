package com.monolith.compass.ui.friend

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp


class FriendSearchFragment : Fragment() {


    private lateinit var friendViewModel: FriendViewModel
    var count=0
    val GLOBAL = MyApp.getInstance()
    val ma = activity as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        friendViewModel =
            ViewModelProvider(this).get(FriendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_friend_search, container, false)
        friendViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //メインアクティビティのcardDataListから名刺データを受け取り生成
        val card=view.findViewById<ImageView>(R.id.cardImage)
        card.setImageBitmap(MyApp().CreateCardBitmap(ma.cardDataList,resources))

        //拡大名刺タップ時の動作
        view.findViewById<ImageView>(R.id.imageButtonZoom).setOnClickListener {
            //名刺拡大画面に
            ma.FriendCardLoardStart(0)

        }

        //お気に入りボタンタップ時の動作
        view.findViewById<ImageButton>(R.id.imageSetFav).setOnClickListener{
            view.findViewById<ImageButton>(R.id.imageRemoveFav).visibility=View.VISIBLE
            view.findViewById<ImageButton>(R.id.imageSetFav).visibility=View.INVISIBLE
        }

        //お気に入り登録済みボタンタップ時の動作
        view.findViewById<ImageButton>(R.id.imageRemoveFav).setOnClickListener {
            view.findViewById<ImageButton>(R.id.imageRemoveFav).visibility=View.INVISIBLE
            view.findViewById<ImageButton>(R.id.imageSetFav).visibility=View.VISIBLE
        }



        //バックボタンタップ時動作
        view.findViewById<ImageButton>(R.id.imageButtonRemoveSearch).setOnClickListener {
            ma.removeSearchFriend()
        }




    }
}


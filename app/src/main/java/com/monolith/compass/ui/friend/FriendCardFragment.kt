package com.monolith.compass.ui.friend

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.provider.Settings
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


class FriendCardFragment : Fragment() {


    private lateinit var friendViewModel: FriendViewModel
    var count=0
    val GLOBAL = MyApp.getInstance()

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


        val favIDList=GLOBAL.Favorite_Readlist()

        //Toast.makeText(context,ma.cardDataList.ID.toString(),Toast.LENGTH_SHORT).show()

        val favID=ma.cardDataList.ID



        var favFrag=0
        for (i in favIDList.indices){
            if (favIDList[i]==favID.toString()){
                favFrag=1
            }
        }




        if (favFrag==1){
            view.findViewById<ImageButton>(R.id.imageRemoveFav).visibility=View.VISIBLE
            view.findViewById<ImageButton>(R.id.imageSetFav).visibility=View.INVISIBLE
        }else{
            view.findViewById<ImageButton>(R.id.imageRemoveFav).visibility=View.INVISIBLE
            view.findViewById<ImageButton>(R.id.imageSetFav).visibility=View.VISIBLE
        }







        //メインアクティビティのcardDataListから名刺データを受け取り生成
        val card=view.findViewById<ImageView>(R.id.cardImage)
        card.setImageBitmap(MyApp().CreateCardBitmap(ma.cardDataList,resources))

        //拡大名刺タップ時の動作
        view.findViewById<ImageView>(R.id.cardImage).setOnClickListener {
            //名刺拡大画面を閉じる
            ma.FriendCardLoadStop(0)
        }

        //お気に入りボタンタップ時の動作
        view.findViewById<ImageButton>(R.id.imageSetFav).setOnClickListener{
            GLOBAL.Favorite_add(favID)
            Toast.makeText(context,"お気に入りに登録しました！！",Toast.LENGTH_SHORT).show()
            view.findViewById<ImageButton>(R.id.imageRemoveFav).visibility=View.VISIBLE
            view.findViewById<ImageButton>(R.id.imageSetFav).visibility=View.INVISIBLE

        }

        //お気に入り登録済みボタンタップ時の動作
        view.findViewById<ImageButton>(R.id.imageRemoveFav).setOnClickListener {
            GLOBAL.Favorite_delete(favID)
            Toast.makeText(context,"お気に入りを解除しました！！",Toast.LENGTH_SHORT).show()
            view.findViewById<ImageButton>(R.id.imageRemoveFav).visibility=View.INVISIBLE
            view.findViewById<ImageButton>(R.id.imageSetFav).visibility=View.VISIBLE
            ma.favFrag=1
        }



        //裏面ボタンタップ時動作
        view.findViewById<ImageButton>(R.id.imageToCardBack).setOnClickListener {
            ma.FriendCardLoardStart(1)
        }




    }
}


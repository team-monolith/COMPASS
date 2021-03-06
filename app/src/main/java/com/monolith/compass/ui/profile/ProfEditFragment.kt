package com.monolith.compass.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeResource
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import kotlin.collections.HashMap
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import javax.microedition.khronos.opengles.GL

class ProfEditFragment : Fragment() {


    private lateinit var profileViewModel: ProfileViewModel

    val GLOBAL= MyApp.getInstance()

    var endbool:Boolean=false

    var handler=Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.name_txt)



        val name = view.findViewById<EditText>(R.id.name_txtedit)
        val phrase = view.findViewById<EditText>(R.id.phrase_txtedit)
        //val frame = view.findViewById<FrameLayout>(R.id.frame)
        val badge_img = view.findViewById<ImageView>(R.id.badge_img)
        val icon_img=view.findViewById<ImageView>(R.id.icon_img)
        val card_img = view.findViewById<ImageView>(R.id.card_img)

        if(LoadIconImage()!=null){
            icon_img.setImageBitmap(LoadIconImage())
        }

        //保存ボタン処理
        view.findViewById<ImageView>(R.id.iv_save3).setOnClickListener{
            UploadData(GLOBAL.replace_comma(name.text.toString()),GLOBAL.replace_comma(phrase.text.toString()))
            //iconBufferDelete()
        }
        view.findViewById<ImageView>(R.id.iv_close3).setOnClickListener{
            //iconBufferDelete()
            findNavController().navigate(R.id.navigation_profile)
        }
        icon_img.setOnClickListener{
            findNavController().navigate(R.id.navigation_iconedit)
        }
        badge_img.setOnClickListener{
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeListFragment())
            transaction.commit()

        }
        card_img.setOnClickListener{
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfCardBackgroundFragment())
            transaction.commit()
        }

        name.setText(GLOBAL.cardData.NAME)
        phrase.setText(GLOBAL.cardData.COMMENT)

        badge_img.setBackgroundResource(resources.getIdentifier("badge_background_" + GLOBAL.cardData.BADGEBACK,"drawable","com.monolith.compass"))
        badge_img.setImageResource(resources.getIdentifier("badge_icon_" + GLOBAL.cardData.BADGE,"drawable","com.monolith.compass"))

        card_img.setImageResource(resources.getIdentifier("frame_" + GLOBAL.cardData.FRAME,"drawable","com.monolith.compass"))
        card_img.setBackgroundResource(resources.getIdentifier("card_background_" + GLOBAL.cardData.BACKGROUND,"drawable","com.monolith.compass"))

    }

    fun LoadIconImage(): Bitmap? {
        try{
            val srcFile = File(GLOBAL.DIRECTORY+"/icon.png")
            val fis = FileInputStream(srcFile)
            return BitmapFactory.decodeStream(fis)
        }
        catch(e:Exception){
            return BitmapFactory.decodeResource(resources, R.drawable.default_icon)
        }
    }

    fun iconBufferDelete(){
        val file=File(GLOBAL.DIRECTORY+"/icon.png")
        file.delete()
    }


    fun UploadData(name:String,comment:String){
        val hash = GLOBAL.CreateHash(GLOBAL.getID().toString() + "ok@fewfwaffeefewgweg4ew")
        HandlerDraw()

        val POSTDATA = HashMap<String, String>()

        val baos = ByteArrayOutputStream()
        LoadIconImage()!!.compress(Bitmap.CompressFormat.PNG, 50, baos)
        val iconstr=Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)


        POSTDATA.put("hash",hash)
        POSTDATA.put("id", GLOBAL.getID().toString())
        POSTDATA.put("name",name)
        POSTDATA.put("icon",iconstr)
        POSTDATA.put("comment",comment)
        POSTDATA.put("level",GLOBAL.cardData.LEVEL.toString())
        POSTDATA.put("distance",GLOBAL.cardData.DISTANCE.toString())
        POSTDATA.put("background",GLOBAL.cardData.BACKGROUND.toString())
        POSTDATA.put("frame",GLOBAL.cardData.FRAME.toString())
        POSTDATA.put("badge", GLOBAL.cardData.BADGE.toString())
        POSTDATA.put("badge_background",GLOBAL.cardData.BADGEBACK.toString())
        POSTDATA.put("state",GLOBAL.cardData.STATE.toString())

        "https://b.compass-user.work/system/user/change_user.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        endbool=true
                    }
                    is Result.Failure -> {
                        UploadData(name,comment)
                    }
                }
            }
    }

    //通信終了監視用
    fun HandlerDraw() {
        handler.post(object : Runnable {
            override fun run() {
                if(endbool){
                    findNavController().navigate(R.id.navigation_profile)
                }
                else{
                    handler.postDelayed(this, 0)
                }
            }
        })
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
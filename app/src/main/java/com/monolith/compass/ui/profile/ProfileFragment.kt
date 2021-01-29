package com.monolith.compass.ui.profile


import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.core.Progress
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.MainActivity
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import com.monolith.compass.ui.map.MapFragment
import java.util.*
import kotlin.collections.HashMap


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    val GLOBAL = MyApp.getInstance()

    var CardBitmap: Bitmap?=null

    val handler=Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }

    fun getResouceId(accid: Int): Int {
        val background = "badge_background_$accid"
        val back = resources.getIdentifier(background, "drawable", "com.monolith.compass")
        return back
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        HandlerDraw(view)

        val imgCard = view.findViewById<ImageView>(R.id.card_img)
        val frame = view.findViewById<FrameLayout>(R.id.frame)
        val back_fl = view.findViewById<FrameLayout>(R.id.back_fl)
        //txt
        view.findViewById<TextView>(R.id.day_txt)
        view.findViewById<TextView>(R.id.distance_txt)
        view.findViewById<TextView>(R.id.step_txt)
        view.findViewById<TextView>(R.id.development_txt)
        view.findViewById<TextView>(R.id.calorie_txt)
        view.findViewById<TextView>(R.id.friend_txt)
        view.findViewById<TextView>(R.id.event_txt)
        //img
        val day_img = view.findViewById<ImageView>(R.id.day_img)
        val level_img = view.findViewById<ImageView>(R.id.level_img)
        val distance_img = view.findViewById<ImageView>(R.id.distance_img)
        val step_img = view.findViewById<ImageView>(R.id.step_img)
        val dev_img = view.findViewById<ImageView>(R.id.development_img)
        val calo_img = view.findViewById<ImageView>(R.id.calorie_img)
        val friend_img = view.findViewById<ImageView>(R.id.friend_img)
        val event_img = view.findViewById<ImageView>(R.id.event_img)
        //progress bar
        view.findViewById<ProgressBar>(R.id.day_pb)
        view.findViewById<ProgressBar>(R.id.level_pb)
        view.findViewById<ProgressBar>(R.id.distance_pb)
        view.findViewById<ProgressBar>(R.id.step_pb)
        view.findViewById<ProgressBar>(R.id.development_pb)
        view.findViewById<ProgressBar>(R.id.calorie_pb)
        view.findViewById<ProgressBar>(R.id.friend_pb)
        view.findViewById<ProgressBar>(R.id.event_pb)

        imgCard.scaleX=0.3f
        imgCard.scaleY=0.3f

        // アニメーションをセットする

        val gifMovie: Int = R.drawable.load

        // gif画像のセット
        Glide.with(this).load(gifMovie).into(imgCard)

        getUserData(imgCard)

        val ma = activity as MainActivity?
        day_img.setBackgroundResource(getResouceId(ma!!.profInt[5] / 10000000))
        level_img.setBackgroundResource(getResouceId(ma.profInt[5] / 1000000 % 10))
        distance_img.setBackgroundResource(getResouceId(ma.profInt[5] / 100000 % 10))
        step_img.setBackgroundResource(getResouceId(ma.profInt[5] / 10000 % 10))
        dev_img.setBackgroundResource(getResouceId(ma.profInt[5] / 1000 % 10))
        calo_img.setBackgroundResource(getResouceId(ma.profInt[5] / 100 % 10))
        friend_img.setBackgroundResource(getResouceId(ma.profInt[5] / 10 % 10))
        event_img.setBackgroundResource(getResouceId(ma.profInt[5] % 10))

        imgCard.setImageResource(R.drawable.ic_launcher_background)


        day_img.setOnClickListener {
            frame.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl, ProfileBlackFragment())
            transaction.add(R.id.frame, ProfBadgeFragment())
            transaction.commit()
        }

        level_img.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_edit)
        }

        distance_img.setOnClickListener {
        }
        imgCard.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_edit)
        }
    }

    //描画関数　再描画用
    fun HandlerDraw(view:View) {
        handler.post(object : Runnable {
            override fun run() {
                if(CardBitmap!=null){
                    val iv=view.findViewById<ImageView>(R.id.card_img)
                    iv.setImageBitmap(CardBitmap)
                    iv.scaleX=1f
                    iv.scaleY=1f
                }
                else{
                    handler.postDelayed(this, 25)
                }
            }
        })
    }

    fun getUserData(card: ImageView) {

        val POSTDATA = HashMap<String, String>()

        POSTDATA.put("id", GLOBAL.getID().toString())

        "https://b.compass-user.work/system/user/show_user.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {   
                        CardBitmap=MyApp().CreateCardBitmap(setData(String(response.data)),resources)
                    }
                    is Result.Failure -> {
                    }
                }
            }
    }

    fun setData(data: String): MyApp.CARDDATA {
        val scan = Scanner(data.replace("<br>", ""))
        scan.useDelimiter(",|\n")

        var buf: String = scan.next()

        val ID: Int
        if (buf != "") ID = buf.toInt()
        else ID = 0

        buf = scan.next()
        val NAME: String
        if (buf != "") NAME = buf
        else NAME = "NAME"

        buf = scan.next()
        val ICON: String
        if (buf != "") ICON = buf
        else ICON = "ICON"

        buf = scan.next()
        val LEVEL: Int
        if (buf != "") LEVEL = buf.toInt()
        else LEVEL = 0

        buf = scan.next()
        val DISTANCE: Int
        if (buf != "") DISTANCE = buf.toInt()
        else DISTANCE = 0

        buf = scan.next()
        val BADGE: Int
        if (buf != "") BADGE = buf.toInt()
        else BADGE = 0

        buf = scan.next()
        val BADGEBACK: Int
        if (buf != "") BADGEBACK = buf.toInt()
        else BADGEBACK = 0

        buf = scan.next()
        val FRAME: Int
        if (buf != "") FRAME = buf.toInt()
        else FRAME = 0

        buf = scan.next()
        val BACK: Int
        if (buf != "") BACK = buf.toInt()
        else BACK = 0

        buf = scan.next()
        val COMMENT: String
        if (buf != "") COMMENT = buf
        else COMMENT = ""

        val STATE: Int
        if (scan.hasNext()) {
            buf = scan.next()
            if (buf != "") STATE = buf.toInt()
            else STATE = 0
        } else {
            STATE = 0
        }

        return MyApp.CARDDATA(
            ID,
            NAME,
            GLOBAL.IconBitmapCreate(ICON),
            LEVEL,
            DISTANCE,
            BADGE,
            BADGEBACK,
            FRAME,
            BACK,
            COMMENT,
            STATE
        )
    }
}

/*
GLOBALSETTING------------------------------------

->サーバーとの通信に使用するデータ
ID(int)
名前(string)
アイコン(string)
総距離(int)
お気に入りバッジ(int)
ひとこと(string)
名刺背景(int)
名刺フレーム(int)
バッジ状態(int)
-------------------------------------------------

LOCALSETTING-------------------------------------

->ユーザ情報等、アプリ内の設定を保存

身長(float)
体重(float)
目標歩数(int)
GPS取得設定(int)
自宅座標(float,float)
非取得範囲(int)
マイカラー(rgb)
-------------------------------------------------

ACTIVITYLOG------------------------------------------

->歩数データを保存。Fitnessはこのファイルを参照

日付(date)
目標歩数(int)
歩数(int)
距離(int)
消費カロリー(int)
-------------------------------------------------

GPSLOG-------------------------------------------

->GPSの全履歴を保存。書き込みはBUFと並列

日付(date)
時間(time)
経度(float)
緯度(float)
範囲(float)
速度(float)
-------------------------------------------------
GPSBUF-------------------------------------------

->GPSのサーバー未送信分履歴を保存

日付(date)
時間(time)
経度(float)
緯度(float)
範囲(float)
速度(float)
-------------------------------------------------
FRIEND-------------------------------------------

->すれ違ったフレンドのIDを保存

ID(int)
-------------------------------------------------

FAVORITE-----------------------------------------

->お気に入りのユーザを保存

ID(int)
-------------------------------------------------

MAPLOG-------------------------------------------

->マップのバッファリングに使用（すると思われる)

-------------------------------------------------



fragment savedinstancestateについて
https://stackoverflow.com/questions/20550016/savedinstancestate-is-always-null-in-fragment

入力領域の空白自動削除
 val text = editText.text.toString().trim()

必要と思われるデータ
開拓ポイント（新旧２種類）
各バッジの獲得に必要なポイント
名刺の背景・フレーム

今日やること
arrayをつかったデータの受け渡し？
どこでどんなデータを必要とするかの正確な洗い出し



名刺------------------------
・ID
・名前
・ユーザレベル
・アイコン
・総距離
・お気に入りバッジID
・ひとこと
・名刺背景色ID
・名刺フレームID
・バッジの状態（8桁の整数で管理）



バッチ処理時に通信する内容ーーーーーーーーーーーーー
・地図の変更（区間を経度緯度でx分割してフラグ管理、変更分のみアップデート）
 Lバイナリで管理する
・自分の現状の名刺データ

日付変更処理に通信する内容ーーーーーーーーーーーーー
・地図の更新

適宜更新時に取得する内容ーーーーーーーーーーーーーー
・他のユーザの名刺データ


サーバで保持するべき情報ーーーーーーーーーーーーーー
・ID(8桁整数)
・名前(2バイト10文字）
・ユーザレベル(3桁整数）
・アイコン（512x512,50kB程度)
・総距離(8桁整数）
・お気に入りバッジID(2桁整数）
・ひとこと（2バイト50文字)
・名刺背景色ID(2桁整数）
・名刺フレームID（2桁整数）
・バッジの状態（8桁の整数で管理）

・地図のマスタデータ200000x180000(csv)

バッジID
0レベル
1累計歩数
2消費カロリー
3累計移動距離
4新規開拓
5すれ違い
6イベント
7ログイン

 */
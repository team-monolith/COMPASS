package com.monolith.compass.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.monolith.compass.MainActivity
import com.monolith.compass.R


class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        setAppearance(root)
        return root
    }

    fun getResouceId(accid:Int): Int {
        val background ="badge_background_$accid"
        val back = resources.getIdentifier(background,"drawable","com.monolith.compass")
        return back
    }
    fun setAppearance(root:View){
        val imgCard: ImageView = root.findViewById(R.id.card_img)

        val frame :FrameLayout =root.findViewById(R.id.frame)
        val edit_btn:Button = root.findViewById(R.id.edit)
        val back_fl:FrameLayout =root.findViewById(R.id.back_fl)
        //txt
        val day_txt:TextView = root.findViewById(R.id.day_txt)
        val distance_txt :TextView =root.findViewById(R.id.distance_txt)
        val step_txt :TextView =root.findViewById(R.id.step_txt)
        val dev_txt :TextView =root.findViewById(R.id.development_txt)
        val calo_txt :TextView =root.findViewById(R.id.calorie_txt)
        val friend_txt :TextView =root.findViewById(R.id.friend_txt)
        val event_txt :TextView =root.findViewById(R.id.event_txt)
        //img
        val day_img:ImageView = root.findViewById(R.id.day_img)
        val level_img:ImageView = root.findViewById(R.id.level_img)
        val distance_img:ImageView = root.findViewById(R.id.distance_img)
        val step_img:ImageView = root.findViewById(R.id.step_img)
        val dev_img:ImageView = root.findViewById(R.id.development_img)
        val calo_img:ImageView = root.findViewById(R.id.calorie_img)
        val friend_img:ImageView = root.findViewById(R.id.friend_img)
        val event_img:ImageView = root.findViewById(R.id.event_img)
        //progress bar
        val day_pb:ProgressBar = root.findViewById(R.id.day_pb)
        val level_pb:ProgressBar = root.findViewById(R.id.level_pb)
        val dis_pb:ProgressBar = root.findViewById(R.id.distance_pb)
        val step_pb:ProgressBar = root.findViewById(R.id.step_pb)
        val dev_pb:ProgressBar = root.findViewById(R.id.development_pb)
        val calo_pb:ProgressBar = root.findViewById(R.id.calorie_pb)
        val friend_pb:ProgressBar = root.findViewById(R.id.friend_pb)
        val event_pb:ProgressBar = root.findViewById(R.id.event_pb)


        val ma = activity as MainActivity?
        day_img.setBackgroundResource(getResouceId(ma!!.profInt[5]/10000000))
        level_img.setBackgroundResource(getResouceId(ma!!.profInt[5]/1000000%10))
        distance_img.setBackgroundResource(getResouceId(ma!!.profInt[5]/100000%10))
        step_img.setBackgroundResource(getResouceId(ma!!.profInt[5]/10000%10))
        dev_img.setBackgroundResource(getResouceId(ma!!.profInt[5]/1000%10))
        calo_img.setBackgroundResource(getResouceId(ma!!.profInt[5]/100%10))
        friend_img.setBackgroundResource(getResouceId(ma!!.profInt[5]/10%10))
        event_img.setBackgroundResource(getResouceId(ma!!.profInt[5]%10))




        //var value =ma?.profileValues


        imgCard.setImageResource(R.drawable.ic_launcher_background)


        day_img.setOnClickListener{
            frame.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfileBlackFragment())
            transaction.add(R.id.frame,Prof_Badge_Fragment())
            transaction.commit()
            edit_btn.isEnabled =false
        }

        level_img.setOnClickListener{
            frame.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfileBlackFragment())
            transaction.add(R.id.frame,Prof_Badge_List_Fragment())
            transaction.commit()
            edit_btn.isEnabled =false
        }

        distance_img.setOnClickListener{
            back_fl.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,Prof_Card_Fragment())
            transaction.commit()
            edit_btn.isEnabled =false
        }

        imgCard.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_edit)
        }
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
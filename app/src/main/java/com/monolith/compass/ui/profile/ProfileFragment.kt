package com.monolith.compass.ui.profile


import android.graphics.Bitmap
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
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
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
        GLOBAL.calc()
        GLOBAL.login_cnt()
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

        GetData()

        getUserData(imgCard)

        SetProgressData()


        imgCard.setImageResource(R.drawable.ic_launcher_background)


        day_img.setOnClickListener {
            GLOBAL.click_badge = "day"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
        }

        level_img.setOnClickListener {
            GLOBAL.click_badge = "level"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
            //findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_edit)
        }

        distance_img.setOnClickListener {
            GLOBAL.click_badge = "distance"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
        }

        step_img.setOnClickListener {
            GLOBAL.click_badge = "step"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
        }

        dev_img.setOnClickListener{
            GLOBAL.click_badge = "dev"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
        }

        calo_img.setOnClickListener {
            GLOBAL.click_badge = "calo"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
        }

        friend_img.setOnClickListener{
            GLOBAL.click_badge = "friend"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
        }

        event_img.setOnClickListener{
            GLOBAL.click_badge = "event"
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.back_fl,ProfBadgeFragment())
            transaction.commit()
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
        val hash = GLOBAL.CreateHash( "kolwegoewgkowope:g")
        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("hash",hash)
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

    fun SetProgressData(){
        //ログイン日数にセット
        var work = 0
        var disp_max = ""

        val day_img = view?.findViewById<ImageView>(R.id.day_img)
        val day_txt = view?.findViewById<TextView>(R.id.day_txt)
        val day_prog = view?.findViewById<ProgressBar>(R.id.day_pb)
        work = GLOBAL.progressData.LOGIN_DAY

        if(work < 7){ //ログイン日数プログレスバーの最大値を設定
            day_prog?.setMax(7)
            disp_max = "7"
            day_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 30){
            day_prog?.setMax(30)
            disp_max = "30"
            day_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work <   180){
            day_prog?.setMax(180)
            disp_max = "180"
            day_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work <180){
            day_prog?.setMax(365)
            disp_max = "365"
            day_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        day_img?.setImageResource(resources.getIdentifier("badge_icon_0","drawable","com.monolith.compass"))
        day_txt?.setText(disp_max +"日ログインしよう\n" + GLOBAL.progressData.LOGIN_DAY.toString() + "/" +  disp_max)
        day_prog?.setProgress(GLOBAL.progressData.LOGIN_DAY)

        //レベルにセット
        val lvl_img = view?.findViewById<ImageView>(R.id.level_img)
        val lvl_txt = view?.findViewById<TextView>(R.id.level_txt)
        val lvl_prog = view?.findViewById<ProgressBar>(R.id.level_pb)
        work = GLOBAL.progressData.BADGE_LEVEL
        if(work < 10){ //ログイン日数プログレスバーの最大値を設定
            lvl_prog?.setMax(10)
            disp_max = "10"
            lvl_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 25){
            lvl_prog?.setMax(25)
            disp_max = "25"
            lvl_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work < 50){
            lvl_prog?.setMax(50)
            disp_max = "50"
            lvl_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work < 180){
            lvl_prog?.setMax(180)
            disp_max = "180"
            lvl_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        lvl_img?.setImageResource(resources.getIdentifier("badge_icon_1","drawable","com.monolith.compass"))
        lvl_txt?.setText(disp_max + "レベルに上げよう\n"+GLOBAL.progressData.BADGE_LEVEL.toString() + "/" + disp_max)
        lvl_prog?.setProgress(GLOBAL.progressData.BADGE_LEVEL)

        //
        val distance_img = view?.findViewById<ImageView>(R.id.distance_img)
        val distance_txt = view?.findViewById<TextView>(R.id.distance_txt)
        val distance_prog = view?.findViewById<ProgressBar>(R.id.distance_pb)
        work = GLOBAL.progressData.BADGE_DISTANCE
        if(work < 10){
            distance_prog?.setMax(10)
            disp_max = "10"
            distance_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 100){
            distance_prog?.setMax(100)
            disp_max = "100"
            distance_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work < 1000){
            distance_prog?.setMax(1000)
            disp_max = "1000"
            distance_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work < 10000){
            distance_prog?.setMax(10000)
            disp_max = "10000"
            distance_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        distance_img?.setImageResource(resources.getIdentifier("badge_icon_2","drawable","com.monolith.compass"))
        distance_txt?.setText(disp_max + "km 進めよう\n"+GLOBAL.progressData.BADGE_DISTANCE.toString() + "/" + disp_max)
        distance_prog?.setProgress(GLOBAL.progressData.BADGE_DISTANCE)

        val step_img = view?.findViewById<ImageView>(R.id.step_img)
        val step_txt = view?.findViewById<TextView>(R.id.step_txt)
        val step_prog = view?.findViewById<ProgressBar>(R.id.step_pb)
        work = GLOBAL.progressData.STEPS
        if(work < 100000){ //ログイン日数プログレスバーの最大値を設定
            step_prog?.setMax(100000)
            disp_max = "10"
            step_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 1000000){
            step_prog?.setMax(1000000)
            disp_max = "100"
            step_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work < 5000000){
            step_prog?.setMax(5000000)
            disp_max = "500"
            step_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work < 10000000){
            step_prog?.setMax(10000000)
            disp_max = "1000"
            step_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        step_img?.setImageResource(resources.getIdentifier("badge_icon_3","drawable","com.monolith.compass"))
        step_txt?.setText(disp_max + "万歩 歩こう\n" + (GLOBAL.progressData.STEPS / 10000).toString() + "/" + disp_max)
        step_prog?.setProgress(GLOBAL.progressData.STEPS)

        val dev_img = view?.findViewById<ImageView>(R.id.development_img)
        val dev_txt = view?.findViewById<TextView>(R.id.development_txt)
        val dev_prog = view?.findViewById<ProgressBar>(R.id.development_pb)
        work = GLOBAL.progressData.DEV_DISTANCE
        if(work < 1){ //ログイン日数プログレスバーの最大値を設定
            dev_prog?.setMax(1)
            disp_max = "1"
            dev_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 10){
            dev_prog?.setMax(10)
            disp_max = "10"
            dev_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work < 100){
            dev_prog?.setMax(100)
            disp_max = "100"
            dev_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work < 500){
            dev_prog?.setMax(500)
            disp_max = "500"
            dev_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        dev_img?.setImageResource(resources.getIdentifier("badge_icon_4","drawable","com.monolith.compass"))
        dev_txt?.setText(disp_max + "km 新規開拓しよう\n" +GLOBAL.progressData.DEV_DISTANCE.toString() + "/" + disp_max)
        dev_prog?.setProgress(GLOBAL.progressData.DEV_DISTANCE)

        val cal_img = view?.findViewById<ImageView>(R.id.calorie_img)
        val cal_txt = view?.findViewById<TextView>(R.id.calorie_txt)
        val cal_prog = view?.findViewById<ProgressBar>(R.id.calorie_pb)
        work = GLOBAL.progressData.CONS_CAL
        if(work < 1000){ //ログイン日数プログレスバーの最大値を設定
            cal_prog?.setMax(1000)
            disp_max = "1000"
            cal_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 5000){
            cal_prog?.setMax(5000)
            disp_max = "5000"
            cal_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work < 25000){
            cal_prog?.setMax(25000)
            disp_max = "25000"
            cal_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work < 50000){
            cal_prog?.setMax(50000)
            disp_max = "50000"
            cal_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        cal_img?.setImageResource(resources.getIdentifier("badge_icon_5","drawable","com.monolith.compass"))
        cal_txt?.setText(disp_max + "kcal 消費しよう\n" +  GLOBAL.progressData.CONS_CAL.toString() + "/" + disp_max)
        cal_prog?.setProgress(GLOBAL.progressData.CONS_CAL)

        val friend_img = view?.findViewById<ImageView>(R.id.friend_img)
        val friend_txt = view?.findViewById<TextView>(R.id.friend_txt)
        val friend_prog = view?.findViewById<ProgressBar>(R.id.friend_pb)
        work = GLOBAL.progressData.PASSING
        if(work < 1){ //ログイン日数プログレスバーの最大値を設定
            friend_prog?.setMax(1)
            disp_max = "1"
            friend_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 10){
            friend_prog?.setMax(10)
            disp_max = "10"
            friend_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work < 50){
            friend_prog?.setMax(50)
            disp_max = "50"
            friend_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work < 100){
            friend_prog?.setMax(100)
            disp_max = "100"
            friend_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        friend_img?.setImageResource(resources.getIdentifier("badge_icon_6","drawable","com.monolith.compass"))
        friend_txt?.setText(disp_max + "人とすれ違う\n" +GLOBAL.progressData.PASSING.toString() + "/" + disp_max)
        friend_prog?.setProgress(GLOBAL.progressData.PASSING)

        val event_img = view?.findViewById<ImageView>(R.id.event_img)
        val event_txt = view?.findViewById<TextView>(R.id.event_txt)
        val event_prog = view?.findViewById<ProgressBar>(R.id.event_pb)
        work = GLOBAL.progressData.EVENT
        if(work < 1){ //ログイン日数プログレスバーの最大値を設定
            event_prog?.setMax(1)
            disp_max = "1"
            event_img?.setBackgroundResource(resources.getIdentifier("badge_background_1","drawable","com.monolith.compass"))
        }else if(work < 3){
            event_prog?.setMax(3)
            disp_max = "3"
            event_img?.setBackgroundResource(resources.getIdentifier("badge_background_2","drawable","com.monolith.compass"))
        }else if(work < 6){
            event_prog?.setMax(6)
            disp_max = "6"
            event_img?.setBackgroundResource(resources.getIdentifier("badge_background_3","drawable","com.monolith.compass"))
        }else if(work < 10){
            event_prog?.setMax(10)
            disp_max = "10"
            event_img?.setBackgroundResource(resources.getIdentifier("badge_background_4","drawable","com.monolith.compass"))
        }
        event_img?.setImageResource(resources.getIdentifier("badge_icon_7","drawable","com.monolith.compass"))
        event_txt?.setText(disp_max + "回イベントに参加しよう\n" + GLOBAL.progressData.EVENT.toString() + "/" + disp_max)
        event_prog?.setProgress(GLOBAL.progressData.EVENT)


    }

    fun GetData(){
        var step_sum=0
        var distance_sum = 0
        var kcal_sum = 0

        for(i in GLOBAL.ACTIVITY_LOG.indices){
            step_sum += GLOBAL.ACTIVITY_LOG[i].STEP
            distance_sum += GLOBAL.ACTIVITY_LOG[i].DISTANCE
            kcal_sum += GLOBAL.ACTIVITY_LOG[i].CAL
        }

        GLOBAL.progressData.STEPS = step_sum
        GLOBAL.progressData.BADGE_DISTANCE = distance_sum
        GLOBAL.progressData.CONS_CAL = kcal_sum
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
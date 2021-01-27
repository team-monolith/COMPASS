package com.monolith.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.monolith.compass.com.monolith.compass.MyApp
import com.monolith.compass.ui.friend.FriendCardFragment
import com.monolith.compass.ui.map.NavChoiceFragment
import com.monolith.compass.ui.setting.SettingFragment
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class MainActivity : AppCompatActivity(), NavChoiceFragment.OnClickListener,
    SettingFragment.OnClickListener, EasyPermissions.PermissionCallbacks {

    private val GLOBAL = MyApp.getInstance()    //グローバル変数宣言用

    private lateinit var locationManager: LocationManager   //ロケーションマネージャーインスタンス保管用

    //植田テスト用
    var cardDataList = MyApp.CARDDATA(0, "", null, 0, 0, 0, 0, 0, "", 0)


    //この3つは吉田のテスト用
    //var profString = arrayOfNulls<String>(3)//name,icon,phrase
    var profString = arrayOf("よしだ", "nasideii", "よろしくお願いします。")

    //var profInt = arrayOfNulls<Int>(6)//id,distance,favbadge,background,frame,badge
    var profInt = arrayOf(12345, 130, 47, 1, 2, 11002233)

    // profsave = backgound,frame,save
    var profsave = arrayOf(-1, -1)
    //ここまで吉田


    var itemselectedlog: Int? = null    //直近アイテム選択ログ保管用

    companion object {
        private val REQUEST_CODE = 0
        private const val READ_REQUEST_CODE: Int = 42
    }

    @SuppressLint("CutPasteId")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //カレントディレクトリを設定しデータを読み込む
        GLOBAL.DIRECTORY = "$filesDir"

        FirstCheck()

        //アイテムIDを設定する
        itemselectedlog =
            findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_profile).itemId
        RequestPermission()

        //カード背景を確認のためおいてます、動作確認時はコメントアウトを消してMyAPPでブレーク置いてね
        //GLOBAL.CreateBackBitmap(MyApp.CARDDATA(1,"テスト",null,12,10000000,1,2,3,"TEST",1234344),resources)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //ナビゲーションバーのコントローラー設定
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_profile -> {
                    //選択したアイテムIDを保持
                    itemselectedlog = it.itemId
                    //NavChoiceフラグメントが起動している場合は削除
                    NavChoiceDelete()
                    //画面を遷移する
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_profile)
                }
                R.id.navigation_fitness -> {
                    itemselectedlog = it.itemId
                    NavChoiceDelete()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_fitness)
                }
                R.id.navigation_navchoice -> {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    //非表示の場合は表示、表示している場合は削除
                    if (supportFragmentManager.findFragmentByTag("NAVCHOICE") == null) {
                        fragmentTransaction.add(
                            R.id.nav_host_fragment,
                            NavChoiceFragment(),
                            "NAVCHOICE"
                        ).commit()
                        //タブ選択アイテムをマップに変更
                        findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_navchoice).isChecked =
                            true
                    } else {
                        fragmentTransaction.remove(supportFragmentManager.findFragmentByTag("NAVCHOICE")!!)
                            .commit()
                        //タブ選択アイテムを元に戻す
                        findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(
                            itemselectedlog!!
                        ).isChecked = true

                        findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_navchoice).isChecked =
                            true

                    }
                }
                R.id.navigation_friend -> {
                    itemselectedlog = it.itemId
                    NavChoiceDelete()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_friend)
                }
                R.id.navigation_setting -> {
                    itemselectedlog = it.itemId
                    NavChoiceDelete()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_setting)
                }
            }

            return@setOnNavigationItemSelectedListener false
        }


    }

    private fun NavChoiceDelete() {
        val NC = supportFragmentManager.findFragmentByTag("NAVCHOICE")
        if (NC != null) {
            supportFragmentManager.beginTransaction().remove(NC).commit()
        }
    }

    override fun onPause() {
        super.onPause()
        //バックグラウンド処理を完成させたらコメントアウトを外す
        //startBackgroundLocationService()
    }

    override fun onStop() {
        super.onStop()
        val POSTDATA = HashMap<String, String>()
        val data = GLOBAL.FileRead("GPSBUF.txt")
        POSTDATA.put("json", data)
        "https://a.compass-user.work/system/map/receive_csv.php".httpPost(POSTDATA.toList())
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {

                    }
                    is Result.Failure -> {
                    }
                }
            }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        RequestPermission()
        startBackgroundLocationService()
        MyApp().GPSFileRead("GPSLOG.txt")
    }

    //戻るボタン無効化、そのうちもどす
    override fun onBackPressed() {}

    //必要なパーミッションの許可を一括で取得
    //一つでも欠けている場合はfalseを返却
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun RequestPermission() {

        //取得するべきパーミッションを配列で保存
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //パーミッションが許可されているか確認
        //if文の中は「パーミッションが許可されていない」際の処理
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            //パーミッションを取得する
            EasyPermissions.requestPermissions(
                this,
                "許可されていない権限があります。\nアプリ利用の為許可をお願いします。",
                REQUEST_CODE,
                *permissions
            )
        }

    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //ユーザの許可が得られた際に呼出
        recreate()//とりあえず再起動
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //ユーザの許可が得られなかった際に呼出
        finish()//とりあえず終了処理
    }

    //SettingFragmentからのコール
    //GPS開始処理
    override fun onClick_start() {
        startBackgroundLocationService()
        MyApp().toastMake(this, "計測を開始します")
    }

    //SettingFragmentからのコール
    //GPS停止処理
    override fun onClick_stop() {
        stopBackgroundLocationService()
        MyApp().toastMake(this, "計測を終了します")
    }

    override fun onClick_Back() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.remove(supportFragmentManager.findFragmentByTag("NAVCHOICE")!!).commit()
        findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(itemselectedlog!!).isChecked =
            true
    }

    //NavChoiceFragmentからのコール
    //画面遷移及びタブの選択変更処理
    override fun onClick_Map() {
        //NavChoiceフラグメントを削除
        NavChoiceDelete()
        //選択ログを設定
        itemselectedlog =
            findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_navchoice).itemId
        //画面遷移
        findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_map)
    }

    //NavChoiceFragmentからのコール
    //画面遷移及びタブの選択変更処理
    override fun onClick_Event() {
        //NavChoiceフラグメントを削除
        NavChoiceDelete()
        //選択ログを設定
        itemselectedlog =
            findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_navchoice).itemId
        //画面遷移
        findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_event)
    }

    //位置情報取得を開始
    private fun startBackgroundLocationService() {
        val intent = Intent(application, LocationService::class.java)
        startForegroundService(intent)
    }

    //位置情報取得を終了
    private fun stopBackgroundLocationService() {
        val intent = Intent(application, LocationService::class.java)
        stopService(intent)
    }

    //(activity as MainActivity).LoadStart()で呼出ができる

    //ロード表示を開始
    fun LoadStart() {

        LoadStop()

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (supportFragmentManager.findFragmentByTag("LOADING") == null) {
            fragmentTransaction.add(
                R.id.nav_host_fragment,
                LoadingFragment(),
                "LOADING"
            ).commit()
        }

    }

    //ロード表示を終了
    fun LoadStop() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (supportFragmentManager.findFragmentByTag("LOADING") != null) {
            fragmentTransaction.remove(supportFragmentManager.findFragmentByTag("LOADING")!!)
                .commit()
        }
    }

    //ギャラリーを展開、Profile->IconEditFragmentより参照
    fun open_gallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    //写真が選択された後の動き
    //IconEditFragmentにつながる（非結合
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        GLOBAL.ImageBuffer = image
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    //拡大名刺画面の表示
    fun FriendCardLoardStart() {
        val friendFragmentTransaction = supportFragmentManager.beginTransaction()

        if (supportFragmentManager.findFragmentByTag("FRIENDCARD") == null) {
            friendFragmentTransaction.add(
                R.id.nav_host_fragment,
                FriendCardFragment(),
                "FRIENDCARD"
            ).commit()
        }
    }

    fun FriendCardLoadStop() {
        val friendFragmentTransaction = supportFragmentManager.beginTransaction()

        if (supportFragmentManager.findFragmentByTag("FRIENDCARD") != null) {
            friendFragmentTransaction.remove(supportFragmentManager.findFragmentByTag("FRIENDCARD")!!)
                .commit()
        }
    }

    fun FirstCheck(){
        if(GLOBAL.getID()==-1){
            val POSTDATA = HashMap<String, String>()
            "https://b.compass-user.work/system/user/add_user.php".httpPost(POSTDATA.toList())
                .response { _, response, result ->
                    when (result) {
                        is Result.Success -> {
                            String(response.data)
                            GLOBAL.FileWrite(String(response.data),"ID.txt")
                        }
                        is Result.Failure -> {
                        }
                    }
                }
        }
    }

}
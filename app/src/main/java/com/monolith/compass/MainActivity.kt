package com.monolith.compass

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.monolith.compass.com.monolith.compass.MyApp
import com.monolith.compass.ui.fitness.FitnessFragment
import com.monolith.compass.ui.map.MapFragment
import com.monolith.compass.ui.map.NavChoiceFragment
import com.monolith.compass.ui.setting.SettingFragment
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.floor


class MainActivity : AppCompatActivity(), LocationListener, NavChoiceFragment.OnClickListener,
    SettingFragment.OnClickListener,EasyPermissions.PermissionCallbacks{

    private val GLOBAL = MyApp.getInstance()    //グローバル変数宣言用

    private lateinit var locationManager: LocationManager   //ロケーションマネージャーインスタンス保管用

    var SharedValue = "こんにちは"

    var itemselectedlog: Int? = null    //直近アイテム選択ログ保管用

    companion object{
        private val REQUEST_CODE = 0
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //カレントディレクトリを設定しデータを読み込む
        GLOBAL.DIRECTORY = "$filesDir"

        //データを読み込みプリロードする処理を書きたい（書きたいだけ）
        //MyApp().GPSFileRead("GPSLOG.txt")
        //MyApp().setMap(MyApp().convertMapFileData(MyApp().FileRead("MAPLOG.txt")))

        //アイテムIDを設定する
        itemselectedlog =
            findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_profile).itemId

        RequestPermission() 


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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        RequestPermission()
        stopBackgroundLocationService()
        startLocationService()
        MyApp().GPSFileRead("GPSLOG.txt")
    }

    //戻るボタン無効化、そのうちもどす
    override fun onBackPressed() {}

    //必要なパーミッションの許可を一括で取得
    //一つでも欠けている場合はfalseを返却
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun RequestPermission(){

        //取得するべきパーミッションを配列で保存
        val permissions=arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //パーミッションが許可されているか確認
        //if文の中は「パーミッションが許可されていない」際の処理
        if(!EasyPermissions.hasPermissions(this,*permissions)){
            //パーミッションを取得する
            EasyPermissions.requestPermissions(this,"許可されていない権限があります。\nアプリ利用の為許可をお願いします。", REQUEST_CODE,*permissions)
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

    private fun startLocationService() {
        Log.d("debug", "locationStart()")

        // Instances of LocationManager class must be obtained using Context.getSystemService(Class)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled")
        } else {
            // to prompt setting up GPS
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            Log.d("debug", "not gpsEnable, startActivity")
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000
            )

            Log.d("debug", "checkSelfPermission false")
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            100,
            1f,
            this
        )
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
        val test: Application = application
        startForegroundService(intent)
    }

    //位置情報取得を終了
    private fun stopBackgroundLocationService() {
        val intent = Intent(application, LocationService::class.java)
        stopService(intent)
    }

    override fun onLocationChanged(location: Location) {

        //GPS取得時にデータを一時保持
        GLOBAL.GPS_BUF.GPS_Y = (floor(location.latitude * 10000.0) / 10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_X = (floor(location.longitude * 10000.0) / 10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_A = (floor(location.accuracy * 10000.0) / 10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_S = (floor(location.speed * 10000.0) / 10000.0).toFloat()

        //誤差が大きい場合はそもそも記録しない
        if (GLOBAL.GPS_BUF.GPS_A!! > 15f) return

        val last = GLOBAL.GPS_LOG.lastIndex

        val filestr: String =
            "X=" + GLOBAL.GPS_BUF.GPS_X + "," + "Y=" + GLOBAL.GPS_BUF.GPS_Y + "," + "A=" + GLOBAL.GPS_BUF.GPS_A + "," + "S=" + GLOBAL.GPS_BUF.GPS_S + "\n"

        //ファイル内がカラの場合は新規追加
        if (last == -1) {
            MyApp().FileWriteAdd(filestr, "GPSLOG.txt")
        }
        //ファイル内に存在する場合は座標に変化があった場合のみ追加
        else if (GLOBAL.GPS_LOG[last].GPS_X != GLOBAL.GPS_BUF.GPS_X
            || GLOBAL.GPS_LOG[last].GPS_Y != GLOBAL.GPS_BUF.GPS_Y
        ) {
            MyApp().FileWriteAdd(filestr, "GPSLOG.txt")
        }
        MyApp().GPSFileRead("GPSLOG.txt")

    }

}
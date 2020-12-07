package com.monolith.compass

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.monolith.compass.com.monolith.compass.MyApp
import com.monolith.compass.ui.setting.SettingFragment


class MainActivity : AppCompatActivity(), LocationListener, SettingFragment.OnClickListener{

    val GLOBAL= MyApp.getInstance()

    private lateinit var locationManager: LocationManager

    var SharedValue  ="こんにちは"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GLOBAL.DIRECTORY="$filesDir"
        MyApp().FileRead("GPSLOG.txt")

        if (RequestGPSPermission()) {
            startLocationService()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //ナビゲーションバーのコントローラー設定
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

    }

    override fun onPause(){
        super.onPause()
        //バックグラウンド処理を完成させたらコメントアウトを外す
        //startBackgroundLocationService()
    }

    override fun onResume(){
        super.onResume()
        stopBackgroundLocationService()
        startLocationService()
    }

    //戻るボタン無効化、そのうちもどす
    override fun onBackPressed() {}

    //GPSパーミッションを取得、trueが返されれば実行OK
    private fun RequestGPSPermission(): Boolean {

        // Android 6, API 23以上でパーミッションの確認
        if (Build.VERSION.SDK_INT >= 23) {
            // 既に許可している
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return true
            }
            //許可していない場合
            else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            }
        } else {
            return true
        }
        return false
    }

    // 結果の受け取り
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>,
                                            grantResults: IntArray) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                MyApp().toastMake(this,"位置情報が取得できないため終了します")
                finish()
            }
        }
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)

            Log.d("debug", "checkSelfPermission false")
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            1f,
            this)
    }

    //SettingFragmentからのコール
    //GPS開始処理
    override fun onClick_start(){
        startBackgroundLocationService()
        MyApp().toastMake(this,"計測を開始します")
    }

    //SettingFragmentからのコール
    //GPS停止処理
    override fun onClick_stop(){
        stopBackgroundLocationService()
        MyApp().toastMake(this,"計測を終了します")
    }

    //位置情報取得を開始
    fun startBackgroundLocationService() {
        val intent = Intent(application, LocationService::class.java)
        val test: Application=application
        startForegroundService(intent)
    }

    //位置情報取得を終了
    fun stopBackgroundLocationService(){
        val intent = Intent(application, LocationService::class.java)
        stopService(intent)
    }

    override fun onLocationChanged(location: Location) {

        //GPS取得時にデータを一時保持
        GLOBAL.GPS_BUF.GPS_Y=(Math.floor(location.latitude*10000.0)/10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_X=(Math.floor(location.longitude*10000.0)/10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_A=(Math.floor(location.accuracy*10000.0)/10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_S=(Math.floor(location.speed*10000.0)/10000.0).toFloat()

        var filestr:String="X="+GLOBAL.GPS_BUF.GPS_X+","+"Y="+GLOBAL.GPS_BUF.GPS_Y+","+"A="+GLOBAL.GPS_BUF.GPS_A+","+"S="+GLOBAL.GPS_BUF.GPS_S+"\n"
        MyApp().FileWriteAdd(filestr,"GPSLOG.txt")

    }



}
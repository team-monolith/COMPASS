package com.monolith.compass

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.monolith.compass.com.monolith.compass.MyApp
import com.monolith.compass.ui.map.MapFragment
import com.monolith.compass.ui.map.NavChoiceFragment
import com.monolith.compass.ui.setting.SettingFragment


class MainActivity : AppCompatActivity(), SettingFragment.OnClickListener{

    val GLOBAL= MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (RequestGPSPermission()) {
            //startLocationService()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //ナビゲーションバーのコントローラー設定
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

    }

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
                startLocationService()
            } else {
                // それでも拒否された時の対応
                MyApp().toastMake(this,"位置情報が取得できないため終了します")
                finish()
            }
        }
    }

    //SettingFragmentからのコール
    //GPS開始処理
    override fun onClick_start(){
        startLocationService()
        MyApp().toastMake(this,"計測を開始します")
    }

    //SettingFragmentからのコール
    //GPS停止処理
    override fun onClick_stop(){
        stopLocationService()
        MyApp().toastMake(this,"計測を終了します")
    }

    //位置情報取得を開始
    fun startLocationService() {
        val intent = Intent(application, LocationService::class.java)
        val test: Application=application
        startForegroundService(intent)
    }

    //位置情報取得を終了
    fun stopLocationService(){
        val intent = Intent(application, LocationService::class.java)
        stopService(intent)
    }

}
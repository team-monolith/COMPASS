package com.monolith.compass

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       RequestGPSPermission()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //ナビゲーションバーのコントローラー設定
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    //GPSパーミッションを取得、trueが返されれば実行OK
    private fun RequestGPSPermission():Boolean{

        // Android 6, API 23以上でパーミッションの確認
        if(Build.VERSION.SDK_INT >= 23){
            // 既に許可している
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                return true
            }
            //許可していない場合
            else{
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1000)
            }
        }
        else{
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
                // それでも拒否された時の対応
                toastMake("位置情報が取得できないため終了します")
                finish()
            }
        }
    }

    fun toastMake(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }

}
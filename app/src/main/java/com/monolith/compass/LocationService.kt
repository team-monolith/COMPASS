package com.monolith.compass

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.*
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.monolith.compass.com.monolith.compass.MyApp
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.floor

class LocationService: Service(), LocationListener,SensorEventListener {

    private val GLOBAL = MyApp.getInstance()    //グローバル変数宣言用

    private var locationManager: LocationManager? = null
    private var context: Context? = null

    private val MinTime = 1000 //最低更新間隔（ミリ秒）
    private val MinDistance = 1f//最低更新距離（メートル）

    protected var mSensorManager: SensorManager? = null

    private var mPrevCount = 0f

    private var today= LocalDate.now()

    override fun onCreate() {
        super.onCreate()

        context=applicationContext

        // LocationManager インスタンス生成
        locationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager

        //歩数センサーインスタンス生成
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val sensor=mSensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        mSensorManager!!.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI)

        MyApp().ActivityFileRead("ACTIVITYLOG.txt")
    }

    //メイン処理
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val requestCode = 0
        val channelId = "default"
        val title = context!!.getString(R.string.app_name)
        val pendingIntent = PendingIntent.getActivity(
            context, requestCode,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        // ForegroundにするためNotificationが必要、Contextを設定
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification　Channel 設定
        val channel = NotificationChannel(
            channelId, title, NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Silent Notification"
        // 通知音を消さないと毎回通知音が出てしまう
        // この辺りの設定はcleanにしてから変更
        channel.setSound(null, null)
        // 通知ランプを消す
        channel.enableLights(false)
        channel.lightColor = Color.BLUE
        // 通知バイブレーション無し
        channel.enableVibration(false)
        notificationManager.createNotificationChannel(channel)
        val notification =
            Notification.Builder(context, channelId)
                .setContentTitle(title) // 本来なら衛星のアイコンですがandroid標準アイコンを設定
                .setSmallIcon(android.R.drawable.btn_star)
                //.setContentText("　"/*通知メモ入れる、GPS*/)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build()

        // startForeground
        startForeground(1, notification)
        startGPS()
        return START_NOT_STICKY
    }

    protected fun startGPS() {
        val strBuf = StringBuilder()
        strBuf.append("startGPS\n")
        val gpsEnabled =
            locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            // GPSを設定するように促す
            enableLocationSettings()
        }
        if (locationManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MinTime.toLong(), MinDistance, this
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            strBuf.append("locationManager=null\n")
        }
    }

    //GPS情報更新時処理
    @SuppressLint("SimpleDateFormat")
    override fun onLocationChanged(location: Location) {

        //日付取得
        val date_pattern = java.text.SimpleDateFormat("yyyyMMddHHmmss")
        val date = date_pattern.format(Date())

        //GPS取得時にデータを一時保持
        GLOBAL.GPS_BUF.GPS_Y = (floor(location.latitude * 10000.0) / 10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_X = (floor(location.longitude * 10000.0) / 10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_A = (floor(location.accuracy * 10000.0) / 10000.0).toFloat()
        GLOBAL.GPS_BUF.GPS_S = (floor(location.speed * 10000.0) / 10000.0).toFloat()

        //誤差が大きい場合はそもそも記録しない
        if (GLOBAL.GPS_BUF.GPS_A!! > 15f) return

        if(GLOBAL.GPS_BUF.GPS_S!! > 30f)return

        val last = GLOBAL.GPS_LOG.lastIndex

        val filestr: String =
             "D=" + date + "," + "X=" + GLOBAL.GPS_BUF.GPS_X + "," + "Y=" + GLOBAL.GPS_BUF.GPS_Y + "," + "A=" + GLOBAL.GPS_BUF.GPS_A + "," + "S=" + GLOBAL.GPS_BUF.GPS_S + "\n"

        //ファイル内がカラの場合は新規追加
        if (last == -1) {
            MyApp().FileWriteAdd(filestr, "GPSLOG.txt")
            MyApp().FileWriteAdd(filestr, "GPSBUF.txt")
        }
        //ファイル内に存在する場合は座標に変化があった場合のみ追加
        else if (GLOBAL.GPS_LOG[last].GPS_X != GLOBAL.GPS_BUF.GPS_X
            || GLOBAL.GPS_LOG[last].GPS_Y != GLOBAL.GPS_BUF.GPS_Y
        ) {
            MyApp().FileWriteAdd(filestr, "GPSLOG.txt")
            MyApp().FileWriteAdd(filestr, "GPSBUF.txt")
        }
        MyApp().GPSFileRead("GPSLOG.txt")

    }

    //GPSが利用不可、利用可能になった場合に呼ばれる
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Android 6, API 23以上でパーミッシンの確認
        if (Build.VERSION.SDK_INT <= 28) {
            val strBuf = StringBuilder()
            when (status) {
                LocationProvider.AVAILABLE -> {
                }
                LocationProvider.OUT_OF_SERVICE -> strBuf.append("LocationProvider.OUT_OF_SERVICE\n")
                LocationProvider.TEMPORARILY_UNAVAILABLE -> strBuf.append("LocationProvider.TEMPORARILY_UNAVAILABLE\n")
            }
        }
    }

    private fun enableLocationSettings() {
        val settingsIntent =
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

    private fun stopGPS() {
        if (locationManager != null) {
            // update を止める
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager!!.removeUpdates(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGPS()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    //歩行センサー検出時
    override fun onSensorChanged(event: SensorEvent?) {

        if (event != null) {

            if(mPrevCount == 0f)mPrevCount=event.values[0]

            if(event.sensor.type == Sensor.TYPE_STEP_COUNTER){
                GLOBAL.ACTIVITY_LOG[GLOBAL.ACTIVITY_LOG.lastIndex].STEP+=(event.values[0]-mPrevCount).toInt()
                MyApp().ActivityFileWrite("ACTIVITYLOG.txt")
                mPrevCount=event.values[0]
                if(today!=LocalDate.now()){
                    MyApp().ActivityFileRead("ACTIVITYLOG.txt")
                    today=LocalDate.now()
                }
            }
            else{
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //nothing to do
    }
}
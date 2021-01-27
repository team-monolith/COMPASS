package com.monolith.compass.com.monolith.compass

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Color.parseColor
import android.util.Base64
import android.widget.Toast
import androidx.core.graphics.toColorInt
import com.monolith.compass.R
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*


class MyApp : Application() {

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること

    val CENTRAL_LATITUDE: Int = 1304090//X座標側、下四桁が少数
    val CENTRAL_LONGITUDE: Int = 335840//Y座標側、下四桁が小数

    var DIRECTORY: String? = null

    data class LOCAL_DC(
        var HEIGHT: Float,
        var WEIGHT: Float,
        var TARGET: Int,
        var GPSFLG: Int,
        var HOME_X: Float,
        var HOME_Y: Float,
        var ACQUIED: Int,
        var MYCOLOR: String
    )

    data class GPSDATA(
        var GPS_D: Date?,
        var GPS_X: Float?,
        var GPS_Y: Float?,
        var GPS_A: Float?,
        var GPS_S: Float?
    )

    data class MAPDATA(
        var MAP: Array<Array<Int?>>,
        var MAP_X: Float?,
        var MAP_Y: Float?,
        var MAP_S: Int?,
        var BITMAP: Bitmap?
    )

    data class ACTIVITYDATA(
        var DATE: Date,
        var TARGET: Int,
        var STEP: Int,
        var DISTANCE: Int,
        var CAL: Int
    )

    data class CARDDATA(
        var ID: Int,
        var NAME: String,
        var ICON: Bitmap?,
        var LEVEL: Int,
        var DISTANCE: Int,
        var BADGE: Int,
        var BACKGROUND: Int,
        var FRAME: Int,
        var COMMENT: String,
        var STATE: Int
    )

    data class COORDINATE(var X: Float?, var Y: Float?)

    var GPS_LOG = mutableListOf<GPSDATA>()

    var ACTIVITY_LOG = mutableListOf<ACTIVITYDATA>()

    var GPS_BUF: GPSDATA = GPSDATA(null, null, null, null, null)

    var ImageBuffer: Bitmap? = null

    //日本は経度122-154,緯度20-46に存在する
    //y320000,x260000のデータで成り立つ
    //500x500でバッファリングする

    //1単位当たり10mで計算

    //開始時処理
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        private var instance: MyApp? = null
        fun getInstance(): MyApp {
            if (instance == null)
                instance = MyApp()
            return instance!!
        }
    }

    //トースト生成関数、第一引数：実行画面コンテキスト、第二引数：表示メッセージ
    fun toastMake(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun FileRead(child: String): String {
        var buf: String = ""
        val GLOBAL = getInstance()

        if (GLOBAL.DIRECTORY == null) return ""

        val file = File(GLOBAL.DIRECTORY + "/", child)

        if (!file.isFile) return ""

        val scan = Scanner(file)

        while (scan.hasNext()) {
            buf += scan.next()
            if (scan.hasNext()) buf += "\n"
        }
        return buf
    }

    fun FileWrite(str: String, child: String) {
        val GLOBAL = getInstance()

        if (GLOBAL.DIRECTORY == null) return

        try {
            val file = File(GLOBAL.DIRECTORY + "/", child)
            file.writeText(str)
        } catch (e: FileNotFoundException) {
            val file = File(GLOBAL.DIRECTORY + "/", child)
            file.writeText(str)
        }
    }

    fun FileWriteAdd(str: String, child: String) {
        var buf: String = ""
        val GLOBAL = getInstance()

        try {
            val file = File(GLOBAL.DIRECTORY + "/", child)
            val scan = Scanner(FileRead(child))
            while (scan.hasNextLine()) {
                buf += scan.nextLine() + "\n"
            }
            buf += str
            file.writeText(buf)
        } catch (e: FileNotFoundException) {
            val file = File(GLOBAL.DIRECTORY + "/", child)
            file.writeText(str)
        }
    }

    fun LocalSettingRead(child: String): LOCAL_DC{
        val GLOBAL = getInstance()
        try {
            val scan = Scanner(FileRead(child))
            scan.useDelimiter("[,\n]")
            while (scan.hasNextLine() && scan.hasNext()) {
                val HEIGHT: String = scan.next()
                val WEIGHT: String = scan.next()
                val TARGET: String = scan.next()
                val GPSFLG: String = scan.next()
                val HOME_X: String = scan.next()
                val HOME_Y: String = scan.next()
                val ACQUIED: String = scan.next()
                val MYCOLOR: String = scan.next()

                return LOCAL_DC(
                        HEIGHT.toFloat(),
                        WEIGHT.toFloat(),
                        TARGET.toInt(),
                        GPSFLG.toInt(),
                        HOME_X.toFloat(),
                        HOME_Y.toFloat(),
                        ACQUIED.toInt(),
                        MYCOLOR
                    )
            }
        } catch (e: FileNotFoundException) {
        }

        return LOCAL_DC(0f,0f,0,0,0f,0f,0,"#000000")
    }

    fun LocalSettingWrite(data: LOCAL_DC, child: String) {
        val GLOBAL = getInstance()
        var buf = ""
        try {
            val file = File(GLOBAL.DIRECTORY + "/", child)
                buf += data.HEIGHT.toString() + "," + data.WEIGHT + "," + data.TARGET + "," + data.GPSFLG + "," + data.HOME_X + "," + data.HOME_Y + "," + data.ACQUIED + "," + data.MYCOLOR
            file.writeText(buf)
        } catch (e: FileNotFoundException) {
            val file = File(GLOBAL.DIRECTORY + "/", child)
            file.writeText("")
        }
    }

    fun getID(): Int? {
        val GLOBAL = getInstance()

        if (GLOBAL.DIRECTORY == null) return null

        val file = File(GLOBAL.DIRECTORY + "/", "ID.txt")

        if (!file.exists()) return -1

        val scan = Scanner(file)

        return scan.nextInt()
    }

    @SuppressLint("SimpleDateFormat")
    fun GPSFileRead(child: String) {
        val GLOBAL = getInstance()

        GLOBAL.GPS_LOG.clear()

        try {
            val scan = Scanner(FileRead(child))
            scan.useDelimiter("[,\n]")
            val format = SimpleDateFormat("yyyyMMddHHmmss")

            while (scan.hasNextLine() && scan.hasNext()) {
                val FILE_D: String = scan.next().substring(2)
                val FILE_X: String = scan.next().substring(2)
                val FILE_Y: String = scan.next().substring(2)
                val FILE_A: String = scan.next().substring(2)
                val FILE_S: String = scan.next().substring(2)

                GLOBAL.GPS_LOG.add(
                    MyApp.GPSDATA(
                        format.parse(FILE_D),
                        FILE_X.toFloat(),
                        FILE_Y.toFloat(),
                        FILE_A.toFloat(),
                        FILE_S.toFloat()
                    )
                )
            }
        } catch (e: FileNotFoundException) {
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun ActivityFileRead(child: String) {
        val GLOBAL = getInstance()

        GLOBAL.ACTIVITY_LOG.clear()

        val pattern = SimpleDateFormat("yyyy/MM/dd")

        try {
            var scan = Scanner(FileRead(child))
            scan.useDelimiter("[,\n]")

            //ファイルが存在しない場合は新規で作る。目標値は仮置き
            if (!scan.hasNextLine()) {
                FileWrite(pattern.format(Date()).toString() + ",10000,0,0,0\n", "ACTIVITYLOG.txt")
                scan = Scanner(FileRead(child))
            }

            while (scan.hasNextLine() && scan.hasNext()) {
                val DATE: Date? = pattern.parse(scan.next())
                val TARGET: Int = scan.nextInt()
                val STEP: Int = scan.nextInt()
                val DISTANCE: Int = scan.nextInt()
                val CAL: Int = scan.nextInt()
                GLOBAL.ACTIVITY_LOG.add(MyApp.ACTIVITYDATA(DATE!!, TARGET, STEP, DISTANCE, CAL))
            }

            if (pattern.format(GLOBAL.ACTIVITY_LOG[GLOBAL.ACTIVITY_LOG.lastIndex].DATE) != pattern.format(
                    Date()
                )
            ) {
                FileWrite(pattern.format(Date()).toString() + ",10000,0,0,0\n", "ACTIVITYLOG.txt")
                val cl = Calendar.getInstance()
                cl.time = Date()

                //時刻データを破棄
                cl.clear(Calendar.MINUTE)
                cl.clear(Calendar.SECOND)
                cl.clear(Calendar.MILLISECOND)
                cl.set(Calendar.HOUR_OF_DAY, 0)

                //calendar型からdate型に変換
                val date = cl.time
                GLOBAL.ACTIVITY_LOG.add(MyApp.ACTIVITYDATA(date, 10000, 0, 0, 0))
            }

        } catch (e: FileNotFoundException) {
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun ActivityFileWrite(child: String) {
        val GLOBAL = getInstance()
        var buf = ""
        val pattern = SimpleDateFormat("yyyy/MM/dd")
        try {
            val file = File(GLOBAL.DIRECTORY + "/", child)
            for (i in GLOBAL.ACTIVITY_LOG.indices) {
                buf += pattern.format(GLOBAL.ACTIVITY_LOG[i].DATE) + "," + GLOBAL.ACTIVITY_LOG[i].TARGET + "," + GLOBAL.ACTIVITY_LOG[i].STEP + "," + GLOBAL.ACTIVITY_LOG[i].DISTANCE + "," + GLOBAL.ACTIVITY_LOG[i].CAL
                if (i != GLOBAL.ACTIVITY_LOG.lastIndex) buf += "\n"
            }
            file.writeText(buf)
        } catch (e: FileNotFoundException) {
            val file = File(GLOBAL.DIRECTORY + "/", child)
            file.writeText("")
        }
    }

    //名刺のBitmap画像を
    //CARDDATA型データを渡し、第二引数でresourcesを投げる
    fun CreateCardBitmap(DATA: CARDDATA, res: Resources): Bitmap {


        val img_card: Bitmap = BitmapFactory.decodeResource(res, R.drawable.card)
        val img_frame: Bitmap = FrameBitmapSearch(DATA.FRAME, res)
        val img_back: Bitmap = CardBackBitmapSearch(DATA.BACKGROUND, res)

        val img_icon: Bitmap?
        if (DATA.ICON != null) img_icon = Bitmap.createScaledBitmap(
            DATA.ICON!!,
            (img_frame.height / 13 * 5),
            (img_frame.height / 13 * 5),
            true
        )
        else img_icon = null

        val img_badge_back = Bitmap.createScaledBitmap(
            BadgeBackBitmapSearch(DATA.BACKGROUND, res),
            (img_frame.height / 5),
            (img_frame.height / 5),
            true
        )
        val img_badge_icon = Bitmap.createScaledBitmap(
            BadgeIconBitmapSearch(DATA.BADGE, res),
            (img_frame.height / 5),
            (img_frame.height / 5),
            true
        )
        val img_level: Bitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(res, R.drawable.levelframe),
            (img_frame.height / 10 * 3),
            (img_frame.height / 10 * 3),
            true
        )
        val str_id: String = DATA.ID.toString()
        val str_name: String = DATA.NAME
        val str_distance: String = DATA.DISTANCE.toString()
        val str_level: String = DATA.LEVEL.toString()
        val str_comment: String = DATA.COMMENT

        val width = img_card.width
        val height = img_card.height

        val frameWidth: Float = img_frame.width / 12.54f

        val paint = Paint()
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        paint.isAntiAlias = true

        canvas.drawBitmap(img_back, 0f, 0f, paint)

        canvas.drawBitmap(img_frame, 0f, 0f, paint)

        canvas.drawBitmap(img_card, 0f, 0f, paint)

        if (img_icon != null) canvas.drawBitmap(
            img_icon,
            ((width - frameWidth) / 8f) - (img_icon.width / 2f) + frameWidth / 2 + 60,
            ((height - frameWidth) / 4f) - (img_icon.height / 2f) + frameWidth / 2 + 20,
            paint
        )

        canvas.drawBitmap(img_level, 2510f, 135f, paint)

        canvas.drawBitmap(img_badge_back, 2610f, 575f, paint)

        canvas.drawBitmap(img_badge_icon, 2610f, 575f, paint)


        paint.textSize = 250f

        canvas.drawText(str_level, 2805f - paint.measureText(str_level) / 2, 410f, paint)

        //※ビューは一度作ったものをリサイズして利用するので、位置は無理やりハードコートしています
        paint.textSize = 150f

        val id: String = str_id.padStart(5, '0')
        val distance: String = String.format("%,d", str_distance.toInt())

        canvas.drawText(
            "ID：$id",
            (width - frameWidth) / 4 + frameWidth / 2 + 125,
            (height - frameWidth) / 4 + frameWidth - 250,
            paint
        )
        canvas.drawText(
            str_name,
            (width - frameWidth) / 4 + frameWidth / 2 + 125,
            ((height - frameWidth) / 4f) + (paint.fontMetrics.top / -2) + frameWidth / 2,
            paint
        )
        canvas.drawText(
            distance + "m",
            (width - frameWidth) / 4 * 3 + frameWidth / 2 - paint.measureText(distance + "m"),
            ((height - frameWidth) / 4f) + (paint.fontMetrics.top / -2) + frameWidth / 2 + 200,
            paint
        )

        if (str_comment.length <= 20) {
            canvas.drawText(
                str_comment,
                width / 2f - paint.measureText(str_comment) / 2f,
                1475f,
                paint
            )
        } else if (str_comment.length <= 40) {
            canvas.drawText(
                str_comment.substring(0..19),
                width / 2f - paint.measureText(str_comment.substring(0..19)) / 2f,
                1380f,
                paint
            )
            canvas.drawText(str_comment.substring(20), 215f, 1570f, paint)
        }



        paint.color = Color.parseColor("#808080")
        paint.strokeWidth = 5f
        //canvas.drawLine(1000f,280f,2400f,280f,paint)
        canvas.drawLine(1000f, 480f, 2450f, 480f, paint)
        canvas.drawLine(1000f, 680f, 2450f, 680f, paint)
        canvas.drawLine(1000f, 880f, 2450f, 880f, paint)

        return output
    }

    //名刺のBitmap画像を
    //CARDDATA型データを渡し、第二引数でresourcesを投げる
    //こっちは裏面だよ
    fun CreateBackBitmap(DATA: CARDDATA, res: Resources): Bitmap {

        val img_frame: Bitmap = FrameBitmapSearch(DATA.FRAME, res)
        val img_back: Bitmap = CardBackBitmapSearch(DATA.BACKGROUND, res)

        val id: String = DATA.STATE.toString().padStart(8, '0')

        val img_badge_back: Array<Bitmap> = arrayOf(
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[0].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[1].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[2].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[3].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[4].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[5].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[6].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeBackBitmapSearch(id[7].toString().toInt(), res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            )
        )


        val img_badge_icon: Array<Bitmap> = arrayOf(
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(0, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(1, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(2, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(3, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(4, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(5, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(6, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            ),
            Bitmap.createScaledBitmap(
                BadgeIconBitmapSearch(7, res),
                (img_frame.height / 7 * 2),
                (img_frame.height / 7 * 2),
                true
            )
        )


        val width = img_frame.width
        val height = img_frame.height

        val frameWidth: Float = img_frame.width / 12.54f

        val paint = Paint()
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        paint.isAntiAlias = true

        canvas.drawBitmap(img_back, 0f, 0f, paint)

        canvas.drawBitmap(img_frame, 0f, 0f, paint)

        for (x in 0..3) {
            for (y in 0..1) {
                canvas.drawBitmap(
                    img_badge_back[y * 4 + x],
                    (width) / 5f * (x + 1) - (img_badge_back[0].width / 2),
                    (height - frameWidth * 2) / 4f * (y * 2 + 1) - (img_badge_back[0].height / 2) + frameWidth,
                    paint
                )
                canvas.drawBitmap(
                    img_badge_icon[y * 4 + x],
                    (width) / 5f * (x + 1) - (img_badge_back[0].width / 2),
                    (height - frameWidth * 2) / 4f * (y * 2 + 1) - (img_badge_back[0].height / 2) + frameWidth,
                    paint
                )
            }
        }

        return output
    }

    fun IconBitmapCreate(data: String?): Bitmap? {
        if (data == null) return null
        val decodedByte: ByteArray = Base64.decode(data, 0)
        val buf = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        return buf
    }

    fun FrameBitmapSearch(ID: Int, res: Resources): Bitmap {
        var img = BitmapFactory.decodeResource(res, R.drawable.frame_0)
        when (ID) {
            0 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_0)
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_4)
            5 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_5)
            6 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_6)
            7 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_7)
            8 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_8)
            9 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_9)
            10 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_10)
            11 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_11)
            12 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_12)
            13 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_13)
            14 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_14)
            15 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_15)
            16 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_16)
        }
        return img
    }

    fun CardBackBitmapSearch(ID: Int, res: Resources): Bitmap {
        var img = BitmapFactory.decodeResource(res, R.drawable.card_background_0)
        when (ID) {
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_4)
            5 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_5)
            6 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_6)
            7 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_7)
            8 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_8)
            9 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_9)
        }
        return img
    }


    fun BadgeBackBitmapSearch(ID: Int, res: Resources): Bitmap {
        var img = BitmapFactory.decodeResource(res, R.drawable.badge_background_0)
        when (ID) {
            1 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_1)
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_4)
        }
        return img
    }

    fun BadgeIconBitmapSearch(ID: Int, res: Resources): Bitmap {
        var img: Bitmap = BitmapFactory.decodeResource(res, R.drawable.badge_icon_0)
        when (ID) {
            1 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_1)
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_4)
            5 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_5)
            6 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_6)
            7 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_7)
        }
        return img
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





STEPLOG------------------------------------------

->歩数データを保存。Fitnessはこのファイルを参照

日付(date)
目標歩数(int)
歩数(int)
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




アプリ終了時に処理
・地図更新情報
・すれちがい情報

変更時に処理
・ユーザ名刺データ

朝５時にバッチ処理
・地図更新情報
・ユーザ名刺データ
・すれちがい情報

起動時に処理
・


サーバで保持するべき情報ーーーーーーーーーーーーーー
・ID(8桁整数)
・名前(2バイト10文字）
・ユーザレベル(3桁整数）
・アイコン（300000文字)
・総距離(8桁整数）
・お気に入りバッジID(2桁整数）
・ひとこと（2バイト50文字)
・名刺背景色ID(2桁整数）
・名刺フレームID（2桁整数）
・バッジの状態（8桁の整数で管理）

・地図のマスタデータ200000x180000(csv)

各種ID----------------------------------------------

・バッジID

0ログイン日数
1レベル
2移動距離
3歩数
4開拓
5カロリー
6すれ違い
7イベント

・バッジ背景ID
0.Null
1.ブロンズ
2.シルバー
3.ゴールド
4.ダイヤ
------------------------------------------------------

  白　赤   青
|0123|456|789|


-------------------------------------------------------
 */
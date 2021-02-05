package com.monolith.compass.ui.fitness

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R
import com.monolith.compass.com.monolith.compass.MyApp
import java.text.SimpleDateFormat
import java.util.*


class FitnessFragment : Fragment() {

    private val GLOBAL = MyApp.getInstance()    //グローバル変数宣言用


    enum class Period {
        DAY, WEEK, MONTH
    }

    var GraphPeriod: Period = Period.DAY


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fitness, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GLOBAL.calc()
        // Fragmentを作成します
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.frame, DayFragment())
        transaction.commit()

        val btnPeriod = view.findViewById<Button>(R.id.btnPeriod)

        //表示期間変更ボタンリスナー
        btnPeriod.setOnClickListener {

            val transaction = childFragmentManager.beginTransaction()

            //現在のモードに合わせて画面を入れ変える
            when (GraphPeriod) {
                Period.DAY -> {
                    transaction.replace(R.id.frame, WeekFragment())
                    GraphPeriod = Period.WEEK
                    btnPeriod.text = "WEEK"
                }
                Period.WEEK -> {

                    transaction.replace(R.id.frame, MonthFragment())
                    GraphPeriod = Period.MONTH
                    btnPeriod.text = "MONTH"
                }
                Period.MONTH -> {
                    transaction.replace(R.id.frame, DayFragment())
                    GraphPeriod = Period.DAY
                    btnPeriod.text = "DAY"
                }
            }

            transaction.commit()
        }
        view.findViewById<Button>(R.id.btnToday).setOnClickListener {
            val transaction = childFragmentManager.beginTransaction()

            //現在のモードに合わせて画面を入れ変える
            when (GraphPeriod) {
                Period.DAY -> {
                    transaction.replace(R.id.frame, DayFragment())
                    GraphPeriod = Period.DAY
                    btnPeriod.text = "DAY"
                }
                Period.WEEK -> {
                    transaction.replace(R.id.frame, WeekFragment())
                    GraphPeriod = Period.WEEK
                    btnPeriod.text = "WEEK"
                }
                Period.MONTH -> {
                    transaction.replace(R.id.frame, MonthFragment())
                    GraphPeriod = Period.MONTH
                    btnPeriod.text = "MONTH"
                }
            }

            transaction.commit()
        }
    }

    //日の場合のセット
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    fun DataSet(startDay: Date) {
        val pattern = SimpleDateFormat("yyyy年MM月dd日")
        val dayNum:Int=SearchDayNumber(startDay)

        var STEPS=0
        var DISTANCE=0
        var CALORIES=0

        if(dayNum!=-1){
            STEPS=GLOBAL.ACTIVITY_LOG[dayNum].STEP
            DISTANCE=GLOBAL.ACTIVITY_LOG[dayNum].DISTANCE
            CALORIES=GLOBAL.ACTIVITY_LOG[dayNum].CAL
        }

        view?.findViewById<TextView>(R.id.txtDate)?.text = pattern.format(startDay)
        view?.findViewById<TextView>(R.id.txtSteps)?.text=STEPS.toString()+"歩"
        view?.findViewById<TextView>(R.id.txtDistance)?.text=DISTANCE.toString()+"km"
        view?.findViewById<TextView>(R.id.txtCalories)?.text=CALORIES.toString()+"kcal"
    }

    //週・月の場合のセット
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    fun DataSet(startDay: Date, endDay: Date,length:Int) {

        val pattern = SimpleDateFormat("yyyy年MM月dd日")

        var STEPS=0
        var DISTANCE=0
        var CALORIES=0

        var day=startDay

        val cl=Calendar.getInstance()
        cl.time=day

        for(i in 1..length){
            day=cl.time
            val n = SearchDayNumber(day)
            if(n!=-1){
                STEPS+=GLOBAL.ACTIVITY_LOG[n].STEP
                DISTANCE+=GLOBAL.ACTIVITY_LOG[n].DISTANCE
                CALORIES+=GLOBAL.ACTIVITY_LOG[n].CAL
            }
            cl.add(Calendar.DAY_OF_YEAR,1)
        }

        view?.findViewById<TextView>(R.id.txtDate)?.text = pattern.format(startDay) + "  ～  " + pattern.format(endDay)
        view?.findViewById<TextView>(R.id.txtSteps)?.text=STEPS.toString()+"歩"
        view?.findViewById<TextView>(R.id.txtDistance)?.text=DISTANCE.toString()+"km(仮)"
        view?.findViewById<TextView>(R.id.txtCalories)?.text=CALORIES.toString()+"kcal"
    }

    fun SearchDayNumber(day:Date):Int{
        for(i in GLOBAL.ACTIVITY_LOG.indices){
            if(day==GLOBAL.ACTIVITY_LOG[i].DATE){
                return i
            }
        }
        return -1
    }




}
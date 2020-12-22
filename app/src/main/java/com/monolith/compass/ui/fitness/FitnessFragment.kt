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

    private lateinit var fitnessViewModel: FitnessViewModel

    enum class Period {
        DAY, WEEK, MONTH
    }

    var GraphPeriod: Period = Period.DAY


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fitnessViewModel =
            ViewModelProvider(this).get(FitnessViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_fitness, container, false)
        fitnessViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            //_clickListener?.onClick_today()
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
            STEPS=GLOBAL.STEP_LOG[dayNum].STEP
            DISTANCE=GLOBAL.STEP_LOG[dayNum].STEP
            CALORIES=GLOBAL.STEP_LOG[dayNum].CAL
        }

        view?.findViewById<TextView>(R.id.txtDate)?.text = pattern.format(startDay)
        view?.findViewById<TextView>(R.id.txtSteps)?.text=STEPS.toString()+"歩"
        view?.findViewById<TextView>(R.id.txtDistance)?.text=DISTANCE.toString()+"km(仮)"
        view?.findViewById<TextView>(R.id.txtCalories)?.text=CALORIES.toString()+"kcal"
    }

    //週・月の場合のセット
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    fun DataSet(startDay: Date, endDay: Date) {
        val pattern = SimpleDateFormat("yyyy年MM月dd日")
        view?.findViewById<TextView>(R.id.txtDate)?.text =
            pattern.format(startDay) + "  ～  " + pattern.format(endDay)

    }

    fun SearchDayNumber(day:Date):Int{
        for(i in GLOBAL.STEP_LOG.indices){
            if(day==GLOBAL.STEP_LOG[i].DATE){
                return i
            }
        }
        return -1
    }


}
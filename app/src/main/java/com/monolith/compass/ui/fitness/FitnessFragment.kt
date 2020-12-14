package com.monolith.compass.ui.fitness

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R


class FitnessFragment : Fragment() {

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

        val btnPeriod=view.findViewById<Button>(R.id.btnPeriod)

        //表示期間変更ボタンリスナー
        btnPeriod.setOnClickListener {

            val transaction = childFragmentManager.beginTransaction()

            //現在のモードに合わせて画面を入れ変える
            when (GraphPeriod) {
                Period.DAY -> {
                    transaction.replace(R.id.frame, WeekFragment())
                    GraphPeriod=Period.WEEK
                    btnPeriod.text="WEEK"
                }
                Period.WEEK -> {
                    transaction.replace(R.id.frame, MonthFragment())
                    GraphPeriod=Period.MONTH
                    btnPeriod.text="MONTH"
                }
                Period.MONTH -> {
                    transaction.replace(R.id.frame, DayFragment())
                    GraphPeriod=Period.DAY
                    btnPeriod.text="DAY"
                }
            }

            transaction.commit()
        }
        view.findViewById<Button>(R.id.btnToday).setOnClickListener {
            //_clickListener?.onClick_today()
        }
    }

}
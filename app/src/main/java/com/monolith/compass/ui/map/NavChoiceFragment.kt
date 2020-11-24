package com.monolith.compass.ui.map

import android.content.Context
import com.monolith.compass.ui.map.MapViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.MainActivity
import com.monolith.compass.R

class NavChoiceFragment : Fragment() {

    private lateinit var navchoiceViewModel: NavChoiceViewModel

    var _clickListener: NavChoiceFragment.OnClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navchoiceViewModel =
            ViewModelProvider(this).get(NavChoiceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_nav_choice, container, false)
        val textView: TextView = root.findViewById(R.id.txt_navchoice)
        navchoiceViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnMap).setOnClickListener{
            _clickListener!!.onClick_map()
        }
        view.findViewById<Button>(R.id.btnEvent).setOnClickListener {
            //遷移処理
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            _clickListener = context as OnClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement OnArticleSelectedListener.")
        }
    }

    //Activityにイベントを通知
    interface OnClickListener {
        fun onClick_map()
    }
}
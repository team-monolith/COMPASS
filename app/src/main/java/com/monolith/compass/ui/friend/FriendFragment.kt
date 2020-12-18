package com.monolith.compass.ui.friend

import com.monolith.compass.ui.friend.FriendViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.monolith.compass.R

class FriendFragment : Fragment() {

    private lateinit var friendViewModel: FriendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        friendViewModel =
            ViewModelProvider(this).get(FriendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_friend, container, false)
        friendViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //コンボボックス生成処理
        val spinner=view.findViewById<Spinner>(R.id.spinnerSortFriend)
        val spinnerSort= arrayOf("新しい順","古い順","お気に入り順","名前順","ID順")
        val adapter=context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_dropdown_item,spinnerSort) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapter
    }
}
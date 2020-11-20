package com.monolith.compass.ui.friend

import com.monolith.compass.ui.friend.FriendViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val textView: TextView = root.findViewById(R.id.txt_friend)
        friendViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
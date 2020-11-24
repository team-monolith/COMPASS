package com.monolith.compass.ui.profile

import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.monolith.compass.R

class ProfileViewModel : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }

    private val _img = MutableLiveData<String>().apply{
        value ="@tools:sample/backgrounds/scenic"
    }

    val img:MutableLiveData<String> = _img
    val text:MutableLiveData<String> =_text
}



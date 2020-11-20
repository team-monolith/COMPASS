package com.monolith.compass.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavChoiceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is navchoice Fragment"
    }
    val text: LiveData<String> = _text
}
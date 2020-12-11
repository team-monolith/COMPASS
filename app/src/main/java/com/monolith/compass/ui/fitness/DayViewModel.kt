package com.monolith.compass.ui.fitness

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DayViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is day Fragment"
    }
    val text: LiveData<String> = _text
}
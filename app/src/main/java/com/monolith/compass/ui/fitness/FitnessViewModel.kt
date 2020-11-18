package com.monolith.compass.ui.fitness

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FitnessViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is fitness Fragment"
    }
    val text: LiveData<String> = _text
}
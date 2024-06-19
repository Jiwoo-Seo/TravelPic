package com.example.travelpic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class navViewmodel : ViewModel(){
    var albumcode = "greenjoa"
    var albumname = "1234"
    var hlname = "greenjoa"
    var maxlike = 0
    var maxcount = 0
    var selectedTags by mutableStateOf(setOf<String>())
    var newHighlight = false

    fun setAlbumInfo(code:String, name:String){
        albumcode = code
        albumname = name
    }
}
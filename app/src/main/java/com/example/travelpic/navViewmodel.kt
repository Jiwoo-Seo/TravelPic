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
//    var albumCode:String? = null
//    var albumName:String? = null

    fun checkInfo(code:String, name:String):Boolean{
        return albumcode == code && albumname == name
    }

    fun setAlbumInfo(code:String, name:String){
        albumcode = code
        albumname = name
    }
}
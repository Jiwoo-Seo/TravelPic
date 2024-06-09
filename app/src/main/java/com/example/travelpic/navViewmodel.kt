package com.example.travelpic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class navViewmodel : ViewModel(){
    var albumcode = "greenjoa"
    var albumname = "1234"

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
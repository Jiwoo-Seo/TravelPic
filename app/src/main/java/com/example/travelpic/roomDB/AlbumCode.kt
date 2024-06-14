package com.example.travelpic.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "album_codes")
data class AlbumCode(
    @PrimaryKey val code: String,
    val name: String
)


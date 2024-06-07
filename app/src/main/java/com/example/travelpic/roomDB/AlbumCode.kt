package com.example.travelpic.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_codes")
data class AlbumCode(
    @PrimaryKey val code: String,
    val name: String
)
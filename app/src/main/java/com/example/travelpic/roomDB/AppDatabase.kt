package com.example.travelpic.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [AlbumCode::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlbumCodeDatabase : RoomDatabase(){
    abstract fun getDao():AlbumCodeDao
    companion object{
        private var database:AlbumCodeDatabase?=null
        fun getItemDatabase(context: Context):AlbumCodeDatabase{
            return database ?:Room.databaseBuilder(
                context,
                AlbumCodeDatabase::class.java,
                "album_codes"
            ).build()
                .also{
                    database = it
                }
        }
    }
}
class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return Gson().toJson(list)
    }
}
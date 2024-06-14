package com.example.travelpic.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//
//@Database(entities = [AlbumCode::class], version = 1, exportSchema = false)
//abstract class UserAlbumDatabase : RoomDatabase() {
//    abstract fun userAlbumDao(): AlbumCodeDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: UserAlbumDatabase? = null
//
//        fun getDatabase(context: Context): UserAlbumDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    UserAlbumDatabase::class.java,
//                    "album_codes"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}
@Database(
    entities = [AlbumCode::class],
    version = 1,
    exportSchema = false)
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

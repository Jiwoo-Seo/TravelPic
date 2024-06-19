package com.example.travelpic.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumCode(albumCode: AlbumCode)

    @Delete
    suspend fun deleteAlbumCode(albumCode: AlbumCode)

    @Query("SELECT * FROM album_codes")
    fun getAllAlbumCodes(): Flow<List<AlbumCode>>

    @Query("SELECT * FROM album_codes WHERE code = :albumCode")
    suspend fun getAlbumCode(albumCode: String): AlbumCode?

    @Query("UPDATE album_codes SET likelist = :likelist WHERE code = :albumCode")
    suspend fun updateLikelist(albumCode: String, likelist: List<String>)
}

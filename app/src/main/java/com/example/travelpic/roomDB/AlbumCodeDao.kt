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

    @Query("UPDATE album_codes SET likelist = likelist || :imageName WHERE code = :albumCode")
    suspend fun addImageToLikelist(albumCode: String, imageName: String)

    @Query("UPDATE album_codes SET likelist = REPLACE(likelist, :imageName, '') WHERE code = :albumCode")
    suspend fun removeImageFromLikelist(albumCode: String, imageName: String)
}

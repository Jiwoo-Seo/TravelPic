package com.example.travelpic.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class FirebaseAlbumRepository(private val table: DatabaseReference) {
    fun fetchAlbums(): Flow<List<Picture>> = callbackFlow {
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {//변경될때마다 호출
                val pictureList = mutableListOf<Picture>()
                for (itemSnapshot in snapshot.children) {
                    val picture = itemSnapshot.getValue(Picture::class.java)
                    //item객체가 null이 아닐때만
                    picture?.let { pictureList.add(picture) }
                }
                trySend(pictureList)
            }

            override fun onCancelled(error: DatabaseError) {//실패하면 호출
                //TODO("Not yet implemented")
            }

        }
        table.addValueEventListener(listener)
        awaitClose{//flow가 중단됐을때 호출되는 콜백함수
            table.removeEventListener(listener)
        }
    }
    /*
    fun fetchAlbums(callback: (List<Album>) -> Unit) {
        table.child("albums").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val albumList = mutableListOf<Album>()
                for (albumSnapshot in snapshot.children) {
                    val album = albumSnapshot.getValue(Album::class.java)
                    if (album != null) {
                        albumList.add(album)
                    }
                }
                callback(albumList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching albums", error.toException())
            }
        })
    }*/

    fun addAlbum(album: Album) {Log.i("firebase","clickaddAlbum")
        table.child(album.code).setValue(album).addOnCompleteListener{
            Log.e("firebase","addAlbum")
        }.addOnFailureListener {
            Log.e("firebase","addAlbumFail")
        }
    }

    fun addPictureToAlbum(albumCode: String, picture: Picture) {
        val pictureRef = table.child(albumCode).child("pictures").push()
        pictureRef.setValue(picture)
            .addOnSuccessListener {
                Log.d("Firebase", "Picture saved successfully")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error saving picture", it)
            }
    }

    fun likePicture(albumCode: String, pictureId: String, newLikeCount: Int) {
        table.child(albumCode).child("pictures").child(pictureId).child("LikeCount")
            .setValue(newLikeCount)
            .addOnSuccessListener {
                Log.d("Firebase", "Like count updated successfully")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error updating like count", it)
            }
    }
}

package com.example.travelpic.data

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAlbumRepository(private val table: DatabaseReference) {

    fun fetchAlbums(): Flow<List<Picture>> = callbackFlow {
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pictureList = mutableListOf<Picture>()
                for (itemSnapshot in snapshot.children) {
                    val picture = itemSnapshot.getValue(Picture::class.java)
                    picture?.let { pictureList.add(picture) }
                }
                trySend(pictureList)
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO: Handle error
            }
        }
        table.addValueEventListener(listener)
        awaitClose {
            table.removeEventListener(listener)
        }
    }

    fun addAlbum(album: Album) {
        Log.i("firebase", "clickaddAlbum")
        table.child(album.code).setValue(album).addOnCompleteListener {
            Log.e("firebase", "addAlbum")
        }.addOnFailureListener {
            Log.e("firebase", "addAlbumFail")
        }
    }

    fun addPictureToAlbum(albumCode: String, picture: Picture) {
        val pictureRef = table.child(albumCode).child("pictures/${picture.key}")
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

    fun getPicturesForAlbum(albumCode: String, callback: (List<Picture>) -> Unit) {
        table.child(albumCode).child("pictures").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pictures = snapshot.children.mapNotNull { it.getValue(Picture::class.java) }
                callback(pictures)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    suspend fun addPictureToLocationTag(albumCode: String, locationTag: String, picture: Picture) {
        val pictureRef = table.child(albumCode).child("locationTags").child(locationTag).push()
        pictureRef.setValue(picture).await()
    }

    fun addLocationTagToAlbum(albumCode: String, locationTag: String, detailedAddress: String) {
        val locationTagRef = table.child(albumCode).child("locationTags").child(locationTag)
        locationTagRef.child("address").setValue(detailedAddress)
            .addOnSuccessListener {
                Log.d("Firebase", "Location tag added successfully")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error adding location tag", it)
            }
    }

    fun getLocationTags(albumCode: String, callback: (Map<String, String>) -> Unit) {
        table.child(albumCode).child("locationTags").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tags = snapshot.children.mapNotNull {
                    val tag = it.key
                    val address = it.child("address").value as? String
                    if (tag != null && address != null) {
                        tag to address
                    } else {
                        null
                    }
                }.toMap()
                callback(tags)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun parseLatLng(address: String): LatLng {
        val parts = address.split(",")
        return LatLng(parts[0].toDouble(), parts[1].toDouble())
    }

    fun getLocationTagsWithDetails(albumCode: String, callback: (Map<String, Triple<LatLng, String, String>>) -> Unit) {
        table.child(albumCode).child("locationTags").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tagsWithDetails = mutableMapOf<String, Triple<LatLng, String, String>>()
                for (locationTagSnapshot in snapshot.children) {
                    val address = locationTagSnapshot.child("address").value as? String
                    val firstPicture = locationTagSnapshot.children.firstOrNull { it.key != "address" }?.child("imageUrl")?.getValue(String::class.java)
                    val tagName = locationTagSnapshot.key ?: "Unknown"
                    if (address != null && firstPicture != null) {
                        val latLng = parseLatLng(address)
                        tagsWithDetails[tagName] = Triple(latLng, firstPicture, tagName)
                    }
                }
                callback(tagsWithDetails)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun getAllPicturesForLocationTag(albumCode: String, locationTag: String, callback: (List<String>) -> Unit) {
        table.child(albumCode).child("locationTags").child(locationTag)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val urls = mutableListOf<String>()
                    for (pictureSnapshot in snapshot.children) {
                        if (pictureSnapshot.key != "address") {
                            val url = pictureSnapshot.child("imageUrl").getValue(String::class.java)
                            if (url != null) {
                                urls.add(url)
                            }
                        }
                    }
                    callback(urls)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}

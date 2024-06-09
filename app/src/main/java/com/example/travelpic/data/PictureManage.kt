package com.example.travelpic.data

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Context

//파이어베이스에 앨범에 사진 저장
fun savePictureToFirebaseDatabase(@SuppressLint("RestrictedApi") context: Context, albumCode: String, picture: Picture) {
    val database = FirebaseDatabase.getInstance()
    val albumRef = database.getReference("albums").child(albumCode)
    val pictureName = picture.Date + picture.Model + picture.Latitude + picture.Longitude
    val newPictureRef = albumRef.child(pictureName).push()
    newPictureRef.setValue(picture)
        .addOnSuccessListener {
            //Toast.makeText(context, "Picture saved successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            //Toast.makeText(context, "Error saving picture", Toast.LENGTH_SHORT).show()
        }
}

//앨범리스트
@Composable
fun AlbumList(albums: List<Album>, albumViewModel: AlbumViewModel) {
    LazyColumn {
        items(albums) { album ->
            Text(album.name, modifier = Modifier.padding(8.dp))
            LazyRow {
                items(album.pictures) { picture ->
                    PictureCard(picture, album.code, albumViewModel)
                }
            }
        }
    }
}

@Composable
fun PictureCard(picture: Picture, albumCode: String, albumViewModel: AlbumViewModel) {
    val likeCount = remember { mutableStateOf(picture.LikeCount) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Date: ${picture.Date}")
            Text("Model: ${picture.Model}")
            Text("Latitude: ${picture.Latitude}")
            Text("Longitude: ${picture.Longitude}")
            Text("Location Tag: ${picture.LocationTag}")
            Text("Likes: ${likeCount.value}")
            Button(onClick = {
                likeCount.value++
                picture.LikeCount = likeCount.value
                val pictureId : String = picture.Date + picture.Model + picture.Latitude + picture.Longitude
                albumViewModel.likePicture(albumCode, pictureId, likeCount.value)  // Assuming `picture.date` as pictureId
            }) {
                Text("Like")
            }
        }
    }
}

package com.example.travelpic.data

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.travelpic.getExifInfo
import com.example.travelpic.parseExifInfo
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

//파이어베이스에 앨범에 사진 저장


//fun uploadImageToFirebase(uri: Uri, albumCode: String,context: android.content.Context) {
//    val storageReference = Firebase.storage.reference
//    val databaseReference: DatabaseReference = Firebase.database.getReference("AlbumList/${albumCode}/images")
////    val context = LocalContext.current
//
//    val key = databaseReference.push().key
//    Log.i("imagetag","${key}")
//    val imageReference = storageReference.child("images/${albumCode}/${key}")
//    try {
//        // Upload image to Firebase Storage
//        imageReference.putFile(uri)
//
//        // Get the download URL
//        val downloadUrl = imageReference.downloadUrl
//
//        // Save the download URL to Firebase Realtime Database
//        //val key = databaseReference.child("AlbumList/${albumCode}").push().key
//        key?.let {
//            var picture = parseExifInfo(getExifInfo(context, uri))
//            picture.imageUrl = key
//            databaseReference.child(it).setValue(picture)
//                .addOnSuccessListener {
//                    Log.i("imagetag","success") }
//                .addOnFailureListener{
//                    Log.i("imagetag","fail")
//                }
//        }
//
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//}

//suspend fun uploadImageToFirebase(uri: Uri, albumCode: String, context: android.content.Context) {
//    val storageReference = Firebase.storage.reference
//    val databaseReference: DatabaseReference = Firebase.database.getReference("AlbumList/$albumCode/images")
//    val key = databaseReference.push().key
//    Log.i("imagetag", "$key")
//    val imageReference = storageReference.child("images/$albumCode/$key")
//
//    try {
//        // Upload image to Firebase Storage
//        val uploadTask = imageReference.putFile(uri).await()
//
//        // Get the download URL after the upload is complete
//        val downloadUrl = imageReference.downloadUrl.await()
//
//        // Save the download URL to Firebase Realtime Database
//        key?.let {
//            val picture = parseExifInfo(getExifInfo(context, uri))
//            picture.imageUrl = downloadUrl.toString()  // Store the download URL
//            databaseReference.child(it).setValue(picture)
//                .addOnSuccessListener {
//                    Log.i("imagetag", "success")
//                }
//                .addOnFailureListener {
//                    Log.i("imagetag", "fail")
//                }
//        }
//    } catch (e: Exception) {
//        Log.e("Upload Error", "Failed to upload image", e)
//    }
//}
class AppLifecycleObserver(
    private val onForeground: () -> Unit
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        onForeground()
    }
}
suspend fun uploadImageToFirebase(
    uri: Uri,
    albumCode: String,
    context: android.content.Context,
    repository: FirebaseAlbumRepository
) {
    val storageReference = Firebase.storage.reference
    val key = Firebase.database.getReference("AlbumList/$albumCode/pictures").push().key
    Log.i("imagetag", "$key")
    val imageReference = storageReference.child("images/$albumCode/${key.toString()}")


    try {
        // Upload image to Firebase Storage
        imageReference.putFile(uri).await()

        // Get the download URL after the upload is complete
        val downloadUrl = imageReference.downloadUrl.await()

        // Create Picture object with Exif information and download URL
        val picture = parseExifInfo(getExifInfo(context, uri))
        picture.imageUrl = downloadUrl.toString()
        picture.key = key.toString()

        // Save the Picture object to Firebase Realtime Database
        key?.let {
            repository.addPictureToAlbum(albumCode, picture)
        }

        Log.i("imagetag", "Image uploaded and saved successfully")
        delay(1)
        Toast.makeText(context, "사진 업로드를 성공하였습니다", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        Log.e("Upload Error", "Failed to upload image", e)
        delay(1)
        Toast.makeText(context, "****사진 업로드를 실패하였습니다****", Toast.LENGTH_SHORT).show()
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

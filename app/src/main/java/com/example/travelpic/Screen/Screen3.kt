package com.example.travelpic.Screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.travelpic.LocalNavGraphViewModelStoreOwner
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.AlbumViewModelFactory
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.data.Picture
import com.example.travelpic.navViewmodel
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun Screen3(
    navController: NavController,
    userAlbumViewModel: UserAlbumViewModel
) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var showNoteDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    val storageRef = Firebase.storage.getReference("images/${navViewModel.albumcode}")
    // MutableState 리스트로 이미지 URL 저장
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var imageNames by remember { mutableStateOf(listOf<String>()) }
    // 이미지 리스트 가져오기
    LaunchedEffect(Unit) {
        storageRef.listAll().addOnSuccessListener { result ->
            result.items.forEach { item ->
                val fileName = item.name // 파일명 가져오기
                item.downloadUrl.addOnSuccessListener { uri ->
                    imageNames = imageNames + fileName
                    imageUrls = imageUrls + uri.toString()
                    // 여기서 fileName을 필요에 맞게 처리할 수 있습니다.
                }.addOnFailureListener { exception ->
                    // 다운로드 URL을 가져오는 도중 에러가 발생한 경우 처리
                }
            }
        }.addOnFailureListener { exception ->
            // 파일 목록을 가져오는 도중 에러가 발생한 경우 처리
        }
    }
//
//    LaunchedEffect(Unit) {
//        storageRef.listAll().addOnSuccessListener { result ->
//            val urls = result.items.map { it.downloadUrl }
//            urls.forEach { uriTask ->
//                uriTask.addOnSuccessListener { uri ->
//                    imageUrls = imageUrls + uri.toString()
//                }
//            }
//        }
//    }

    // 현재 이미지 인덱스를 저장할 상태
    var currentIndex by remember { mutableStateOf(0) }
    var isLiked by remember { mutableStateOf(false) }
    // 현재 이미지 URL
    val storageCurrentImageUrl = if (imageUrls.isNotEmpty()) imageUrls[currentIndex] else ""
    val CurrentImageName = if (imageUrls.isNotEmpty()) imageNames[currentIndex] else ""
    val ref = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/images/${CurrentImageName}")
    Log.i("CurrentImageName",CurrentImageName)
    //val image: Painter = painterResource(id = currentImageUrl) // 이미지 리소스
    var memo by remember { mutableStateOf("") }
    if (showNoteDialog) {
        ref.child("memo").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    memo = snapshot.getValue(String::class.java).toString()
                    Log.i("Currentmemo",memo)
                } else {
                    // 데이터가 없는 경우 처리
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우 처리
            }
        })
        NoteDialog(memo, onDismiss = { showNoteDialog = false }, onSave = { note ->
            ref.child("memo").setValue(note)
        })
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center

            ) {
                Image(painter = rememberAsyncImagePainter(model = storageCurrentImageUrl), contentDescription = null, contentScale = ContentScale.Fit)

            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Filled.Download, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = { showNoteDialog = true }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = {  }) {
                        Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = Color.White)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = {
                        if (imageUrls.isNotEmpty()) {
                            currentIndex = (currentIndex - 1 + imageUrls.size) % imageUrls.size
                    }}) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    IconButton(onClick = {
                        if (imageUrls.isNotEmpty()) {
                            currentIndex = (currentIndex + 1) % imageUrls.size
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

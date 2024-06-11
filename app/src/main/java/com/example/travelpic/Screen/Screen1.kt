package com.example.travelpic.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelpic.R
import com.example.travelpic.data.Album
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.roomDB.AlbumCode
import com.example.travelpic.userAlbumViewModel.MyAlbumList
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel

@Composable
fun Screen1(navController: NavController, albumViewModel: AlbumViewModel, userAlbumViewModel: UserAlbumViewModel) {
    val albums by userAlbumViewModel.userAlbumCodes.collectAsState(initial = emptyList())
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image) // 배경 이미지 리소스
    var showNewButtons by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                MyAlbumList(albums, userAlbumViewModel, navController)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                if (showNewButtons) {
                    Button(
                        onClick = {
                            showDialog = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    ) {
                        Text(text = "앨범 생성하기")
                    }

                    Button(
                        onClick = {
                            // 앨범 참여하기 로직 추가
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    ) {
                        Text(text = "앨범 참여하기")
                    }
                }

                Button(
                    onClick = {
                        showNewButtons = !showNewButtons
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "새로운 여정")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("앨범 생성하기") },
            text = {
                Column {
                    Text("앨범 이름을 입력하세요:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = albumName,
                        onValueChange = { albumName = it },
                        label = { Text("앨범 이름") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                    val newAlbum = Album(List(10) { charset.random() }.joinToString(""), albumName)
                    val newAlbumCode = AlbumCode(newAlbum.code, newAlbum.name)
                    albumViewModel.addAlbum(newAlbum)
                    userAlbumViewModel.addAlbumCode(newAlbumCode)
                    navController.navigate("screen2")
                    showDialog = false
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

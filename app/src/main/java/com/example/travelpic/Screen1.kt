package com.example.travelpic

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelpic.data.Album
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.roomDB.AlbumCode
import com.example.travelpic.userAlbumViewModel.MyAlbumList
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel

@Composable
fun Screen1(navController: NavController, albumViewModel: AlbumViewModel, userAlbumViewModel: UserAlbumViewModel) {
    val albums by userAlbumViewModel.userAlbumCodes.collectAsState(initial = emptyList())
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image) // 배경 이미지 리소스
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
                /*repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "앨범 이름")
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        }
                    }
                }*/
                MyAlbumList(albums,userAlbumViewModel,navController)


            }
            Button(
                onClick = {
                    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                    var newAlbum = Album(List(10) { charset.random() }.joinToString(""),"새 앨범456")
                    var newAlbumCode = AlbumCode(newAlbum.code, newAlbum.name)
                    albumViewModel.addAlbum(newAlbum)
                    userAlbumViewModel.addAlbumCode(newAlbumCode)
                    navController.navigate("screen2") },
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
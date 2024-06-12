package com.example.travelpic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelpic.data.AlbumViewModel

@Composable
fun highlightAlbumMenu(navController: NavController, albumViewModel: AlbumViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.PhotoAlbum,
            contentDescription = null,
            modifier = Modifier
                .clickable(onClick = { expanded = true })
                .size(48.dp)
        )
        Text(text = "하이라이트 앨범", fontSize = 12.sp)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem({
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    Text("하이라이트 앨범 만들기", modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "하이라이트 앨범 만들기") }
            }, onClick = {
                expanded = false
                navController.navigate("screen4")
            })
            Divider()
            DropdownMenuItem({
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    Text("하이라이트 앨범 보기", modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "하이라이트 앨범 만들기")}
            }, onClick = { expanded = false
                navController.navigate("screen5")})
        }
    }
}

@Composable
fun newAlbum(onClick1 :() -> Unit,onClick2 :() -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Button(onClick = { expanded = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "새로운 여정")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem({
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    Text("앨범 생성하기", modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "앨범 생성하기") }
            }, onClick = { expanded = false
                onClick1})
            Divider()
            DropdownMenuItem({
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    Text("앨범 참여하기", modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "앨범 참여하기")}
            }, onClick = { expanded = false
                onClick2})
        }
    }
}
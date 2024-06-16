package com.example.travelpic.Screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelpic.LocalNavGraphViewModelStoreOwner
import com.example.travelpic.R
import com.example.travelpic.data.Album
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.navViewmodel
import com.example.travelpic.roomDB.AlbumCode
import com.example.travelpic.userAlbumViewModel.MyAlbumList
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Random

@Composable
fun Screen1(navController: NavController, albumViewModel: AlbumViewModel, userAlbumViewModel: UserAlbumViewModel) {
    val albums by userAlbumViewModel.userAlbumCodes.collectAsState(initial = emptyList())
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image) // 배경 이미지 리소스
    var showNewButtons by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("") }
    var albumCode by remember { mutableStateOf("") }
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var expanded by remember { mutableStateOf(false) }
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

                Button(
                    onClick = {
                        //showNewButtons = !showNewButtons
                        expanded = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "새로운 여정")
                }

                DropdownMenu(
                    modifier = Modifier.width(180.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem({
                        Row(horizontalArrangement = Arrangement.SpaceBetween){
                            Text("앨범 생성하기", modifier = Modifier.weight(1f))
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "앨범 생성하기") }
                    }, onClick = {
                        expanded = false
                        showDialog = true
                        })
                    Divider(modifier = Modifier.padding(2.dp))
                    DropdownMenuItem({
                        Row(horizontalArrangement = Arrangement.SpaceBetween){
                            Text("앨범 참여하기", modifier = Modifier.weight(1f))
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "앨범 참여하기")}
                    }, onClick = {
                        expanded = false
                        showJoinDialog = true
                        })
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
//                    val newAlbum = Album(List(10) { charset.random() }.joinToString(""), albumName)
                    val newAlbum = Album(createInviteCode(), albumName)
                    val newAlbumCode = AlbumCode(newAlbum.code, newAlbum.name)
                    albumViewModel.addAlbum(newAlbum)
                    userAlbumViewModel.addAlbumCode(newAlbumCode)
                    navViewModel.albumname = albumName
                    navViewModel.albumcode = newAlbum.code
                    albumName = ""
                    navController.navigate("screen2")
                    showDialog = false
                    Log.i("albumcode",newAlbum.code)
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
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("앨범 참여하기") },
            text = {
                Column {
                    Text("앨범 코드을 입력하세요:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = albumCode,
                        onValueChange = { albumCode = it },
                        label = { Text("앨범 코드") }
                    )
                }
            },
            confirmButton = {
                var albumname by remember { mutableStateOf("") }
                Button(onClick = {
                    Log.i("albumcode",albumCode)
                    val ref = Firebase.database.getReference("AlbumList/${albumCode}/name")
                    ref.child("memo").get().addOnSuccessListener { dataSnapshot ->
                        if (dataSnapshot.exists()) {
                            albumname = dataSnapshot.getValue(String::class.java).toString()
                            // memoValue를 사용하여 원하는 작업을 수행할 수 있습니다.
                            // 예: MutableState로 저장하여 Compose UI에 반영하는 등의 작업
                        } else {
                            // 데이터가 없는 경우 처리
                        }
                    }.addOnFailureListener { exception ->
                        // 데이터를 가져오는 도중 에러가 발생한 경우 처리
                    }
                    val newAlbumCode = AlbumCode(albumCode, albumname)
                    userAlbumViewModel.addAlbumCode(newAlbumCode)
                    navViewModel.albumname = albumName
                    navViewModel.albumcode = albumCode
                    albumCode = ""
                    navController.navigate("screen2")
                    showJoinDialog = false
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { showJoinDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

// 초대 코드 생성
// 현재 abc1234567 형식
fun createInviteCode():String{
    val length=10
    val random= Random()
    // source
    val chars1="abcdefghijklmnopqrstuvwxyz"
    val chars2="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val nums="0123456789"
    // alphabet length
    val alph=3

    // code 생성
    val str=StringBuilder().apply{
        for(i in 0 until alph){
            val index1=random.nextInt(chars1.length)
            val index2=random.nextInt(chars2.length)
            append(if(random.nextBoolean()) chars1[index1] else chars2[index2])
        }
        for(i in alph until length){
            val index=random.nextInt(nums.length)
            append(nums[index])
        }
    }.toString()

    // 무결성 확인

    return str
}

// 초대 코드 매치 함수
// Firebase 연동 예정
fun matchInviteCode(code:String):Boolean{
    return true
}
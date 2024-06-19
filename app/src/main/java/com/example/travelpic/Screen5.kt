package com.example.travelpic

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.AlbumViewModelFactory
import com.example.travelpic.data.FirebaseAlbumRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun Screen5(navController: NavController, albumViewModel: AlbumViewModel) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val hlTable = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/highlight/${navViewModel.hlname}")

    val dbref = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/pictures")
    val locationref = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/locationTags")
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var imageNames by remember { mutableStateOf(listOf<String>()) }
    var locationTagsList by remember { mutableStateOf(listOf<String>()) }
    val currentDate = remember { LocalDate.now() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }
    val formattedDate = remember { currentDate.format(dateFormatter) }
    var isLoaded by remember { mutableStateOf(false) }

    if(navViewModel.newHighlight){
        hlTable.child("name").setValue(navViewModel.hlname)
        hlTable.child("date").setValue(formattedDate)
        LaunchedEffect(Unit) {
            locationref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        locationTagsList = emptyList()
                        for (snapshot in dataSnapshot.children) {
                            val locationTag = snapshot.key.toString()
                            if (locationTag != null && navViewModel.selectedTags.contains(locationTag)) {
                                for (snapshotshot in snapshot.children) {
                                    locationTagsList+=snapshotshot.child("key").getValue().toString()
                                    Log.i("locationddddTag", "추가완료: ${snapshotshot.child("key").getValue().toString()}")
                                }

                            }
                        }
                    } else {
                        println("No data available")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error: ${databaseError.message}")
                }
            })
        }

        LaunchedEffect(Unit) {
            dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        imageUrls = emptyList()
                        imageNames = emptyList()
                        for (snapshot in dataSnapshot.children) {
                            val imagekey = snapshot.child("key").getValue(String::class.java)
                            val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                            val imageLike = snapshot.child("likeCount").getValue(Int::class.java)?:0
                            if (imagekey != null && imageUrl != null && locationTagsList.contains(imagekey)&&imageNames.size<navViewModel.maxcount) {
                                hlTable.child(imagekey).setValue(imageUrl)
                                imageUrls += imageUrl
                                Log.i("highlight","key: "+imagekey+"url: "+imageUrl+" like: "+imageLike)
                            }
                        }
                        isLoaded = true

                    } else {
                        println("No data available")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error: ${databaseError.message}")
                }
            })
        }
    }else{

        LaunchedEffect(Unit) {
            hlTable.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        imageUrls = emptyList()
                        imageNames = emptyList()
                        val hlName = dataSnapshot.child("name").getValue(String::class.java)
                        val hlDate = dataSnapshot.child("date").getValue(String::class.java)
                        Log.i("hlhlanname",hlName.toString())
                        for (snapshot in dataSnapshot.children) {
                            val url = snapshot.getValue(String::class.java)
                            if(url!=hlName&&url!=hlDate){
                                imageUrls += url.toString()
                            }
                        }
                        isLoaded = true
                    } else {
                        println("No data available")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error: ${databaseError.message}")
                }
            })
        }
    }
    var currentIndex by remember { mutableStateOf(0) }
    LaunchedEffect(imageUrls) {
        if (imageUrls.isNotEmpty()) {
            launch {
                while (true) {
                    delay(2000) // 2초 대기
                    currentIndex = (currentIndex + 1) % imageUrls.size
                }
            }
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center

        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .padding(10.dp)
                .clickable {
                    navViewModel.hlname = ""
                    navViewModel.maxlike = 0
                    navViewModel.maxcount = 0
                    navViewModel.selectedTags = emptyList<String>().toSet()
                    navController.navigate("screen2") {
                        popUpTo("screen2") { inclusive = true }
                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Home, contentDescription = null, modifier = Modifier
                        .size(50.dp),tint = Color.White
                )
                //Text("앨범으로 돌아가기", fontSize = 20.sp)
            }

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = navViewModel.hlname, fontSize = 40.sp, color = Color.White)
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    if (isLoaded) {
                        if (imageUrls.isNotEmpty()) {
                            Crossfade(targetState = currentIndex) { index ->
                                Image(
                                    painter = // 코일 라이브러리에서 crossfade 효과 활성화
                                    rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = imageUrls[index]).apply(block = fun ImageRequest.Builder.() {
                                                crossfade(true) // 코일 라이브러리에서 crossfade 효과 활성화
                                            }).build()
                                    ),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
//                            Image(
//                                painter = rememberAsyncImagePainter(model = imageUrls[currentIndex]),
//                                contentDescription = null,
//                                contentScale = ContentScale.FillWidth
//                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            BasicText(text = "No images available")
                        }
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(60.dp))
                    }

                }
            }



}
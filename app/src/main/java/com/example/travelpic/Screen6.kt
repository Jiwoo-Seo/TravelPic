package com.example.travelpic

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.travelpic.data.AlbumViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun Screen6(navController: NavController, albumViewModel: AlbumViewModel) {
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image) // 배경 이미지 리소스
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val hlTable = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/highlight")
    var hlUrls by remember { mutableStateOf(listOf<String>()) }
    var hlNames by remember { mutableStateOf(listOf<String>()) }
    var hlDates by remember { mutableStateOf(listOf<String>()) }
    LaunchedEffect(Unit) {
        hlTable.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    hlUrls = emptyList()
                    hlNames = emptyList()
                    hlDates = emptyList()
                    for (snapshot in dataSnapshot.children) {
                        val hlName = snapshot.child("name").getValue(String::class.java)
                        val hlDate = snapshot.child("date").getValue(String::class.java)?:"Unknown"
                        for (snapshotshot in snapshot.children){
                            val url = snapshotshot.getValue(String::class.java)
                            if(hlName != null&& url!=hlName){
                                hlUrls += url.toString()
                                hlNames += hlName
                                hlDates += hlDate
                                break
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

    Box(modifier = Modifier
        .fillMaxSize()){
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
    AlbumList(hlNames, hlDates, hlUrls, navController)

}
@Composable
fun AlbumList(hlNames: List<String>, hlDates: List<String>, hlUrls: List<String>,navController:NavController) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(text = "하이라이트 앨범 목록", fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
        Spacer(modifier = Modifier.height(15.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(hlNames.indices.toList()) { index ->
                AlbumItem(name = hlNames[index], date = hlDates[index],imageUrl = hlUrls[index],navController)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun AlbumItem(name: String, date:String, imageUrl: String,navController:NavController) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .padding(10.dp)
            .clickable {
                navViewModel.hlname = name
                navViewModel.newHighlight = false
                navController.navigate("screen5")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = name,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "앨범 이름 : "+name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "생성 일자 : "+date,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }

        }
    }
}


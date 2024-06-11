package com.example.travelpic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelpic.data.Album
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.AlbumViewModelFactory
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.roomDB.AlbumCode
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun Screen4(navController: NavController, albumViewModel: AlbumViewModel) {
    val pictures = albumViewModel.pictures.collectAsState(initial = emptyList())
    val uniqueLocationTags = albumViewModel.uniqueLocationTags.collectAsState(emptyList())
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var newHighlightAlbumName by remember { mutableStateOf("")}
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    var maxLike by remember { mutableStateOf(0) }
    pictures.value.forEach { pic ->
       if (pic.LikeCount>maxLike){
           maxLike = pic.LikeCount
       }

    }
    var slider_Like by remember { mutableStateOf(0) }
    slider_Like = maxLike/2
    var slider_PictureCount by remember { mutableStateOf(0) }
    slider_PictureCount = pictures.value.size/2

    val scrollState = rememberScrollState()
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)){
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "하이라이트 앨범 설정", fontSize = 25.sp, fontWeight = Bold, modifier = Modifier.padding(0.dp,5.dp,0.dp,0.dp))
            Text(text = "최소 좋아요 개수 : ${slider_Like.toInt()}개", fontSize = 20.sp, modifier = Modifier.padding(0.dp,5.dp,0.dp,0.dp))
            Slider(
                value = slider_Like.toFloat(),
                onValueChange = { slider_Like = it.toInt() },
                valueRange = 0f..maxLike.toFloat(),
                steps = maxLike,
                modifier = Modifier.width(350.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(5.dp))
            Text(text = "최대 사진 개수 : ${slider_PictureCount}개", fontSize = 20.sp, modifier = Modifier.padding(0.dp,5.dp,0.dp,0.dp))
            Slider(
                value = slider_PictureCount.toFloat(),
                onValueChange = { slider_PictureCount = it.toInt() },
                valueRange = 0f..pictures.value.size.toFloat(),
                steps = pictures.value.size,
                modifier = Modifier.width(350.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(5.dp))
            Text(text = "위치 태그", fontSize = 20.sp, modifier = Modifier.padding(0.dp,5.dp,0.dp,0.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)
                    .height(500.dp)
            ) {
                uniqueLocationTags.value.forEach { tag ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedTags.contains(tag),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedTags = selectedTags + tag
                                } else {
                                    selectedTags = selectedTags - tag
                                }
                            }
                        )
                        Text(text = tag)
                    }
                }


            }
            Row (horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically){
                TextField(
                    value = newHighlightAlbumName,
                    onValueChange = { newHighlightAlbumName = it},
                    label = { Text("앨범이름")},
                    modifier = Modifier.width(300.dp)
                )
                Icon(imageVector = Icons.Default.ArrowCircleRight,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp,0.dp,0.dp,0.dp)
                        .width(50.dp)
                        .height(50.dp)
                        .clickable{
                            navViewModel.hlname = newHighlightAlbumName
                            navViewModel.maxlike = slider_Like.toInt()
                            navViewModel.maxcount = slider_PictureCount.toInt()
                            navViewModel.selectedTags = selectedTags
                            navController.navigate("screen5")
                        } )
            }

        }
    }
}
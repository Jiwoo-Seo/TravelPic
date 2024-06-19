package com.example.travelpic.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.travelpic.R
import com.example.travelpic.data.FirebaseAlbumRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationTagImagesScreen(navController: NavController, tagName: String, repository: FirebaseAlbumRepository, albumCode: String) {
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(tagName) {
        repository.getAllPicturesForLocationTag(albumCode, tagName) { urls ->
            imageUrls = urls
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("${tagName} 위치태그 앨범")},
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        LazyColumn {
            items(imageUrls.chunked(3)) { rowImages ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowImages.forEach { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (rowImages.size < 3) {
                        repeat(3 - rowImages.size) {
                            Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}

package com.example.travelpic.PictureClassification

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.travelpic.R
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.data.Picture
import kotlinx.coroutines.launch

@Composable
fun ClassifyPicturesScreen(
    albumCode: String,
    locationTag: String,
    navController: NavController,
    repository: FirebaseAlbumRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var pictures by remember { mutableStateOf<List<Picture>>(emptyList()) }
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image) // 배경 이미지 리소스

    LaunchedEffect(albumCode) {
        repository.getPicturesForAlbum(albumCode) { fetchedPictures ->
            pictures = fetchedPictures
        }
    }
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
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "사진 분류 - $locationTag", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(pictures.chunked(3)) { rowPictures ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowPictures.forEach { picture ->
                            PictureItem(
                                picture = picture,
                                onClick = {
                                    coroutineScope.launch {
                                        repository.addPictureToLocationTag(
                                            albumCode,
                                            locationTag,
                                            picture
                                        )
                                        navController.navigateUp()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )
                        }
                        if (rowPictures.size < 3) {
                            repeat(3 - rowPictures.size) {
                                Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PictureItem(picture: Picture, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(picture.imageUrl)
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

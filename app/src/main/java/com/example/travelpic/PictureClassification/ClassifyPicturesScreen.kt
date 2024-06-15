package com.example.travelpic.PictureClassification

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
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

    LaunchedEffect(albumCode) {
        repository.getPicturesForAlbum(albumCode) { fetchedPictures ->
            pictures = fetchedPictures
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "사진 분류 - $locationTag", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(pictures) { picture ->
                PictureItem(
                    picture = picture,
                    onClick = {
                        coroutineScope.launch {
                            repository.addPictureToLocationTag(albumCode, locationTag, picture)
                            navController.navigateUp()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PictureItem(picture: Picture, onClick: () -> Unit) {
    val painter = rememberAsyncImagePainter(picture.imageUrl)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "Date: ${picture.Date}")
            Text(text = "Model: ${picture.Model}")
        }
    }
}

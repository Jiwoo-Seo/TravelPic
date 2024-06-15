package com.example.travelpic.PictureClassification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelpic.data.FirebaseAlbumRepository
import kotlinx.coroutines.launch

@Composable
fun PictureClassificationDialog(
    albumCode: String,
    onDismiss: () -> Unit,
    repository: FirebaseAlbumRepository,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTag by remember { mutableStateOf("") }
    var locationTags by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(albumCode) {
        repository.getLocationTags(albumCode) { tags ->
            locationTags = tags
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("사진 분류") },
        text = {
            Column {
                Text("위치태그를 선택하세요:")
                locationTags.forEach { (tag, address) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTag = tag }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedTag == tag,
                            onClick = { selectedTag = tag }
                        )
                        Text(text = "$tag ($address)")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedTag.isNotEmpty()) {
                    coroutineScope.launch {
                        navController.navigate("ClassifyPicturesScreen/$albumCode/$selectedTag")
                        onDismiss()
                    }
                }
            }) {
                Text("확인")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("취소")
            }
        }
    )
}

package com.example.travelpic.Screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelpic.LocalNavGraphViewModelStoreOwner
import com.example.travelpic.PictureClassification.PictureClassificationDialog
import com.example.travelpic.R
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.data.uploadImageToFirebase
import com.example.travelpic.highlightAlbumMenu
import com.example.travelpic.navViewmodel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalNaverMapApi::class)
@Composable
fun Screen2(navController: NavController, albumViewModel: AlbumViewModel) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image)
    var showLocationDialog by remember { mutableStateOf(false) }
    var showPictureClassificationDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = FirebaseAlbumRepository(Firebase.database.getReference("AlbumList"))

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val selectImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
            }
        }
        if (selectedImageUri != null) {
            selectedImageUri?.let { uri ->
                scope.launch {
                    try {
                        uploadImageToFirebase(uri, navViewModel.albumcode, context, repository)
                        Log.i("DEBUG", "Image uploaded successfully: $uri")
                    } catch (e: Exception) {
                        Log.e("Upload Error", "Failed to upload image", e)
                    }
                }
            }
            Log.i("DEBUG", selectedImageUri.toString())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
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
            Text(
                text = navViewModel.albumname,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { navController.navigate("screen3") }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                val cameraPositionState = rememberCameraPositionState()
                NaverMap(
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(isZoomControlEnabled = false)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0x80FFFFFF), RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp)
            ) {
                ActionButton(icon = Icons.Default.UploadFile, text = "사진 업로드") {
                    permissions.launchMultiplePermissionRequest()
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }
                    if (permissions.allPermissionsGranted) {
                        selectImageLauncher.launch(intent)
                    }
                }

                ActionButton(icon = Icons.Default.Place, text = "위치태그") {
                    showLocationDialog = true
                }

                highlightAlbumMenu(navController, albumViewModel)
                ActionButton(icon = Icons.Default.PersonAddAlt, text = "친구 초대")
            }
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("위치태그 옵션") },
            text = {
                Column {
                    Button(
                        onClick = {
                            navController.navigate("AddLocationTag")
                            showLocationDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "위치태그 추가")
                    }
                    Button(
                        onClick = {
                            showPictureClassificationDialog = true
                            showLocationDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "사진 분류")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showLocationDialog = false }) {
                    Text("닫기")
                }
            }
        )
    }

    if (showPictureClassificationDialog) {
        PictureClassificationDialog(
            albumCode = navViewModel.albumcode,
            onDismiss = { showPictureClassificationDialog = false },
            repository = repository,
            navController = navController
        )
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .clickable(onClick = onClick)
                .size(48.dp)
        )
        Text(text = text, fontSize = 12.sp)
    }
}

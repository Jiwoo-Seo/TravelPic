@file:OptIn(ExperimentalNaverMapApi::class)

package com.example.travelpic.Screen

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelpic.LocalNavGraphViewModelStoreOwner
import com.example.travelpic.R
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.navViewmodel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.*
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun AddLocationTag(
    navController: NavController,
    albumViewModel: AlbumViewModel,
    userAlbumViewModel: UserAlbumViewModel
) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val albumCode = navViewModel.albumcode // 앨범 코드를 가져옴
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image) // 배경 이미지 리소스
    val context = LocalContext.current
    val repository = FirebaseAlbumRepository(Firebase.database.getReference("AlbumList"))
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(LatLng(37.5666102, 126.9783881)) }
    var showDialog by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }
    var locationTagName by remember { mutableStateOf("") }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(location, 16.0)
    }
    val markerState = rememberMarkerState(position = location)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(Color(0xE0FFFFFF), RoundedCornerShape(8.dp))
        ) {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("주소 입력") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        coroutineScope.launch {
                            val (newLocation, _) = searchAddress(
                                context,
                                address,
                                location
                            )
                            location = newLocation
                            cameraPositionState.move(CameraUpdate.scrollTo(newLocation))
                            markerState.position = newLocation
                        }
                    }) {
                        Text("검색")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        showDialog = true
                    }) {
                        Text("위치태그 추가")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "검색된 주소: ${location.latitude}, ${location.longitude}", modifier = Modifier.padding(8.dp))
                NaverMap(
                    modifier = Modifier.weight(1f),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = markerState,
                        captionText = "${location.latitude}, ${location.longitude}"
                    )
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("위치태그 추가") },
                    text = { Text("${location.latitude}, ${location.longitude} 를 위치태그에 추가하시겠습니까?") },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false
                            showInputDialog = true
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

            if (showInputDialog) {
                AlertDialog(
                    onDismissRequest = { showInputDialog = false },
                    title = { Text("위치태그 이름 입력") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = locationTagName,
                                onValueChange = { locationTagName = it },
                                label = { Text("위치태그 이름") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            coroutineScope.launch {
                                val albumCode = navViewModel.albumcode
                                if (locationTagName.isNotBlank() && albumCode != null) {
                                    val latLngString = "${location.latitude}, ${location.longitude}"
                                    repository.addLocationTagToAlbum(
                                        albumCode,
                                        locationTagName,
                                        latLngString
                                    )
                                    showInputDialog = false
                                    navController.navigateUp()
                                }
                            }
                        }) {
                            Text("확인")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showInputDialog = false }) {
                            Text("취소")
                        }
                    }
                )
            }
        }
    }
}

suspend fun searchAddress(context: android.content.Context, query: String, currentLocation: LatLng): Pair<LatLng, String> {
    val geocoder = Geocoder(context)
    return try {
        val results = geocoder.getFromLocationName(query, 5)
        if (!results.isNullOrEmpty()) {
            // Find the closest result to the current location
            val closestLocation = results.minByOrNull {
                val resultLocation = LatLng(it.latitude, it.longitude)
                distanceBetween(currentLocation, resultLocation)
            }
            if (closestLocation != null) {
                val detailedAddress = closestLocation.getAddressLine(0) ?: "주소를 찾을 수 없습니다"
                Pair(LatLng(closestLocation.latitude, closestLocation.longitude), detailedAddress)
            } else {
                Pair(currentLocation, "주소를 찾을 수 없습니다")
            }
        } else {
            Pair(currentLocation, "주소를 찾을 수 없습니다")
        }
    } catch (e: IOException) {
        Log.e("AddLocationTag", "Geocoding failed", e)
        Pair(currentLocation, "주소를 찾을 수 없습니다")
    }
}

fun distanceBetween(start: LatLng, end: LatLng): Double {
    val earthRadius = 6371e3 // meters
    val startLat = Math.toRadians(start.latitude)
    val startLng = Math.toRadians(start.longitude)
    val endLat = Math.toRadians(end.latitude)
    val endLng = Math.toRadians(end.longitude)

    val dLat = endLat - startLat
    val dLng = endLng

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(startLat) * Math.cos(endLat) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return earthRadius * c
}

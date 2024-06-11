@file:OptIn(ExperimentalNaverMapApi::class)

package com.example.travelpic.Screen

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel
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
    val context = LocalContext.current
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(LatLng(37.5666102, 126.9783881)) }
    var detailedAddress by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(location, 16.0)
    }
    val markerState = rememberMarkerState(position = location)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
                    val (newLocation, newAddress) = searchAddress(context, address, location)
                    location = newLocation
                    detailedAddress = newAddress
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
        Text(text = "검색된 주소: $detailedAddress", modifier = Modifier.padding(8.dp))
        NaverMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = markerState,
                captionText = detailedAddress
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("위치태그 추가") },
            text = { Text("${detailedAddress}를 위치태그에 추가하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    // 위치태그 추가 로직을 여기에 추가합니다.


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
    val dLng = endLng - startLng

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(startLat) * Math.cos(endLat) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return earthRadius * c
}

package com.example.travelpic.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelpic.R

@Composable
fun Screen3(navController: NavController) {
    val image: Painter = painterResource(id = R.drawable.image) // 이미지 리소스
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.DarkGray)
            ) {
                Image(painter = image, contentDescription = null, contentScale = ContentScale.Crop)
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Filled.Download, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Filled.Assignment, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = Color.White)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

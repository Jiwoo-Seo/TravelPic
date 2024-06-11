package com.example.travelpic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.AlbumViewModelFactory
import com.example.travelpic.data.FirebaseAlbumRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

@Composable
fun Screen5(navController: NavController, albumViewModel: AlbumViewModel) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val hlTable = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/${navViewModel.hlname}")
    val hlAlbumViewModel: AlbumViewModel = viewModel(factory = AlbumViewModelFactory(FirebaseAlbumRepository(hlTable)))
    val pictures = albumViewModel.pictures.collectAsState(initial = emptyList())
    pictures.value.forEach { pic ->

    }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()) {

    }
}
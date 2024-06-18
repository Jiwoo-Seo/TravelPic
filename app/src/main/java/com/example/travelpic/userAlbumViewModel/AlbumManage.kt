package com.example.travelpic.userAlbumViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelpic.LocalNavGraphViewModelStoreOwner
import com.example.travelpic.navViewmodel
import com.example.travelpic.roomDB.AlbumCode

@Composable
fun MyAlbumList(albums: List<AlbumCode>, userAlbumViewModel: UserAlbumViewModel, navigate: NavController) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    LazyColumn{
        items(albums) { album ->
            //Text(album.name, modifier = Modifier.padding(8.dp))
            AlbumCard(album,userAlbumViewModel,navigate,navViewModel)
        }
    }


}

@Composable
fun AlbumCard(
    albumCode: AlbumCode,
    userAlbumViewModel: UserAlbumViewModel,
    nav: NavController,
    navViewModel: navViewmodel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .clickable {

                    navViewModel.setAlbumInfo(albumCode.code, albumCode.name)
                    nav.navigate("screen2") }
        ) {
            Text(modifier = Modifier.padding(5.dp), text = albumCode.name)
            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.padding(5.dp).clickable {
                userAlbumViewModel.removeAlbumCode(albumCode)
            })

        }
    }

    Spacer(modifier = Modifier.height(16.dp))
//    Card(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//    ) {
//        Column(modifier = Modifier.padding(10.dp).fillMaxWidth().clickable { }){
//            Text(albumCode.code, fontSize = 15.sp)
//        }
//    }
}

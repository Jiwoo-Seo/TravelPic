package com.example.travelpic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.AlbumViewModelFactory
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.roomDB.AlbumCodeDatabase
import com.example.travelpic.ui.theme.TravelPicTheme
import com.example.travelpic.userAlbumViewModel.UserAlbumRepository
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.database.database

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelPicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TravelPicNavigator()
                }
            }
        }
    }
}
@Composable
fun rememberViewModelStoreOwner(): ViewModelStoreOwner {
    val context = LocalContext.current
    return remember(context) { context as ViewModelStoreOwner }
}

val LocalNavGraphViewModelStoreOwner =
    staticCompositionLocalOf<ViewModelStoreOwner> {
        error("Undefined")
    }
@Composable
fun TravelPicNavigator() {
    val context = LocalContext.current
    val table = Firebase.database.getReference("AlbumList")
    val albumViewModel: AlbumViewModel = viewModel(factory = AlbumViewModelFactory(FirebaseAlbumRepository(table))
    )
    //val userAlbumRepository = UserAlbumRepository(UserAlbumDatabase.getDatabase(context).userAlbumDao())
    //val userAlbumViewModel: UserAlbumViewModel = viewModel(factory = UserAlbumViewModelFactory(userAlbumRepository))
    val albumcodeDB = AlbumCodeDatabase.getItemDatabase(context)
    val userAlbumViewModel:UserAlbumViewModel =
        viewModel(factory = UserAlbumViewModelFactory(UserAlbumRepository(albumcodeDB.getDao())))

    val navController = rememberNavController()
    val navStoreOwner = rememberViewModelStoreOwner()
    CompositionLocalProvider(
        LocalNavGraphViewModelStoreOwner provides navStoreOwner
    ) {

        val navViewModel: navViewmodel =
            viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

        NavHost(navController = navController, startDestination = "screen1") {
            composable("screen1") { Screen1(navController, albumViewModel, userAlbumViewModel) }
            composable("screen2") { Screen2(navController, albumViewModel) }
            composable("screen3") { Screen3(navController) }
        }
    }

}


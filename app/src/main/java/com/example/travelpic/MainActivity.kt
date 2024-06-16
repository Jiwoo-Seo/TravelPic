package com.example.travelpic

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travelpic.PictureClassification.ClassifyPicturesScreen
import com.example.travelpic.Screen.AddLocationTag
import com.example.travelpic.Screen.Screen1
import com.example.travelpic.Screen.Screen3
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.AlbumViewModelFactory
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.roomDB.AlbumCodeDatabase
import com.example.travelpic.Screen.Screen2
import com.example.travelpic.navViewmodel
import com.example.travelpic.ui.theme.TravelPicTheme
import com.example.travelpic.userAlbumViewModel.UserAlbumRepository
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
//                    var checkDB = false
//                    val table = Firebase.database.getReference("AlbumList")
//                    table.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            checkDB = true
//                        }
//                        override fun onCancelled(databaseError: DatabaseError) {
//                            checkDB = true
//                        }
//                    })
//                    if(checkDB==false){
//
//                        AlertDialog.Builder(this) // 이 부분에서 YourActivity는 현재 활성화된 액티비티의 이름입니다.
//                            .setTitle("Error")
//                            .setMessage("파이어베이스에 연결되지 않습니다. 앱을 종료합니다.")
//                            .setPositiveButton("OK") { dialog, _ ->
//                                // OK 버튼 클릭 시 앱 종료
//                                finishAffinity() //액티비티 종료
//                            }
//                            .setCancelable(false) // 사용자가 다이얼로그를 취소할 수 없도록 설정
//                            .show()
//                    }else{
//                        TravelPicNavigator()
//                    }
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
    val albumViewModel: AlbumViewModel = viewModel(factory = AlbumViewModelFactory(FirebaseAlbumRepository(table)))
    val albumcodeDB = AlbumCodeDatabase.getItemDatabase(context)
    val userAlbumViewModel: UserAlbumViewModel =
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
            composable("screen3") { Screen3(navController, userAlbumViewModel) }
            composable("screen4") { Screen4(navController, albumViewModel) }
            composable("screen5") { Screen5(navController, albumViewModel) }
            composable("screen6") { Screen6(navController, albumViewModel) }
            composable("AddLocationTag") { AddLocationTag(navController, albumViewModel, userAlbumViewModel) }
            composable(
                route = "ClassifyPicturesScreen/{albumCode}/{locationTag}",
                arguments = listOf(
                    navArgument("albumCode") { type = NavType.StringType },
                    navArgument("locationTag") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val albumCode = backStackEntry.arguments?.getString("albumCode") ?: return@composable
                val locationTag = backStackEntry.arguments?.getString("locationTag") ?: return@composable
                ClassifyPicturesScreen(
                    albumCode = albumCode,
                    locationTag = locationTag,
                    navController = navController,
                    repository = FirebaseAlbumRepository(table)
                )
            }
        }
    }
}


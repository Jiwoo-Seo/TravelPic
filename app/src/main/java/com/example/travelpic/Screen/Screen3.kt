package com.example.travelpic.Screen

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.travelpic.LocalNavGraphViewModelStoreOwner
import com.example.travelpic.data.AlbumViewModel
import com.example.travelpic.data.AlbumViewModelFactory
import com.example.travelpic.data.FirebaseAlbumRepository
import com.example.travelpic.data.Picture
import com.example.travelpic.navViewmodel
import com.example.travelpic.userAlbumViewModel.UserAlbumViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Screen3(
    navController: NavController,
    userAlbumViewModel: UserAlbumViewModel
) {
    val navViewModel: navViewmodel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val coroutineScope = rememberCoroutineScope()
    var showNoteDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    val storageRef = Firebase.storage.getReference("images/${navViewModel.albumcode}")
    val dbref = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/pictures")
    // MutableState 리스트로 이미지 URL 저장
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var imageNames by remember { mutableStateOf(listOf<String>()) }

    val context= LocalContext.current


    // 이미지 리스트 가져오기
//    LaunchedEffect(Unit) {
//        storageRef.listAll().addOnSuccessListener { result ->
//            result.items.forEach { item ->
//                val fileName = item.name // 파일명 가져오기
//                item.downloadUrl.addOnSuccessListener { uri ->
//                    imageNames = imageNames + fileName
//                    imageUrls = imageUrls + uri.toString()
//                    // 여기서 fileName을 필요에 맞게 처리할 수 있습니다.
//                }.addOnFailureListener { exception ->
//                    // 다운로드 URL을 가져오는 도중 에러가 발생한 경우 처리
//                }
//                item.downloadUrl.addOnSuccessListener { uri ->
//                    imageNames = imageNames + fileName
//                    imageUrls = imageUrls + uri.toString()
//                    // 여기서 fileName을 필요에 맞게 처리할 수 있습니다.
//                }.addOnFailureListener { exception ->
//                    // 다운로드 URL을 가져오는 도중 에러가 발생한 경우 처리
//                }
//            }
//        }.addOnFailureListener { exception ->
//            // 파일 목록을 가져오는 도중 에러가 발생한 경우 처리
//        }
//    }
    LaunchedEffect(Unit) {
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (snapshot in dataSnapshot.children) {
                        val imagekey = snapshot.child("key").getValue(String::class.java)
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)

                        Log.i("imagenow",imageUrl.toString())
                        Log.i("imagenow",imagekey.toString())
                        if (imagekey != null && imageUrl != null) {
                            imageNames += imagekey
                            imageUrls += imageUrl
                            Log.i("imagenow", "추가완료${ imagekey.toString() }")
                        }
                    }

                } else {
                    println("No data available")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
    }

    // 현재 이미지 인덱스를 저장할 상태
    var currentIndex by remember { mutableStateOf(0) }
    var isLiked by remember { mutableStateOf(false) }
    // 현재 이미지 URL
    val storageCurrentImageUrl = if (imageUrls.isNotEmpty()) imageUrls[currentIndex] else ""
    Log.i("imagenowww",storageCurrentImageUrl)
    val CurrentImageName = if (imageNames.isNotEmpty()) imageNames[currentIndex] else ""
    Log.i("imagenowww",CurrentImageName)
    val ref = Firebase.database.getReference("AlbumList/${navViewModel.albumcode}/pictures/${CurrentImageName}")
    Log.i("CurrentImageName",CurrentImageName)
    //val image: Painter = painterResource(id = currentImageUrl) // 이미지 리소스
    var memo by remember { mutableStateOf("") }
    var likecount by remember { mutableStateOf(0) }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            coroutineScope.launch {
                downloadAndSaveImage(context, storageCurrentImageUrl)
            }
        }
    )

    if (showNoteDialog&&CurrentImageName!="") {
        ref.child("memo").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    memo = snapshot.getValue(String::class.java).toString()
                    Log.i("Currentmemo",memo)
                } else {
                    // 데이터가 없는 경우 처리
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우 처리
            }
        })
        NoteDialog(memo, onDismiss = { showNoteDialog = false }, onSave = { note ->
            ref.child("memo").setValue(note)
        })
    }
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
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center

            ) {
                if(storageCurrentImageUrl!="")
                    Image(painter = rememberAsyncImagePainter(model = storageCurrentImageUrl), contentDescription = null, contentScale = ContentScale.Fit)

            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    IconButton(onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            coroutineScope.launch {
                                downloadAndSaveImage(context, storageCurrentImageUrl)
                            }
                        } else {
                            storagePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Download, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = { if(CurrentImageName!="")showNoteDialog = true }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = Color.White)
                    }
                    coroutineScope.launch {
                        isLiked = userAlbumViewModel.isImageInLikelist(navViewModel.albumcode,CurrentImageName)
                    }
                    if(CurrentImageName!=""){
                        ref.child("likeCount").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    likecount = snapshot.getValue(Int::class.java)?:0
                                } else {
                                    // 데이터가 없는 경우 처리
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // 데이터를 가져오는 도중 에러가 발생한 경우 처리
                            }
                        })
                    }

                    IconButton(onClick = {
                        if(CurrentImageName!=""){
                            coroutineScope.launch {
                                userAlbumViewModel.toggleImageInLikelist(navViewModel.albumcode,CurrentImageName) { newState ->
                                    isLiked = newState
                                }
                            }
                            if(isLiked){
                                likecount-=1
                            }else{
                                likecount+=1
                            }
                            ref.child("likeCount").setValue(likecount)
                        }

                    }) {
                        Row {
                            if(CurrentImageName!=""&&isLiked){
                                Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = Color.Red)
                            }else{
                                Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = Color.White)
                            }
                            Text(text = likecount.toString(), color = Color.White)
                        }

                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = {
                        if (imageUrls.isNotEmpty()) {
                            currentIndex = (currentIndex - 1 + imageUrls.size) % imageUrls.size
                    }}) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    IconButton(onClick = {
                        if (imageUrls.isNotEmpty()) {
                            currentIndex = (currentIndex + 1) % imageUrls.size
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

suspend fun downloadAndSaveImage(context: Context, imageUrl: String) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.getReferenceFromUrl(imageUrl)

    try {
        val localFile = withContext(Dispatchers.IO) {
            val file = File.createTempFile("images", "jpg")
            storageRef.getFile(file).await()
            file
        }

        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        saveImageToGallery(context, bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "이미지를 다운로드하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
    }
}

fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "${System.currentTimeMillis()}.jpg"
    val fos: OutputStream?
    val resolver = context.contentResolver

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = resolver.openOutputStream(imageUri!!)
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }

    fos.use {
        if(it!=null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        Toast.makeText(context, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
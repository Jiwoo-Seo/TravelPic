package com.example.travelpic

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.travelpic.data.Picture
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MyApp() {
    val context = LocalContext.current
    var exifInfo by remember { mutableStateOf("No EXIF data") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            uri?.let {
                exifInfo = getExifInfo(context, it)
            }
        }
    )

//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("EXIF Info Viewer") })
//        },
//        content = {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
//            ) {
//                Button(onClick = {
//                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
//                        pickImageLauncher.launch("image/*")
//                    }
//                }) {
//                    Text("Pick Image")
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(exifInfo)
//            }
//        }
//    )
}

// exif 정보뽑는 함수
fun getExifInfo(context: Context, uri: Uri): String {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val exif = androidx.exifinterface.media.ExifInterface(inputStream!!)
        val dateTime = exif.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME)
        val dateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
//      val date = dateFormat.parse(dateTime ?: "") ?: Date()
        val date = try {
            dateFormat.parse(dateTime)
        } catch (e: Exception) {
            Date()  // 기본값으로 현재 날짜를 사용
        }
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
        val model = exif.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL) ?: "Unknown"
        var latitude = "Unknown"
        var longitude = "Unknown"

        val llexif = ExifInterface(context.contentResolver.openInputStream(uri)!!)
        val latLongArray = FloatArray(2)
        if (llexif.getLatLong(latLongArray)) {
            latitude = latLongArray[0].toString()
            longitude = latLongArray[1].toString()
        }

        "Date: $formattedDate\nModel: $model\nLatitude: $latitude\nLongitude: $longitude"
    } catch (e: Exception) {
        Log.e("getExifInfo", "Error reading Exif data", e)
        "Failed to load EXIF data"
    }
}
//정보뽑고 사진 데이터 만들기
fun parseExifInfo(exifInfo: String): Picture {
//    val lines = exifInfo.split("\n")
//    val date = lines[0].substringAfter("Date: ").trim()
//    val model = lines[1].substringAfter("Model: ").trim()
//    val latitude = lines[2].substringAfter("Latitude: ").trim()
//    val longitude = lines[3].substringAfter("Longitude: ").trim()
//    return Picture(date, model, latitude, longitude)
    val lines = exifInfo.split("\n")
    val date = lines.getOrNull(0)?.substringAfter("Date: ")?.trim() ?: "Unknown"
    val model = lines.getOrNull(1)?.substringAfter("Model: ")?.trim() ?: "Unknown"
    val latitude = lines.getOrNull(2)?.substringAfter("Latitude: ")?.trim() ?: "Unknown"
    val longitude = lines.getOrNull(3)?.substringAfter("Longitude: ")?.trim() ?: "Unknown"
    return Picture(date, model, latitude, longitude)
}
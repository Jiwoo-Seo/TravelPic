package com.example.travelpic.userAlbumViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelpic.data.Picture
import com.example.travelpic.roomDB.AlbumCode
import com.example.travelpic.roomDB.AlbumCodeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserAlbumRepository(private val albumCodeDao: AlbumCodeDao) {

    suspend fun insertAlbumCode(albumCode: AlbumCode) {
        albumCodeDao.insertAlbumCode(albumCode)
    }

    suspend fun deleteAlbumCode(albumCode: AlbumCode) {
        albumCodeDao.deleteAlbumCode(albumCode)
    }

    fun getAllAlbumCodes(): Flow<List<AlbumCode>> = albumCodeDao.getAllAlbumCodes()
}

class UserAlbumViewModelFactory(private val repository: UserAlbumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserAlbumViewModel::class.java)) {
            return UserAlbumViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}class UserAlbumViewModel(private val repository: UserAlbumRepository) : ViewModel() {
    //val userAlbumCodes: LiveData<List<String>> = repository.getAllAlbumCodes().asLiveData()
    private var _userAlbumCodes = MutableStateFlow<List<AlbumCode>>(emptyList())
    val userAlbumCodes = _userAlbumCodes.asStateFlow()//StateFlow는 UI를 갱신할때 사용

    init {
        getAllAlbumCodes()
    }
    fun getAllAlbumCodes(){
        viewModelScope.launch {
            repository.getAllAlbumCodes().collect{
                _userAlbumCodes.value = it
            }
        }
    }

    fun addAlbumCode(albumCode: AlbumCode) {
        viewModelScope.launch {
            repository.insertAlbumCode(albumCode)
            getAllAlbumCodes()
        }
    }

    fun removeAlbumCode(albumCode: AlbumCode) {
        viewModelScope.launch {
            repository.deleteAlbumCode(albumCode)
            getAllAlbumCodes()
        }
    }
}

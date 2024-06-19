package com.example.travelpic.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AlbumViewModelFactory(private val repository: FirebaseAlbumRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            return AlbumViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class AlbumViewModel(private val repository: FirebaseAlbumRepository) : ViewModel() {
    //private val _albums = MutableLiveData<List<Album>>()
    private var _pictures = MutableStateFlow<List<Picture>>(emptyList())
    val pictures = _pictures.asStateFlow()//StateFlow는 UI를 갱신할때 사용
    //val albums: LiveData<List<Album>> get() = _albums
    val uniqueLocationTags: Flow<List<String>> = _pictures
        .map { pictures -> pictures.map { it.LocationTag }.toSet().toList() }
    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
//        repository.fetchAlbums { albumList ->
//            _albums.postValue(albumList)
//        }
        viewModelScope.launch {
            repository.fetchAlbums().collect{
                _pictures.value = it
            }
        }
    }

    fun addAlbum(album: Album) {
        viewModelScope.launch {
            repository.addAlbum(album)
            fetchAlbums()
        }
    }

    fun likePicture(albumCode: String, pictureId: String, newLikeCount: Int) {
        viewModelScope.launch {
            repository.likePicture(albumCode, pictureId, newLikeCount)
            fetchAlbums()
        }
    }
}


package com.example.travelpic.data

data class Album(
    val code: String,
    var name: String,
    var pictures: MutableList<Picture> = mutableListOf(),
    var locationTag: MutableList<String> = mutableListOf()
) {
    constructor() : this("noinfo", "noinfo",)
}

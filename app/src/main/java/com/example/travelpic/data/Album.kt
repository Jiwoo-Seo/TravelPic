package com.example.travelpic.data

data class Album(
    val code: String,
    val name: String,
    val pictures: MutableList<Picture> = mutableListOf()
) {
    constructor() : this("noinfo", "noinfo",)
}

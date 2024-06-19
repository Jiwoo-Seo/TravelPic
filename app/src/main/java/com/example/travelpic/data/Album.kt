package com.example.travelpic.data

data class Album(
    val code: String,
    var name: String,
    var pictures: MutableList<Picture> = mutableListOf(),
    var locationTags: MutableMap<String, Pair<String, MutableList<Picture>>> = mutableMapOf() // 태그별로 사진을 모아놓아야해서 변경했습니다.(key=위치태그, value=사진들 리스트)
) {
    constructor() : this("noinfo", "noinfo",)
}

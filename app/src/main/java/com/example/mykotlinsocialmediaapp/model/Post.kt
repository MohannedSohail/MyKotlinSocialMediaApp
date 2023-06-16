package com.example.mykotlinsocialmediaapp.model

data class Post(
    val name: String?="",
    var postId: String?="",
    var postImg: String?="",
    val postFile: String?="",
    val stdId: String?="",
    val createdDate: String?="",
    val userImg: String?="",
    var postBody: String?="",
    val isLiked: Boolean?=false,
    val postComments: ArrayList<Comment>?=ArrayList(),

    )

package com.example.mykotlinsocialmediaapp.model

data class Student(
    var studentName: String?=null,
    var studentEmail: String?=null,
    var studentPhone: String?=null,
    var studentId: String?=null,
    var studentImg: String?=null,
    var studentBio: String?=null,
    var likedPostIds: List<String>?= null,
)

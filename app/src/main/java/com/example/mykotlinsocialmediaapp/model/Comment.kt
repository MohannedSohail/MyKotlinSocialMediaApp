package com.example.mykotlinsocialmediaapp.model

import java.sql.Timestamp

data class Comment(
    val body: String,
    val commentId: String,
    val stdId: String,
    val date: Timestamp,

    )

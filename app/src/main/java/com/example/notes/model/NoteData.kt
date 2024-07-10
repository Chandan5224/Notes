package com.example.notes.model

import java.io.Serializable


data class NoteData(
    val id: Int,
    val userUid: String,
    var title: String,
    var body: String,
    var timestamp: String
) : Serializable

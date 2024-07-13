package com.example.notes.model

import java.io.Serializable


data class NoteData(
    val id: Int,
    val userUid: String,
    var title: String,
    var body: String,
    var timestamp: String,
    var imagePaths: List<String> = listOf(),
    var isSelected: Boolean = false
) : Serializable

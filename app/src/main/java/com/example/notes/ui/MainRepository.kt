package com.example.notes.ui

import android.content.Context
import com.example.notes.db.NotesDao
import com.example.notes.model.NoteData

class MainRepository(context: Context) {
    private val noteDao: NotesDao = NotesDao(context)

    fun insertNote(note: NoteData) = noteDao.insertNote(note)

    fun getNotesByUserUid(userUid: String): List<NoteData> = noteDao.getNotesByUserUid(userUid)

    fun updateNote(note: NoteData) = noteDao.updateNote(note)

    fun deleteNoteById(id: Int) = noteDao.deleteNoteById(id)
}
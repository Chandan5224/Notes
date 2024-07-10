package com.example.notes.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract.CommonDataKinds.Note
import com.example.notes.model.NoteData
import com.example.notes.utils.Constants.TABLE_NAME

class NotesDao( context: Context) {

    private val dbHelper: MyDBHelper = MyDBHelper(context)

    fun insertNote(note: NoteData): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userUid", note.userUid)
            put("title", note.title)
            put("body", note.body)
            put("timestamp", note.timestamp)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getNotesByUserUid(userUid: String): List<NoteData> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf("id", "userUid", "title", "body","timestamp"),
            "userUid=?",
            arrayOf(userUid),
            null,
            null,
            "timestamp DESC"
        )

        val notes = mutableListOf<NoteData>()
        while (cursor.moveToNext()) {
            val note = NoteData(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("userUid")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("body")),
                cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
            )
            notes.add(note)
        }
        cursor.close()
        return notes
    }

    fun updateNote(note: NoteData): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("body", note.body)
            put("timestamp", note.timestamp)
        }
        return db.update(TABLE_NAME, values, "id=?", arrayOf(note.id.toString()))
    }

    fun deleteNoteById(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_NAME, "id=?", arrayOf(id.toString()))
    }
}
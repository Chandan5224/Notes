package com.example.notes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.model.NoteData
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    private val _notes = MutableLiveData<List<NoteData>>()
    val notes: LiveData<List<NoteData>> = _notes

    init {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            getNotesByUserUid(it.uid)
        }
    }

    fun insertNote(note: NoteData) {
        repository.insertNote(note)
        _notes.value = repository.getNotesByUserUid(note.userUid)
    }

    fun getNotesByUserUid(userUid: String) {
        _notes.value = emptyList()
        _notes.value = repository.getNotesByUserUid(userUid)
    }

    fun updateNote(note: NoteData) {
        repository.updateNote(note)
        _notes.value = repository.getNotesByUserUid(note.userUid)
    }

    fun deleteNoteById(id: Int, userUid: String) {
        repository.deleteNoteById(id)
        _notes.value = repository.getNotesByUserUid(userUid)
    }

    fun clearAllData() {
        AppPreferences.removeDataSharePreference(Constants.LOGIN)
        AppPreferences.removeDataSharePreference(Constants.USER_UID)
        AppPreferences.removeDataSharePreference(Constants.USER_NAME)
        AppPreferences.removeDataSharePreference(Constants.USER_IMAGE_URL)
    }
}
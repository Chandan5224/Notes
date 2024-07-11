package com.example.notes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.FragmentNoteBinding
import com.example.notes.model.NoteData
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.example.notes.utils.Utils

class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private var noteData: NoteData? = null
    private lateinit var userUid: String
    private var deleteNote: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(layoutInflater, container, false)
        viewModel = (requireActivity() as MainActivity).viewModel
        userUid = AppPreferences.getDataFromSharePreference(Constants.USER_UID) ?: ""
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNoteData()
        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnShare.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val body = binding.etBody.text.toString().trim()
            noteData?.apply {
                this.title = title
                this.body = body
            }
            val shareNote = "*$title*\n$body"
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareNote)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Share To:"))

        }

        binding.btnDelete.setOnClickListener {
            noteData?.let { data ->
                if (data.id == 0) {
                    Toast.makeText(
                        requireContext(),
                        "Empty note can't deleted !",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    deleteNote = true
                    findNavController().navigateUp()

                }
            }
        }
    }


    private fun setupNoteData() {
        arguments?.getSerializable(Constants.FROM_HOME_TO_NOTE)?.let { data ->
            noteData = data as NoteData
            binding.etBody.setText(noteData?.body ?: "")
            binding.etTitle.setText(noteData?.title ?: "")
            focusAndShowKeyboard(binding.etBody)
        } ?: run {
            noteData = NoteData(
                0,
                userUid,
                "",
                "",
                Utils.getTimeStamp()
            )
            focusAndShowKeyboard(binding.etTitle)
        }
    }

    private fun focusAndShowKeyboard(editText: EditText) {
        editText.requestFocus()
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroy() {
        super.onDestroy()
        noteData?.let { data ->
            if (deleteNote) {
                viewModel.deleteNoteById(data.id, data.userUid)
                _binding = null
                return
            }
            if (binding.etTitle.text.toString() != data.title || binding.etBody.text.toString() != data.body) {
                data.apply {
                    title = binding.etTitle.text.toString().trim()
                    body = binding.etBody.text.toString().trim()
                    timestamp = Utils.getTimeStamp()
                }
                if (data.id == 0)
                    viewModel.insertNote(data)
                else
                    viewModel.updateNote(data)
            }
        }
        _binding = null
    }
}
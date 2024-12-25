package com.example.notes.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.adapter.ImageAdapter
import com.example.notes.adapter.OnImageClick
import com.example.notes.databinding.FragmentNoteBinding
import com.example.notes.model.NoteData
import com.example.notes.ui.MainActivity
import com.example.notes.ui.MainViewModel
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.example.notes.utils.Utils

class NoteFragment : Fragment(), OnImageClick {

    private lateinit var mAdapter: ImageAdapter
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private var noteData: NoteData? = null
    private lateinit var userUid: String
    private var deleteNote: Boolean = false
    private var imageList: ArrayList<String> = arrayListOf()

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            // Handle the selected images (URIs)
            if (uris.isNotEmpty()) {
                binding.rvImages.visibility = View.VISIBLE
                val paths = uris.mapNotNull { uri ->
                    getPathFromUri(uri)
                }
                imageList.addAll(paths)
                Log.d("troubleshoot", imageList.toString())
                mAdapter.updateImageData(imageList)
            }

        }

    private fun getPathFromUri(uri: Uri): String? {
        var filePath: String? = null
        val cursor = requireActivity().contentResolver.query(
            uri,
            arrayOf(MediaStore.Images.Media.DATA),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    }


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
        setupRecyclerView()
        setupNoteData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        // Initialize RecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.layoutManager = layoutManager
        mAdapter = ImageAdapter(this)
        binding.rvImages.adapter = mAdapter
    }

    private fun setupClickListeners() {

        binding.btnAttach.setOnClickListener {
            imagePicker.launch("image/*")
        }
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
            imageList.addAll(noteData?.imagePaths ?: listOf())
            mAdapter.updateImageData(imageList)
            if (imageList.isNotEmpty()) {
                binding.rvImages.visibility = View.VISIBLE
            }
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
        val etTitle = binding.etTitle.text.toString().trim()
        val etBody = binding.etBody.text.toString().trim()
        noteData?.let { data ->
            if (deleteNote) {
                viewModel.deleteNoteById(data.id, data.userUid)
                _binding = null
                return
            }
            if (etTitle != data.title || etBody != data.body || imageList != data.imagePaths) {
                data.apply {
                    title = etTitle
                    body = etBody
                    imagePaths = imageList
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

    override fun onClick(position: Int) {
        imageList.removeAt(position)
        mAdapter.removeImageData(position)
        if (imageList.isEmpty()) {
            binding.rvImages.visibility = View.GONE
        }
    }
}
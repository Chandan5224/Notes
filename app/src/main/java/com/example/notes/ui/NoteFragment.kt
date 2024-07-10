package com.example.notes.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.FragmentNoteBinding
import com.example.notes.model.NoteData
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.example.notes.utils.Constants.USER_UID
import com.example.notes.utils.Utils
import com.google.android.material.internal.ViewUtils.showKeyboard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarEditTitle: EditText
    private lateinit var viewModel: MainViewModel
    private var noteData: NoteData? = null
    private lateinit var userUid: String

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
        setupToolbar()
        setupNoteData()
        setupEditText()
        setupMenu()
        focusAndShowKeyboard()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.arrow_left)
                setHomeButtonEnabled(true)
                title = getString(R.string.default_title)
            }
        }
    }

    private fun setupNoteData() {
        arguments?.getSerializable(Constants.FROM_HOME_TO_NOTE)?.let { data ->
            noteData = data as NoteData
            binding.etBody.setText(noteData?.body ?: "")
            binding.toolbar.title = noteData?.title ?: getString(R.string.default_title)
        } ?: run {
            noteData = NoteData(
                0,
                userUid,
                getString(R.string.default_title),
                "",
                Utils.getTimeStamp()
            )
        }
    }

    private fun setupEditText() {
        toolbarTitle = TextView(requireContext()).apply {
            visibility = View.GONE
            text = binding.toolbar.title
            textSize = 22f
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        toolbarEditTitle = EditText(requireContext()).apply {
            visibility = View.GONE
            textSize = 22f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            background = null
            setSingleLine()
        }

        binding.toolbar.addView(toolbarTitle)
        binding.toolbar.addView(toolbarEditTitle)

        binding.toolbar.setOnClickListener {
            binding.toolbar.title = ""
            toolbarTitle.visibility = View.GONE
            toolbarEditTitle.visibility = View.VISIBLE
            toolbarEditTitle.setText(toolbarTitle.text)
            toolbarEditTitle.requestFocus()
            toolbarEditTitle.setSelection(toolbarEditTitle.text.length)
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(toolbarEditTitle, InputMethodManager.SHOW_IMPLICIT)
        }

        toolbarEditTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                toolbarTitle.text = toolbarEditTitle.text
                toolbarTitle.visibility = View.VISIBLE
                toolbarEditTitle.visibility = View.GONE
            }
        }

        toolbarEditTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                toolbarTitle.text = s.toString()
            }
        })
    }

    private fun setupMenu() {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }

                    R.id.action_attach -> {
                        // Handle Attach click
                        true
                    }

                    R.id.action_more -> {
                        // Handle More click
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun focusAndShowKeyboard() {
        binding.etBody.requestFocus()
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etBody, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroy() {
        super.onDestroy()
        noteData?.let { data ->
            if (toolbarTitle.text.toString() != data.title || binding.etBody.text.toString() != data.body) {
                data.apply {
                    title = toolbarTitle.text.toString()
                    body = binding.etBody.text.toString()
                    timestamp = Utils.getTimeStamp()
                }
                Log.d("TAG", data.toString())
                if (data.id == 0)
                    viewModel.insertNote(data)
                else
                    viewModel.updateNote(data)
            }
        }
        _binding = null
    }
}
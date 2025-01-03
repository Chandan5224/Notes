package com.example.notes.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notes.R
import com.example.notes.adapter.NoteAdapter
import com.example.notes.adapter.OnNoteClick
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.model.NoteData
import com.example.notes.ui.MainActivity
import com.example.notes.ui.MainViewModel
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import java.util.Locale


class HomeFragment : Fragment(), OnNoteClick {
    private var notesList: ArrayList<NoteData>? = arrayListOf()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NoteAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        setupBackPressHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchView()
        observeNotes()
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        // Initialize RecyclerView
        val layoutManager = GridLayoutManager(context, 2) // 2 columns in the grid
        binding.recyclerView.layoutManager = layoutManager
        adapter = NoteAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.searchView.isVisible) {
                    hideSearchView()
                } else if (viewModel.longPress.value!!) {
                    offLongPress()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun hideSearchView() {
        binding.layoutCreateFirstNote.root.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        binding.tvFragmentTitle.visibility = View.VISIBLE
        binding.btnSearch.visibility = View.VISIBLE
        binding.btnProfile.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        viewModel.getNotesByUserUid(AppPreferences.getDataFromSharePreference(Constants.USER_UID)!!)
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)
        // Clear the original top constraint of the RecyclerView
        constraintSet.clear(binding.recyclerView.id, ConstraintSet.TOP)
        // Connect the top of the RecyclerView to the bottom of the SearchView
        val scale: Float = resources.displayMetrics.scaledDensity
        val marginInPixels = (8 * scale + 0.5f).toInt()  // 0.5f is added for rounding
        constraintSet.connect(
            binding.recyclerView.id,
            ConstraintSet.TOP,
            binding.btnSearch.id,
            ConstraintSet.BOTTOM,
            marginInPixels
        )
        // Apply the new constraints to the ConstraintLayout
        constraintSet.applyTo(binding.root)
    }

    private fun observeNotes() {
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            notes?.let {
                if (notes.isEmpty()) {
                    binding.layoutCreateFirstNote.root.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.layoutCreateFirstNote.tvCaption.text =
                        getString(R.string.create_your_first_note)
                    binding.layoutCreateFirstNote.imgBoyWithNotebook.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.boy_with_notebook
                        )
                    )
                } else {
                    notesList?.apply {
                        clear()
                        addAll(notes)
                    }
                    Log.d("troubleshoot", "first time $notes")
                    binding.layoutCreateFirstNote.root.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.differ.submitList(notes)
                    adapter.notifyItemChanged(0, notes.size)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnProfile.setOnClickListener {
            ProfileFragment().show(childFragmentManager, ProfileFragment().tag)
        }

        binding.btnAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_noteFragment)
        }

        binding.btnSearch.setOnClickListener {
            if (viewModel.longPress.value!!) {
                val deleteNotes = adapter.getListOfSelectedNoteIds()
                offLongPress()
                viewModel.deleteNotesByIds(
                    deleteNotes,
                    AppPreferences.getDataFromSharePreference(Constants.USER_UID)!!
                )
            } else {
                showSearchView()
            }
        }
    }

    private fun showSearchView() {
        // Make the search view visible
        binding.searchView.visibility = View.VISIBLE
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)
        // Clear the original top constraint of the RecyclerView
        constraintSet.clear(binding.recyclerView.id, ConstraintSet.TOP)
        // Connect the top of the RecyclerView to the bottom of the SearchView
        val scale: Float = resources.displayMetrics.scaledDensity
        val marginInPixels = (8 * scale + 0.5f).toInt()  // 0.5f is added for rounding
        constraintSet.connect(
            binding.recyclerView.id,
            ConstraintSet.TOP,
            binding.searchView.id,
            ConstraintSet.BOTTOM,
            marginInPixels
        )
        // Apply the new constraints to the ConstraintLayout
        constraintSet.applyTo(binding.root)

        binding.tvFragmentTitle.visibility = View.GONE
        binding.btnSearch.visibility = View.GONE
        binding.btnProfile.visibility = View.GONE
        binding.searchView.isIconified = false
        binding.searchView.requestFocus()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })
    }

    private fun performSearch(text: String) {
        if (notesList?.isEmpty() == true) {
            return
        }
        val filteredList = notesList!!.filter {
            it.title.toLowerCase(Locale.getDefault())
                .contains(text.toLowerCase(Locale.getDefault()))
        }
        if (filteredList.isEmpty()) {
            showEmptySearchState()
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.layoutCreateFirstNote.root.visibility = View.GONE
            adapter.differ.submitList(filteredList)
        }
    }

    private fun showEmptySearchState() {
        binding.layoutCreateFirstNote.root.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.layoutCreateFirstNote.imgBoyWithNotebook.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.note_not_found
            )
        )
        binding.layoutCreateFirstNote.tvCaption.text =
            getString(R.string.file_not_found_try_searching_again)
    }

    private fun offLongPress() {
        adapter.setNotSelected()
        viewModel.longPress.postValue(false)
        binding.btnAddNote.visibility = View.VISIBLE
        binding.btnSearch.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.search
            )
        )
    }


    override fun onClick(noteData: NoteData) {
        val bundle = Bundle().apply {
            putSerializable(Constants.FROM_HOME_TO_NOTE, noteData)
        }
        findNavController().navigate(R.id.action_homeFragment_to_noteFragment, bundle)
    }

    override fun onLongClick(noteData: NoteData, position: Int) {
        viewModel.longPress.postValue(true)
        adapter.setSelected(position)
        Log.d("TAG", "position $position $noteData")
        binding.btnAddNote.visibility = View.GONE
        binding.btnSearch.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.baseline_delete
            )
        )
    }

}
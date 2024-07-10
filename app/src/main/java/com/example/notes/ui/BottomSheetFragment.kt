package com.example.notes.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notes.R
import com.example.notes.databinding.FragmentBottomSheetBinding
import com.example.notes.model.NoteData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG_PARAM = "param"

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var noteData: NoteData? = null
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.ProfileSheetDialogTheme)
        arguments?.let {
            noteData = it.getSerializable(ARG_PARAM) as NoteData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBottomSheetBinding.inflate(layoutInflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDelete.setOnClickListener {
            noteData?.let { data ->
                viewModel.deleteNoteById(data.id, data.userUid)
                dismiss()
            }
        }

        binding.btnShare.setOnClickListener {
            noteData?.let { data ->
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, data.body)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share To:"))
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param: NoteData) =
            BottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM, param)
                }
            }
    }

}
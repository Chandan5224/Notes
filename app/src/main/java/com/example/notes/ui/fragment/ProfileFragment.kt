package com.example.notes.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.notes.R
import com.example.notes.databinding.FragmentProfileBinding
import com.example.notes.ui.MainActivity
import com.example.notes.ui.MainViewModel
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.ProfileSheetDialogTheme)
        mAuth = FirebaseAuth.getInstance()
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        binding.btnLogout.setOnClickListener {
            dismiss()
            mAuth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            GoogleSignIn.getClient(binding.root.context, gso).signOut()
            findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
            viewModel.clearAllData()
        }

        setView()
        return binding.root
    }

    private fun setView() {
        AppPreferences.getDataFromSharePreference(Constants.USER_NAME)?.let {
            binding.tvName.text = it
        }
        AppPreferences.getDataFromSharePreference(Constants.USER_IMAGE_URL)?.let {
            Glide.with(binding.root.context).load(it).into(binding.imgProfile)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
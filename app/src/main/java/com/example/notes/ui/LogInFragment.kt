package com.example.notes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.databinding.FragmentLogInBinding
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LogInFragment : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel


    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                binding.proBarGgg.visibility = View.GONE
                // Handle sign in failure
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        viewModel = (activity as MainActivity).viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        binding.proBarGgg.visibility = View.VISIBLE
        val signInIntent = mGoogleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    mAuth.currentUser?.let { user ->
                        Log.d("TAG", user.displayName.toString())

                        // Update UI with the signed-in user's information
                        AppPreferences.saveDataInSharePreference(Constants.LOGIN, "success")
                        AppPreferences.saveDataInSharePreference(
                            Constants.USER_UID,
                            user.uid
                        )
                        AppPreferences.saveDataInSharePreference(
                            Constants.USER_NAME,
                            user.displayName!!
                        )
                        AppPreferences.saveDataInSharePreference(
                            Constants.USER_IMAGE_URL,
                            user.photoUrl.toString()
                        )
                        viewModel.getNotesByUserUid(userUid = user.uid)
                    } ?: {
                        Toast.makeText(requireContext(), "Some error occur !", Toast.LENGTH_SHORT)
                            .show()
                    }

                    findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
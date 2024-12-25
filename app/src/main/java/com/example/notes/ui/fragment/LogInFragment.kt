package com.example.notes.ui.fragment

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
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.notes.R
import com.example.notes.databinding.FragmentLogInBinding
import com.example.notes.ui.MainActivity
import com.example.notes.ui.MainViewModel
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LogInFragment : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentLogInBinding
    private lateinit var viewModel: MainViewModel


    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error code: ${e.statusCode}, Message: ${e.message}")
                binding.proBarGgg.visibility = View.GONE
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLogInBinding.inflate(inflater, container, false)

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

        // Setting a placeholder image while the animation is being prepared
        binding.lottieAnimationView.setImageResource(R.drawable.img)

        // Preloading and playing the Lottie animation from the raw folder
        val resId = R.raw.sign_in_green // Replace with your actual file name
        LottieCompositionFactory.fromRawRes(requireContext(), resId).addListener { composition ->
            binding.lottieAnimationView.setComposition(composition)
            binding.lottieAnimationView.repeatCount = LottieDrawable.INFINITE
            binding.lottieAnimationView.playAnimation()
        }

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

}
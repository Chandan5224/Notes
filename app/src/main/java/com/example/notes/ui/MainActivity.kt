package com.example.notes.ui

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.notes.R
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.utils.AppPreferences
import com.example.notes.utils.Constants
import com.example.notes.utils.MyApplication
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = MyApplication.getAppViewModel(this@MainActivity)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        if (AppPreferences.getDataFromSharePreference(Constants.LOGIN) == "success") {
            Log.d("TAG", AppPreferences.getDataFromSharePreference(Constants.LOGIN).toString())
            navGraph.setStartDestination(R.id.homeFragment)
            navController.graph = navGraph
        }
    }
}
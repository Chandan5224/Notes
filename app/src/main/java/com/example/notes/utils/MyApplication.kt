package com.example.notes.utils

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.notes.ui.MainRepository
import com.example.notes.ui.MainViewModel
import com.example.notes.ui.ViewModelProviderFactory

class MyApplication : Application() {
    lateinit var appViewModel: MainViewModel
    private val appViewModelStore: ViewModelStore by lazy { ViewModelStore() }

    override fun onCreate() {
        super.onCreate()
        // Delay heavy initialization
        AppPreferences.initialize(this)
        val repository = MainRepository(this)
        appViewModel = ViewModelProvider(
            appViewModelStore,
            ViewModelProviderFactory(repository)
        )[MainViewModel::class.java]

    }


    companion object {
        fun getAppViewModel(activity: Activity): MainViewModel {
            return (activity.application as MyApplication).appViewModel
        }
    }

}
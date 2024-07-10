package com.example.notes.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.applicationContext.getSharedPreferences(
                "appPreferences",
                Context.MODE_PRIVATE
            )
        }
    }

    fun saveDataInSharePreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getDataFromSharePreference(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun removeDataSharePreference(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clearSharePreferences() {
        sharedPreferences.edit().clear().apply()
    }
}

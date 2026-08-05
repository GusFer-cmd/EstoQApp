package com.example.estoq.data.Preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _darkTheme = MutableStateFlow(
        prefs.getBoolean(KEY_DARK_THEME, false)
    )
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
        _darkTheme.value = enabled
    }

    fun toggleDarkTheme() {
        setDarkTheme(!_darkTheme.value)
    }

    private companion object {
        const val PREFS_NAME = "estoq_prefs"
        const val KEY_DARK_THEME = "dark_theme"
    }
}

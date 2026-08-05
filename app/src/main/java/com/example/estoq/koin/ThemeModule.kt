package com.example.estoq.koin

import com.example.estoq.data.Preferences.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val themeModule = module {
    single {
        ThemeManager(androidContext())
    }
}

package com.example.estoq.koin

import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    databaseModule,
    networkModule,
    repositoryModule,
    authModule,
    viewModelModule,
    notificationModule,
    themeModule
)
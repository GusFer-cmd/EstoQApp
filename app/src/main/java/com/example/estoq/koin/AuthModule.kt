package com.example.estoq.koin

import androidx.credentials.CredentialManager
import com.example.estoq.data.Repository.Auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {

    single {
        FirebaseAuth.getInstance()
    }

    single {
        CredentialManager.create(androidContext())
    }

    single {
        AuthRepository(
            auth = get(),
            credentialManager = get()
        )
    }
}
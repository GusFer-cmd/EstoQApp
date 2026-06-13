package com.example.estoq.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.estoq.data.Viewmodel.Auth.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    loginViewModel: LoginViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {

    LaunchedEffect(Unit) {

        delay(1500)

        if (loginViewModel.isUserLogged()) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "EstoQ")
    }
}
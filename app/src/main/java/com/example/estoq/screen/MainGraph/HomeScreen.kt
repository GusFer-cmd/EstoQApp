package com.example.estoq.screen.MainGraph

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.estoq.data.Viewmodel.Auth.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit
) {

    Button(
        onClick = {
            loginViewModel.logout()
            onLogout()
        }
    ) {
        Text(text = "Logout")
    }
}
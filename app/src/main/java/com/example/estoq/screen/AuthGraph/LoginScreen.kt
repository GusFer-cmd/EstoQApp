package com.example.estoq.screen.AuthGraph

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.R
import com.example.estoq.component.Container
import com.example.estoq.data.Viewmodel.Auth.LoginViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onNavigateToHome: () -> Unit
) {

    val context = LocalContext.current
    val activity  = context as Activity

    val focusManager = LocalFocusManager.current

    val state by loginViewModel.uiState.collectAsState()

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onNavigateToHome()
        }
    }

    Scaffold (

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            Container {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures (
                                onTap = {
                                    focusManager.clearFocus()
                                }
                            )
                        },
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Preencha os campos abaixo para acessar sua conta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(25.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { loginViewModel.onEmailChange(it) },
                        isError = state.emailError != null,
                        placeholder = { Text(text = "E-mail") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Rounded.Email, contentDescription = "Email Icon")
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    state.emailError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { loginViewModel.onPasswordChange(it) },
                        placeholder = { Text(text = "Senha") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Rounded.Lock, contentDescription = "Lock Icon")
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = {
                            loginViewModel.loginWithEmail()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Ou continue com")
                    }

                    OutlinedButton(
                        onClick = { Log.d("GOOGLE_LOGIN", "Botão clicado")
                            loginViewModel.singInWithGoogle(
                                activity,
                                context.getString(R.string.web_client_id)
                            ) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = "Login com o Google",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {

}
package com.example.estoq.screen.AuthGraph

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.R
import com.example.estoq.component.Container
import com.example.estoq.data.Viewmodel.Auth.LoginViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {

    val context = LocalContext.current
    val activity  = context as Activity

    val focusManager = LocalFocusManager.current

    val state by loginViewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var forgotPasswordDialogVisible by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onNavigateToHome()
        }
    }

    LaunchedEffect(state.forgotPasswordEmailSent) {
        if (state.forgotPasswordEmailSent) {
            forgotPasswordDialogVisible = false
            forgotPasswordEmail = ""
            snackbarHostState.showSnackbar("Email de recuperação enviado!")
            loginViewModel.clearForgotPasswordState()
        }
    }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState) }
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

                    state.authError?.let { errorMessage ->

                        Spacer(Modifier.height(10.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = "Erro",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

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
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                                                  else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Ocultar senha"
                                                         else "Exibir senha"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                                              else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    state.passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Esqueceu a senha?",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                forgotPasswordDialogVisible = true
                            }
                        )
                    }

                    Spacer(Modifier.height(15.dp))

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

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Não possue conta? ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Registre-se",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }

                    Text(
                        text = "Ou",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
            
                    OutlinedButton(
                        onClick = {
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
                            text = "Continue com o Google",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }

    if (forgotPasswordDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                forgotPasswordDialogVisible = false
                loginViewModel.clearForgotPasswordState()
            },
            title = { Text("Recuperar senha") },
            text = {
                Column {
                    Text(
                        text = "Digite seu e-mail para receber o link de redefinição de senha.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = forgotPasswordEmail,
                        onValueChange = { forgotPasswordEmail = it },
                        placeholder = { Text("E-mail") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Email, contentDescription = "Email")
                        },
                        isError = state.forgotPasswordError != null,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.forgotPasswordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { loginViewModel.forgotPassword(forgotPasswordEmail) }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    forgotPasswordDialogVisible = false
                    loginViewModel.clearForgotPasswordState()
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
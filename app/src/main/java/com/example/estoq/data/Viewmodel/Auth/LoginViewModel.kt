package com.example.estoq.data.Viewmodel.Auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estoq.data.Exceptions.Login.LoginException
import com.example.estoq.data.Repository.Auth.AuthRepository
import com.example.estoq.data.Ui_State.AuthResponse
import com.example.estoq.data.Ui_State.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel (
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState

    fun resetState() {
        _uiState.value = LoginUiState()
    }

    fun clearForgotPasswordState() {
        _uiState.value = _uiState.value.copy(
            forgotPasswordEmailSent = false,
            forgotPasswordError = null
        )
    }

    fun onEmailChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                email = value, emailError = null
            )
    }

    fun onPasswordChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                password = value, passwordError = null
            )
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                confirmPassword = value, confirmPasswordError = null
            )
    }

    fun isUserLogged() : Boolean {
        return repository.getCurrentUser() != null
    }

    fun checkAuthentication() {

        val currentUser = repository.getCurrentUser()

        _uiState.value = _uiState.value.copy(
            isAuthenticated = currentUser != null
        )
    }

    fun createAccountWithEmailAndPassword() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.email.isBlank()) {
                    throw LoginException.EmptyEmailException()
                }

                if (state.password.isBlank()) {
                    throw LoginException.EmptyPasswordException()
                }

                if (state.password != state.confirmPassword) {
                    throw LoginException.PasswordMismatchException()
                }

                repository.createAccountWithEmailAndPassword(
                    state.email,
                    state.password
                ).collectLatest { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            _uiState.value = state.copy(
                                isAuthenticated = true,
                                authError = null
                            )

                            resetState()
                        }

                        is AuthResponse.Error -> {
                            when (response.errorCode) {
                                "ERROR_EMAIL_ALREADY_IN_USE" ->
                                    throw LoginException.EmailAlreadyInUseException()
                                "ERROR_INVALID_EMAIL" ->
                                    throw LoginException.InvalidEmailException()
                                else ->
                                    _uiState.value = state.copy(
                                        authError = response.errorMessage
                                    )
                            }
                        }
                    }
                }
            } catch (e: LoginException) {
                _uiState.value = when (e) {
                    is LoginException.EmptyEmailException ->
                        state.copy(emailError = e.message)

                    is LoginException.EmptyPasswordException ->
                        state.copy(passwordError = e.message)

                    is LoginException.InvalidEmailException ->
                        state.copy(emailError = e.message)

                    is LoginException.PasswordMismatchException ->
                        state.copy(confirmPasswordError = e.message)

                    is LoginException.EmailAlreadyInUseException ->
                        state.copy(authError = e.message)

                    is LoginException.ItemUnknownException ->
                        state.copy(authError = e.message)

                    else -> state
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    authError = LoginException.ItemUnknownException().message
                )
            }
        }
    }

    fun loginWithEmail() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.email.isBlank()) {
                    throw LoginException.EmptyEmailException()
                }

                if (state.password.isBlank()) {
                    throw LoginException.EmptyPasswordException()
                }

                repository.loginWithEmail(
                    state.email,
                    state.password
                ).collectLatest { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            _uiState.value = state.copy(
                                isAuthenticated = true,
                                authError = null
                            )
                        }

                        is AuthResponse.Error -> {
                            when (response.errorCode) {
                                "ERROR_INVALID_EMAIL" ->
                                    throw LoginException.InvalidEmailException()
                                else ->
                                    throw LoginException.WrongCredentialsException()
                            }
                        }
                    }
                }
            } catch (e: LoginException) {
                _uiState.value = when (e) {
                    is LoginException.EmptyEmailException ->
                        state.copy(emailError = e.message)

                    is LoginException.EmptyPasswordException ->
                        state.copy(passwordError = e.message)

                    is LoginException.InvalidEmailException ->
                        state.copy(emailError = e.message)

                    is LoginException.WrongCredentialsException ->
                        state.copy(authError = e.message)

                    is LoginException.ItemUnknownException ->
                        state.copy(authError = e.message)

                    else -> state
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    authError = LoginException.ItemUnknownException().message
                )
            }
        }
    }

    fun singInWithGoogle(activity: Activity, webClientId: String) {

        viewModelScope.launch {
            repository.singInWithGoogle(activity, webClientId)
                .collectLatest { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isAuthenticated = true,
                                authError = null
                            )
                        }

                        is AuthResponse.Error -> {
                            _uiState.value = _uiState.value.copy(
                                authError = response.errorMessage
                            )
                        }
                    }
                }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                if (email.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        forgotPasswordError = "Informe seu e-mail"
                    )
                } else {
                    repository.sendPasswordResetEmail(email)
                    .collectLatest { response ->
                        when (response) {
                            is AuthResponse.Success -> {
                                _uiState.value = _uiState.value.copy(
                                    forgotPasswordEmailSent = true,
                                    forgotPasswordError = null
                                )
                            }

                            is AuthResponse.Error -> {
                                when (response.errorCode) {
                                    "ERROR_INVALID_EMAIL" ->
                                        throw LoginException.InvalidEmailException()
                                    "ERROR_USER_NOT_FOUND" ->
                                        throw LoginException.WrongCredentialsException()
                                    else ->
                                        throw LoginException.ItemUnknownException()
                                }
                            }
                        }
                    }
                }
            } catch (e: LoginException) {
                _uiState.value = _uiState.value.copy(
                    forgotPasswordError = e.message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    forgotPasswordError = "Ocorreu um erro inesperado"
                )
            }
        }
    }

    fun logout() {

        repository.logout()

        _uiState.value = LoginUiState()
    }
}
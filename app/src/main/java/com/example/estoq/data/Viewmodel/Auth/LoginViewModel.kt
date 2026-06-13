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

        try {
            if (state.email.isBlank()) {
                throw LoginException.EmptyEmailException()
            }

            if (state.password.isBlank()) {
                throw LoginException.EmptyPasswordException()
            }

            viewModelScope.launch {
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
                    state.copy(
                        emailError = e.message
                    )

                is LoginException.EmptyPasswordException ->
                    state.copy(
                        passwordError = e.message
                    )

                else -> state
            }
        }
    }

    fun loginWithEmail() {

        val state = _uiState.value

        try {
            if (state.email.isBlank()) {
                throw LoginException.EmptyEmailException()
            }

            if (state.password.isBlank()) {
                throw LoginException.EmptyPasswordException()
            }

            viewModelScope.launch {
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
                    state.copy(
                        emailError = e.message
                    )

                is LoginException.EmptyPasswordException ->
                    state.copy(
                        passwordError = e.message
                    )

                else -> state
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

    fun logout() {

        repository.logout()

        _uiState.value = LoginUiState()
    }
}
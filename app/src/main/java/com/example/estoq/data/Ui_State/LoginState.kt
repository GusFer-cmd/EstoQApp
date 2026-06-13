package com.example.estoq.data.Ui_State

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,

    val password: String = "",
    val passwordError: String? = null,

    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,

    val authError: String? = null,

    val forgotPasswordEmailSent: Boolean = false,
    val forgotPasswordError: String? = null,

    val isAuthenticated: Boolean = false
)
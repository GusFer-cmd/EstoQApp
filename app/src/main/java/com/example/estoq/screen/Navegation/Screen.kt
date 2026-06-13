package com.example.estoq.screen.Navegation

sealed class Screen(
    val route: String
) {

    object Splash : Screen("splash")

    object Login : Screen("login")

    object Register : Screen("register")

    object Home : Screen("home")
}
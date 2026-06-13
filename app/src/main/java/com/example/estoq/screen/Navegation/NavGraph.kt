package com.example.estoq.screen.Navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import com.example.estoq.data.Viewmodel.Auth.LoginViewModel
import com.example.estoq.screen.AuthGraph.LoginScreen
import com.example.estoq.screen.MainGraph.HomeScreen
import com.example.estoq.screen.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {

            val loginViewModel: LoginViewModel = koinViewModel()

            SplashScreen (
                loginViewModel = loginViewModel,
                onNavigateToLogin = {
                    navController.navigate(
                        Screen.Login.route
                    ) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(
                        Screen.Home.route
                    ) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {

            val loginViewModel: LoginViewModel = koinViewModel()

            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateToHome = {
                    navController.navigate(
                        Screen.Home.route
                    ) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {

            val loginViewModel: LoginViewModel = koinViewModel()

            HomeScreen(
                loginViewModel = loginViewModel,
                onLogout = {
                    navController.navigate(
                        Screen.Login.route
                    ) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
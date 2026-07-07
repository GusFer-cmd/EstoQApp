package com.example.estoq.screen.Navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import com.example.estoq.data.Viewmodel.Auth.LoginViewModel
import com.example.estoq.data.Viewmodel.Client.ClientViewModel
import com.example.estoq.data.Viewmodel.Item.ItemViewModel
import com.example.estoq.data.Viewmodel.Storage.StorageViewModel
import com.example.estoq.screen.AuthGraph.LoginScreen
import com.example.estoq.screen.AuthGraph.RegisterScreen
import com.example.estoq.screen.MainGraph.Client.ClientCreateScreen
import com.example.estoq.screen.MainGraph.Client.ClientDetailScreen
import com.example.estoq.screen.MainGraph.Client.ClientScreen
import com.example.estoq.screen.MainGraph.Client.ClientUpdateScreen
import com.example.estoq.screen.MainGraph.HomeScreen
import com.example.estoq.screen.MainGraph.Item.ItemCreateScreen
import com.example.estoq.screen.MainGraph.Item.ItemScreen
import com.example.estoq.screen.MainGraph.Item.ItemUpdateScreen
import com.example.estoq.screen.MainGraph.Storage.StorageCreateScreen
import com.example.estoq.screen.MainGraph.Storage.StorageScreen
import com.example.estoq.screen.MainGraph.Storage.StorageUpdateScreen
import com.example.estoq.screen.SplashScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val storageViewModel: StorageViewModel = koinViewModel()
    val itemViewModel: ItemViewModel = koinViewModel()
    val clientViewModel: ClientViewModel = koinViewModel()
    val storages by storageViewModel.allStorages.collectAsState(initial = emptyList())
    val items by itemViewModel.allItems.collectAsState(initial = emptyList())
    val clients by clientViewModel.allClients.collectAsState(initial = emptyList())

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
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
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {

             val loginViewModel: LoginViewModel = koinViewModel()

            RegisterScreen(
                loginViewModel = loginViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(
                        Screen.Home.route
                    ) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {

            val loginViewModel: LoginViewModel = koinViewModel()

            val storages by storageViewModel.allStorages.collectAsState(initial = emptyList())

            HomeScreen(
                loginViewModel = loginViewModel,
                totalStorages = storages.size,
                totalItems = items.size,
                totalClients = clients.size,
                onLogout = {
                    navController.navigate(
                        Screen.Login.route
                    ) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.StorageIndex.route) {
            StorageScreen(
                storageViewModel = storageViewModel,
                onNavigateToCreate = {
                    storageViewModel.resetState()
                    navController.navigate(Screen.StorageCreate.route)
                },
                onNavigateToUpdate = { id ->
                    navController.navigate(Screen.StorageUpdate.createRoute(id))
                }
            )
        }

        composable(Screen.StorageCreate.route) {
            StorageCreateScreen(
                storageViewModel = storageViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.StorageUpdate.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            StorageUpdateScreen(
                id = id,
                storageViewModel = storageViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(Screen.ItemIndex.route) {
            ItemScreen(
                itemViewModel = itemViewModel,
                onNavigateToCreate = {
                    itemViewModel.resetState()
                    navController.navigate(Screen.ItemCreate.route)
                },
                onNavigateToUpdate = { id ->
                    navController.navigate(Screen.ItemUpdate.createRoute(id))
                }
            )
        }

        composable(Screen.ItemCreate.route) {
            ItemCreateScreen(
                itemViewModel = itemViewModel,
                availableStorages = storages,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ItemUpdate.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            ItemUpdateScreen(
                id = id,
                itemViewModel = itemViewModel,
                availableStorages = storages,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ClientIndex.route) {
            ClientScreen(
                clientViewModel = clientViewModel,
                onNavigateToCreate = {
                    clientViewModel.resetState()
                    navController.navigate(Screen.ClientCreate.route)
                },
                onNavigateToUpdate = { id ->
                    navController.navigate(Screen.ClientUpdate.createRoute(id))
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.ClientDetail.createRoute(id))
                }
            )
        }

        composable(Screen.ClientCreate.route) {
            ClientCreateScreen(
                clientViewModel = clientViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ClientUpdate.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            ClientUpdateScreen(
                id = id,
                clientViewModel = clientViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ClientDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            ClientDetailScreen(
                id = id,
                clientViewModel = clientViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
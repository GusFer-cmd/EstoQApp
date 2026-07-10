package com.example.estoq.screen.Navegation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String
) {

    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")

    object StorageIndex : Screen("storage/index")
    object StorageCreate : Screen("storage/create")
    object StorageUpdate : Screen("storage/update/{id}") {
        fun createRoute(id: Long) = "storage/update/$id"
    }

    object ItemIndex : Screen("item/index")
    object ItemCreate : Screen("item/create")
    object ItemUpdate : Screen("item/update/{id}") {
        fun createRoute(id: Long) = "item/update/$id"
    }
    object ItemIndexStorage : Screen("item/storage/{storageId}") {
        fun createRoute(storageId: Long) = "item/storage/$storageId"
    }

    object ClientIndex : Screen("client/index")
    object ClientCreate : Screen("client/create")
    object ClientUpdate : Screen("client/update/{id}") {
        fun createRoute(id: Long) = "client/update/$id"
    }
    object ClientDetail : Screen("client/detail/{id}") {
        fun createRoute(id: Long) = "client/detail/$id"
    }

    object SalesIndex : Screen("sales/index")
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)
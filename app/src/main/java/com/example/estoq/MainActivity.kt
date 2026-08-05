package com.example.estoq

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.estoq.component.NavBottomBar.NavegationBar
import com.example.estoq.data.Preferences.ThemeManager
import com.example.estoq.notification.manager.NotificationPermissionManager
import com.example.estoq.screen.Navegation.AppNavGraph
import com.example.estoq.screen.Navegation.BottomNavItem
import com.example.estoq.screen.Navegation.Screen
import com.example.estoq.ui.theme.EstoQTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val permissionManager: NotificationPermissionManager by inject()
    private val themeManager: ThemeManager by inject()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permissionManager.requestPermission(this)
        setContent {
            val darkTheme by themeManager.darkTheme.collectAsState()
            EstoQTheme(darkTheme = darkTheme) {
                navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavItems = listOf(
                    BottomNavItem(Screen.Home, Icons.Default.Home, "Início"),
                    BottomNavItem(Screen.StorageIndex, Icons.Default.Inventory, "Estoques"),
                    BottomNavItem(Screen.ItemIndex, Icons.Default.Checklist, "Items"),
                    BottomNavItem(Screen.ClientIndex, Icons.Default.People, "Clientes"),
                    BottomNavItem(Screen.SalesIndex, Icons.Default.AttachMoney, "Vendas")
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute in bottomNavItems.map { it.screen.route }) {
                            NavegationBar(
                                navController = navController,
                                items = bottomNavItems,
                                modifier = Modifier
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        darkTheme = darkTheme,
                        onToggleTheme = { themeManager.toggleDarkTheme() }
                    )
                }

                androidx.compose.runtime.LaunchedEffect(navController) {
                    navController.handleDeepLink(intent)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (::navController.isInitialized) {
            navController.handleDeepLink(intent)
        }
    }
}

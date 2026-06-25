package com.example.estoq.screen.MainGraph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.estoq.component.Container
import com.example.estoq.component.HomeCard
import com.example.estoq.data.Viewmodel.Auth.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    loginViewModel: LoginViewModel,
    totalStorages: Int,
    totalItems: Int,
    onLogout: () -> Unit
) {

    val storages = totalStorages
    val items = totalItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Início")}
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            Column {
                Container {
                    LazyVerticalGrid(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(25.dp),
                        columns = GridCells.Fixed(2)
                    ) {
                        item {
                            HomeCard(
                                cardIcon = Icons.Default.Inventory,
                                cardTittle = "Estoques",
                                cardTotalNumber = storages.toLong(),
                                cardBackground = Color.Red
                            )
                        }

                        item {
                            HomeCard(
                                cardIcon = Icons.Default.PeopleAlt,
                                cardTittle = "Clientes",
                                cardTotalNumber = 19,
                                cardBackground = Color.Cyan
                            )
                        }

                        item {
                            HomeCard(
                                cardIcon = Icons.Default.Checklist,
                                cardTittle = "Items",
                                cardTotalNumber = items.toLong(),
                                cardBackground = Color.DarkGray
                            )
                        }

                        item {
                            HomeCard(
                                cardIcon = Icons.Default.AttachMoney,
                                cardTittle = "Vendas",
                                cardTotalNumber = 19,
                                cardBackground = Color.Magenta
                            )
                        }
                    }
                }
            }
        }
    }
}
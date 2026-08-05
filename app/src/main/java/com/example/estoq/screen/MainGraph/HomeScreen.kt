package com.example.estoq.screen.MainGraph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.component.Container
import com.example.estoq.component.GraphCard
import com.example.estoq.component.HomeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    totalStorages: Int,
    totalItems: Int,
    totalClients: Int,
    totalSales: Int,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBestSeller: () -> Unit,
    onWorstSeller: () -> Unit,
    onLastUnit: () -> Unit,
    onMonthlyProfit: () -> Unit,
    onLogout: () -> Unit
) {

    val storages = totalStorages
    val items = totalItems
    val clients = totalClients
    val sales = totalSales

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Início")},
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = if (darkTheme) "Modo claro" else "Modo escuro"
                        )
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC0000),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Output,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = "Sair",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(Modifier.width(12.dp))
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
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
                                cardBackground = Color(0xFFBD4444)
                            )
                        }

                        item {
                            HomeCard(
                                cardIcon = Icons.Default.PeopleAlt,
                                cardTittle = "Clientes",
                                cardTotalNumber = clients.toLong(),
                                cardBackground = Color(0XFF4B5694)
                            )
                        }

                        item {
                            HomeCard(
                                cardIcon = Icons.Default.Checklist,
                                cardTittle = "Items",
                                cardTotalNumber = items.toLong(),
                                cardBackground = Color(0xFF85409D)
                            )
                        }

                        item {
                            HomeCard(
                                cardIcon = Icons.Default.AttachMoney,
                                cardTittle = "Vendas",
                                cardTotalNumber = sales.toLong(),
                                cardBackground = Color(0XFF467235)
                            )
                        }
                    }

                    Spacer(Modifier.height(30.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Métricas de desempenho",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(10.dp))

                        LazyColumn (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                GraphCard(
                                    tittle = "Mais vendidos",
                                    description = "Itens mais vendidos do estoque",
                                    icon = painterResource(id = com.example.estoq.R.drawable.flame),
                                    onNavigate = { onBestSeller() }
                                )
                            }

                            item {
                                GraphCard(
                                    tittle = "Menos vendidos",
                                    description = "Itens menos vendidos do estoque",
                                    icon = painterResource(id = com.example.estoq.R.drawable.snowflake),
                                    onNavigate = { onWorstSeller() }
                                )
                            }

                            item {
                                GraphCard(
                                    tittle = "Últimas unidades",
                                    description = "Poucas unidades no estoque",
                                    icon = painterResource(id = com.example.estoq.R.drawable.alert),
                                    onNavigate = { onLastUnit() }
                                )
                            }

                            item {
                                GraphCard(
                                    tittle = "Lucro Mensal",
                                    description = "Vendas por mês",
                                    icon = painterResource(id = com.example.estoq.R.drawable.coin),
                                    onNavigate = { onMonthlyProfit() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.estoq.screen.MainGraph.SaleArchive

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.estoq.component.Container
import com.example.estoq.component.SearchBar
import com.example.estoq.data.Viewmodel.SaleArchive.SaleArchiveViewModel
import com.example.estoq.screen.MainGraph.SaleArchive.components.SaleArchiveCard
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleArchiveScreen(
    saleArchiveViewModel: SaleArchiveViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val state by saleArchiveViewModel.uiState.collectAsState()
    val sales by saleArchiveViewModel.allSales.collectAsState(initial = emptyList())
    val clients by saleArchiveViewModel.allClients.collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }
    
    val focusManager = LocalFocusManager.current

    val clientMap = remember(clients) {
        clients.associate { it.id to "${it.firstName} ${it.lastName}".trim() }
    }

    val filteredSales = remember(sales, clientMap, state.clientSearchQuery) {
        if (state.clientSearchQuery.isBlank()) sales
        else sales.filter { sale ->
            val name = clientMap[sale.clientId] ?: ""
            name.contains(state.clientSearchQuery, ignoreCase = true)
        }
    }

    val priceFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            snackbarHostState.showSnackbar(
                message = "Venda deletada com sucesso!",
                duration = SnackbarDuration.Short
            )
            saleArchiveViewModel.clearIsDeleted()
        }
    }

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            snackbarHostState.showSnackbar(
                message = "Venda criada com sucesso!",
                duration = SnackbarDuration.Short
            )
            saleArchiveViewModel.clearIsCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendas") },
                actions = {
                    Button(
                        onClick = {
                            saleArchiveViewModel.resetState()
                            onNavigateToCreate()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = "Nova Venda",
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
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    snackbar = { snackbarData ->
                        val snackbarColors = when {
                            snackbarData.visuals.message.contains("sucesso", ignoreCase = true) ->
                                MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
                            else ->
                                MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
                        }

                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = snackbarColors.first,
                            contentColor = snackbarColors.second,
                        )
                    }
                )

                SearchBar(
                    value = state.clientSearchQuery,
                    onValueChange = { saleArchiveViewModel.onClientSearchQueryChange(it) },
                    placeholder = "Pesquisar por cliente...",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (filteredSales.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (sales.isEmpty()) "Nenhuma venda encontrada"
                                       else "Nenhuma venda encontrada para \"${
                                           state.clientSearchQuery
                                       }\"",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Container {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredSales, key = { it.id }) { sale ->
                                    SaleArchiveCard(
                                        sale = sale,
                                        clientName = clientMap[sale.clientId] ?: "Cliente desconhecido",
                                        priceFormat = priceFormat,
                                        dateFormat = dateFormat,
                                        onClick = { onNavigateToDetail(sale.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.estoq.screen.MainGraph.Analytic

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.estoq.component.Container
import com.example.estoq.component.LastUnitCard
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Viewmodel.Analytic.AnalyticViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastUnitScreen(
    analyticViewModel: AnalyticViewModel,
    onNavigateBack: () -> Unit
) {

    val state by analyticViewModel.uiState.collectAsState()
    val items by analyticViewModel.lowStockItems.collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    val showDialog = remember { mutableStateOf(false) }

    var restockItem by remember { mutableStateOf<Item?>(null) }
    var restockQuantity by remember { mutableStateOf("") }

    if (showDialog.value) {
        val item = restockItem
        if (item != null) {
            AlertDialog(
                onDismissRequest = {
                    showDialog.value = false
                    restockItem = null
                    restockQuantity = ""
                },
                shape = RoundedCornerShape(20.dp),
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Repor estoque",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Estoque atual: ${item.stockQuantity} unidades",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = restockQuantity,
                            onValueChange = { restockQuantity = it.filter { c -> c.isDigit() }.take(6) },
                            label = { Text("Nova quantidade") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                showDialog.value = false
                                restockItem = null
                                restockQuantity = ""
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC0000),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                val quantity = restockQuantity.toIntOrNull() ?: 0
                                if (quantity > 0) {
                                    analyticViewModel.addStock(item.id, quantity)
                                    showDialog.value = false
                                    restockItem = null
                                    restockQuantity = ""
                                }
                            },
                            enabled = (restockQuantity.toIntOrNull() ?: 0) > 0,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text(
                                text = "Repor",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    }


    LaunchedEffect(state.isRestocked) {
        if (state.isRestocked) {
            snackbarHostState.showSnackbar(
                message = "Estoque reposto com sucesso.",
                duration = SnackbarDuration.Short
            )
            analyticViewModel.clearIsRestocked()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Últimas Unidades") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .consumeWindowInsets(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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

                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum item com estoque baixo",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Container {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            items(items, key = { it.id }) { item ->
                                LastUnitCard(
                                    item = item,
                                    onRestockClick = {
                                        restockItem = item
                                        showDialog.value = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.estoq.screen.MainGraph.Storage

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
import androidx.compose.material3.FloatingActionButton
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
import com.example.estoq.component.StorageCard
import com.example.estoq.data.Viewmodel.Storage.StorageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    storageViewModel: StorageViewModel,
    onNavigateToItemStorage: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToUpdate: (Long) -> Unit
) {

    val storages by storageViewModel.filteredStorages.collectAsState(initial = emptyList())
    val searchQuery by storageViewModel.searchQuery.collectAsState()
    val state by storageViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            snackbarHostState.showSnackbar(
                message = "Estoque deletado com sucesso!",
                duration = SnackbarDuration.Short
            )
            storageViewModel.clearIsDeleted()
        }
    }

    LaunchedEffect(state.isUpdated) {
        if (state.isUpdated) {
            snackbarHostState.showSnackbar(
                message = "Estoque atulizado com sucesso!",
                duration = SnackbarDuration.Short
            )
            storageViewModel.clearIsUpdated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estoques")},
                actions = {
                    Button(
                        onClick = onNavigateToCreate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = "Adicionar",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures (
                            onTap = {
                                focusManager.clearFocus()
                            }
                        )
                    }
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    snackbar = { snackbarData ->
                        val backgroundColor = when {
                            snackbarData.visuals.message.contains("sucesso", ignoreCase = true) ->
                                Color(0xFF2B5748)
                            else ->
                                Color(0xFF95271D)
                        }

                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = backgroundColor,
                            contentColor = Color.White,
                        )
                    }
                )

                SearchBar(
                    value = searchQuery,
                    onValueChange = { storageViewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar por titulo",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (storages.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum estoque encontrado",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Container {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(28.dp),
                            ) {
                                items(storages, key = { it.id }) { storage ->
                                    StorageCard(
                                        storage = storage,
                                        onNavigateToItemStorage = { onNavigateToItemStorage(storage.id) },
                                        onEditClick = { onNavigateToUpdate(storage.id) },
                                        onDeleteClick = { storageViewModel.deleteStorage(storage.id) }
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

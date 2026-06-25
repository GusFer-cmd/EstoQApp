package com.example.estoq.screen.MainGraph.Storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.estoq.component.Container
import com.example.estoq.component.StorageCard
import com.example.estoq.data.Viewmodel.Storage.StorageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    storageViewModel: StorageViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToUpdate: (Long) -> Unit
) {

    val storages by storageViewModel.allStorages.collectAsState(initial = emptyList())
    val state by storageViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            snackbarHostState.showSnackbar("Estoque deletado com sucesso!")
            storageViewModel.clearIsDeleted()
        }
    }

    LaunchedEffect(state.isUpdated) {
        if (state.isUpdated) {
            snackbarHostState.showSnackbar("Estoque atulizado com sucesso!")
            storageViewModel.clearIsUpdated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estoques")}
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Storage"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
                                    .padding(bottom = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(28.dp),
                            ) {
                                items(storages, key = { it.id }) { storage ->
                                    StorageCard(
                                        storage = storage,
                                        onEditClick = { onNavigateToUpdate(storage.id) },
                                        onDeleteClick = { storageViewModel.deleteStorage(storage) }
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

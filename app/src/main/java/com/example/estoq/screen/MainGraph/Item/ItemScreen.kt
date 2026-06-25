package com.example.estoq.screen.MainGraph.Item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.estoq.component.ItemCard
import com.example.estoq.data.Viewmodel.Item.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    itemViewModel: ItemViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToUpdate: (Long) -> Unit
) {

    val storages by itemViewModel.allStorages.collectAsState(initial = emptyList())

    val state by itemViewModel.uiState.collectAsState()

    val searchQuery by itemViewModel.searchQuery.collectAsState()

    val itemsFlow = remember(searchQuery) {
        if (searchQuery.isNotBlank()) itemViewModel.searchByNameOrBrand(searchQuery)
        else itemViewModel.allItems
    }
    val items by itemsFlow.collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    val storageMap = remember(storages) {
        storages.associate { it.id to it.title }
    }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            snackbarHostState.showSnackbar("Item deletado com sucesso!")
            itemViewModel.clearIsDeleted()
        }
    }

    LaunchedEffect(state.isUpdated) {
        if (state.isUpdated) {
            snackbarHostState.showSnackbar("Item atualizado com sucesso!")
            itemViewModel.clearIsUpdated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Items") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Item"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { itemViewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Buscar por nome ou marca") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = if (searchQuery.isNotBlank()) {
                            {
                                IconButton(onClick = { itemViewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Limpar",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        } else null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum item encontrado",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Container {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(bottom = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(28.dp)
                            ) {
                                items(items, key = { it.id }) { item ->
                                    ItemCard(
                                        item = item,
                                        storageName = storageMap[item.storageId] ?: "Desconhecido",
                                        onEditClick = { onNavigateToUpdate(item.id) },
                                        onDeleteClick = { itemViewModel.deleteItem(item) }
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

package com.example.estoq.screen.MainGraph.Item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.estoq.component.Container
import com.example.estoq.component.ItemCard
import com.example.estoq.component.SearchBar
import com.example.estoq.data.Model.Item.ItemType
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
    val selectedTypeFilter by itemViewModel.selectedTypeFilter.collectAsState()

    val items by itemViewModel.filteredItems.collectAsState(initial = emptyList())

    val focusManager = LocalFocusManager.current

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
                title = { Text("Items") },
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
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                SearchBar(
                    value = searchQuery,
                    onValueChange = { itemViewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar por nome ou marca",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTypeFilter == null,
                        onClick = { itemViewModel.onTypeFilterChange(null) },
                        label = { Text("Todas") }
                    )
                    ItemType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedTypeFilter == type,
                            onClick = { itemViewModel.onTypeFilterChange(type) },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

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
                                        onDeleteClick = { itemViewModel.deleteItem(item.id) }
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

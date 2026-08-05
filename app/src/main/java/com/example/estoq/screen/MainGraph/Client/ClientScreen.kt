package com.example.estoq.screen.MainGraph.Client

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.component.ClientCard
import com.example.estoq.component.SearchBar
import com.example.estoq.data.Viewmodel.Client.ClientSection
import com.example.estoq.data.Viewmodel.Client.ClientViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(
    clientViewModel: ClientViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToUpdate: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val sections by clientViewModel.sections.collectAsState(initial = emptyList())
    val state by clientViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val searchQuery by clientViewModel.searchQuery.collectAsState()

    val alphabet = remember { ('A'..'Z').toList() }

    val letterIndexMap = remember(sections) {
        val map = mutableMapOf<Char, Int>()
        var index = 0
        for (section in sections) {
            map[section.letter] = index
            index += 1 + section.clients.size
        }
        map
    }

    val currentLetter by remember {
        derivedStateOf {
            val visibleIndex = lazyListState.firstVisibleItemIndex
            letterIndexMap.entries
                .filter { it.value <= visibleIndex }
                .maxByOrNull { it.value }
                ?.key
        }
    }

    val isScrolling by remember { derivedStateOf { lazyListState.isScrollInProgress } }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            snackbarHostState.showSnackbar(
                message = "Cliente deletado com sucesso!",
                duration = SnackbarDuration.Short
            )
            clientViewModel.clearIsDeleted()
        }
    }

    LaunchedEffect(state.isUpdated) {
        if (state.isUpdated) {
            snackbarHostState.showSnackbar(
                message = "Cliente atualizado com sucesso!",
                duration = SnackbarDuration.Short
            )
            clientViewModel.clearIsUpdated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
                actions = {
                    Button(
                        onClick = onNavigateToCreate,
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
                    value = searchQuery,
                    onValueChange = { clientViewModel.onSearchQueryChange(it) },
                    placeholder = "Pesquisar cliente...",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (sections.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum cliente encontrado",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                sections.forEach { section ->
                                    stickyHeader {
                                        Text(
                                            text = section.letter.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface)
                                                .padding(vertical = 8.dp)
                                        )
                                    }

                                    items(section.clients, key = { it.id }) { client ->
                                        ClientCard(
                                            client = client,
                                            onClick = { onNavigateToDetail(client.id) },
                                            onEditClick = { onNavigateToUpdate(client.id) },
                                            onDeleteClick = { clientViewModel.deleteClient(client.id) }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.width(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        awaitEachGesture {
                                            val down = awaitFirstDown(requireUnconsumed = false)
                                            val idx = (down.position.y / size.height * alphabet.size)
                                                .toInt().coerceIn(0, alphabet.size - 1)
                                            coroutineScope.launch {
                                                lazyListState.animateScrollToItem(
                                                    letterIndexMap[alphabet[idx]] ?: 0
                                                )
                                            }
                                            do {
                                                val event = awaitPointerEvent()
                                                val change = event.changes.firstOrNull() ?: break
                                                if (change.pressed) {
                                                    val i = (change.position.y / size.height * alphabet.size)
                                                        .toInt().coerceIn(0, alphabet.size - 1)
                                                    coroutineScope.launch {
                                                        lazyListState.animateScrollToItem(
                                                            letterIndexMap[alphabet[i]] ?: 0
                                                        )
                                                    }
                                                }
                                            } while (event.changes.any { it.pressed })
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    alphabet.forEach { letter ->
                                        Text(
                                            text = letter.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = if (letterIndexMap.containsKey(letter))
                                                FontWeight.Bold else FontWeight.Normal,
                                            color = if (letter == currentLetter)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isScrolling && currentLetter != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentLetter?.toString() ?: "",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

package com.example.estoq.screen.MainGraph.Storage

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.estoq.component.ColorPicker
import com.example.estoq.component.Container
import com.example.estoq.data.Viewmodel.Storage.StorageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageUpdateScreen(
    id: Long,
    storageViewModel: StorageViewModel,
    onNavigateBack: () -> Unit
) {

    val focusManager = LocalFocusManager.current

    val state by storageViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        storageViewModel.getById(id)
    }

    LaunchedEffect(state.isUpdated) {
        if (state.isUpdated) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Estoque") },
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
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Container {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .pointerInput(Unit) {
                                detectTapGestures (
                                    onTap = {
                                        focusManager.clearFocus()
                                    }
                                )
                            }
                    ) {
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Título",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { storageViewModel.onTitleChange(it) },
                            isError = state.titleError != null,
                            placeholder = { Text("Nome do estoque") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        state.titleError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Cor",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(12.dp))

                        ColorPicker(
                            hue = state.hue,
                            saturation = state.saturation,
                            brightness = state.brightness,
                            onHueChange = { storageViewModel.onHueChange(it) },
                            onSaturationBrightnessChange = { sat, bri ->
                                storageViewModel.onSaturationBrightnessChange(sat, bri)
                            }
                        )

                        state.mainColorError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = { storageViewModel.updateStorage() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Green
                            ),
                            enabled = !state.isLoading
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Salvar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

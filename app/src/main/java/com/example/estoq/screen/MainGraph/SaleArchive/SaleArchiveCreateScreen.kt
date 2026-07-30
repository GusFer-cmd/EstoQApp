package com.example.estoq.screen.MainGraph.SaleArchive

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.data.Viewmodel.SaleArchive.SaleArchiveViewModel
import com.example.estoq.screen.MainGraph.SaleArchive.components.CartReviewStep
import com.example.estoq.screen.MainGraph.SaleArchive.components.CartStep
import com.example.estoq.screen.MainGraph.SaleArchive.components.ClientSelectionStep
import com.example.estoq.screen.MainGraph.SaleArchive.components.PaymentStep
import com.example.estoq.screen.MainGraph.SaleArchive.components.StepIndicator
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleArchiveCreateScreen(
    saleArchiveViewModel: SaleArchiveViewModel,
    onNavigateBack: () -> Unit
) {
    val state by saleArchiveViewModel.uiState.collectAsState()
    val clients by saleArchiveViewModel.allClients.collectAsState(initial = emptyList())
    val items by saleArchiveViewModel.allItems.collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    val priceFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    val totalQuantity = remember(state.cartItems) {
        state.cartItems.sumOf { it.quantity }
    }

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            onNavigateBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state.currentStep) {
                            1 -> "Selecionar Cliente"
                            2 -> "Montar Carrinho"
                            3 -> "Revisão do Carrinho"
                            4 -> "Pagamento"
                            else -> "Nova Venda"
                        }
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = {
                        if (state.currentStep > 1) {
                            saleArchiveViewModel.onStepChange(state.currentStep - 1)
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (state.currentStep == 2 && state.cartItems.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrinho",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "$totalQuantity",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = priceFormat.format(state.totalValue),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            )
                        }
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
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                StepIndicator(
                    currentStep = state.currentStep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )

                AnimatedContent(
                    targetState = state.currentStep,
                    label = "step_content"
                ) { step ->
                    when (step) {
                        1 -> ClientSelectionStep(
                            clients = clients,
                            searchQuery = state.clientSearchQuery,
                            onSearchQueryChange = { saleArchiveViewModel.onClientSearchQueryChange(it) },
                            onClientSelected = { saleArchiveViewModel.selectClient(it.id) }
                        )
                        2 -> CartStep(
                            items = items,
                            cartItems = state.cartItems,
                            onAddItem = { saleArchiveViewModel.addToCart(it) },
                            onIncrementItem = { itemId ->
                                val item = items.find { it.id == itemId }
                                if (item != null) {
                                    saleArchiveViewModel.addToCart(item)
                                }
                            },
                            onDecrementItem = { itemId ->
                                val cartItem = state.cartItems.find { it.itemId == itemId }
                                if (cartItem != null) {
                                    saleArchiveViewModel.updateCartItemQuantity(
                                        itemId,
                                        cartItem.quantity - 1
                                    )
                                }
                            },
                            onContinue = { saleArchiveViewModel.onStepChange(3) }
                        )
                        3 -> CartReviewStep(
                            cartItems = state.cartItems,
                            priceFormat = priceFormat,
                            onUpdateQuantity = { itemId, qty ->
                                saleArchiveViewModel.updateCartItemQuantity(itemId, qty)
                            },
                            onRemoveItem = { saleArchiveViewModel.removeFromCart(it) },
                            onContinue = { saleArchiveViewModel.onStepChange(4) }
                        )
                        4 -> PaymentStep(
                            paymentMethod = state.paymentMethod,
                            installment = state.installment,
                            cartItems = state.cartItems,
                            totalValue = state.totalValue,
                            priceFormat = priceFormat,
                            onPaymentMethodChange = { saleArchiveViewModel.onPaymentMethodChange(it) },
                            onInstallmentChange = { saleArchiveViewModel.onInstallmentChange(it) },
                            onConfirm = { saleArchiveViewModel.createSaleArchive() },
                            isLoading = state.isLoading
                        )
                    }
                }
            }
        }
    }
}

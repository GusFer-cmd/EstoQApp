package com.example.estoq.screen.MainGraph.SaleArchive.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.estoq.component.SearchBar
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Ui_State.SaleArchive.CartUiItem

@Composable
internal fun CartStep(
    items: List<Item>,
    cartItems: List<CartUiItem>,
    onAddItem: (Item) -> Unit,
    onIncrementItem: (Long) -> Unit,
    onDecrementItem: (Long) -> Unit,
    onContinue: () -> Unit
) {
    var itemSearchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(items, itemSearchQuery) {
        if (itemSearchQuery.isBlank()) items
        else items.filter {
            it.name.contains(itemSearchQuery, ignoreCase = true) ||
                it.brand.contains(itemSearchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SearchBar(
            value = itemSearchQuery,
            onValueChange = { itemSearchQuery = it },
            placeholder = "Pesquisar item...",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(4.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems, key = { it.id }) { item ->
                val cartItem = cartItems.find { it.itemId == item.id }
                val quantityInCart = cartItem?.quantity ?: 0

                ItemGridCartCard(
                    item = item,
                    quantityInCart = quantityInCart,
                    onAdd = { onAddItem(item) },
                    onIncrement = { onIncrementItem(item.id) },
                    onDecrement = { onDecrementItem(item.id) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = cartItems.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onSecondary,
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Continuar")
        }

        Spacer(Modifier.height(8.dp))
    }
}

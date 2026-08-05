package com.example.estoq.data.Viewmodel.Analytic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.PivotSaleItem.ItemSalesSummary
import com.example.estoq.data.Model.PivotSaleItem.MonthlyProfitSummary
import com.example.estoq.data.Repository.Item.ItemRepository
import com.example.estoq.data.Repository.PivotSaleItem.PivotSaleItemRepository
import com.example.estoq.data.Ui_State.Analytic.LastUnitUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticViewModel (
    private val pivotSaleItemRepository: PivotSaleItemRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    val bestSellers: Flow<List<ItemSalesSummary>> = pivotSaleItemRepository.getBestSellingItems()

    val worstSellers: Flow<List<ItemSalesSummary>> = pivotSaleItemRepository.getLeastSellingItems()

    val monthlyProfits: Flow<List<MonthlyProfitSummary>> = pivotSaleItemRepository.getProfitByMonth()

    val lowStockItems: Flow<List<Item>> = itemRepository.getLowStockItems(5)

    private val _uiState = MutableStateFlow(LastUnitUiState())
    val uiState: StateFlow<LastUnitUiState> = _uiState

    fun clearIsRestocked() {
        _uiState.value = _uiState.value.copy(isRestocked = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun addStock(itemId: Long, quantity: Int) {
        if (quantity <= 0) return
        viewModelScope.launch {
            try {   
                itemRepository.incrementStock(itemId, quantity)
                _uiState.value = _uiState.value.copy(isRestocked = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Não foi possível reabastecer o item.")
            }
        }
    }
}

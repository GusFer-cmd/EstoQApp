package com.example.estoq.data.Viewmodel.Item

import androidx.lifecycle.ViewModel
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Repository.Item.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class ItemCategoryViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _storageId = MutableStateFlow(0L)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allItems: Flow<List<Item>> = _storageId.flatMapLatest { storageId ->
        if (storageId == 0L) flowOf(emptyList())
        else itemRepository.getByStorageId(storageId)
    }

    val filteredItems: Flow<List<Item>> = combine(
        _searchQuery, allItems
    ) { query, items ->
        if (query.isBlank()) items
        else items.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    val itemCount: Flow<Int> = filteredItems.map { it.size }

    fun setStorageId(id: Long) {
        _storageId.value = id
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}

package com.example.estoq.data.Viewmodel.Item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estoq.data.Exceptions.Item.ItemException
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.Item.ItemType
import com.example.estoq.data.Repository.Item.ItemRepository
import com.example.estoq.data.Repository.Storage.StorageRepository
import com.example.estoq.data.Ui_State.Item.ItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ItemViewModel(
    private val itemRepository: ItemRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemUiState())

    val uiState: StateFlow<ItemUiState> = _uiState

    val allItems: Flow<List<Item>> = itemRepository.getAll()

    val allStorages = storageRepository.getAll()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedTypeFilter = MutableStateFlow<ItemType?>(null)
    val selectedTypeFilter: StateFlow<ItemType?> = _selectedTypeFilter

    val filteredItems: Flow<List<Item>> = combine(
        _searchQuery, _selectedTypeFilter, allItems
    ) { query, typeFilter, items ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.brand.contains(query, ignoreCase = true)
            val matchesType = typeFilter == null || item.type == typeFilter
            matchesQuery && matchesType
        }
    }

    fun onTypeFilterChange(type: ItemType?) {
        _selectedTypeFilter.value = type
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null)
    }

    fun onBrandChange(value: String) {
        _uiState.value = _uiState.value.copy(brand = value, brandError = null)
    }

    fun onTypeChange(type: ItemType) {
        val current = _uiState.value
        val resetSize = current.type != type
        _uiState.value = current.copy(
            type = type,
            typeError = null,
            size = if (resetSize) "" else current.size,
            sizeError = if (resetSize) null else current.sizeError
        )
    }

    fun onSizeChange(size: String) {
        _uiState.value = _uiState.value.copy(size = size, sizeError = null)
    }

    fun onPriceChange(value: String) {
        val digits = value.filter { it.isDigit() }
            .trimStart('0')
            .ifEmpty { "0" }
        val padded = digits.padStart(3, '0')
        val cents = padded.takeLast(2)
        val reais = padded.dropLast(2)
        val formattedReais = reais.reversed().chunked(3).joinToString(".").reversed()
        val formatted = "$formattedReais,$cents"
        _uiState.value = _uiState.value.copy(currentPrice = formatted, currentPriceError = null)
    }

    fun onQuantityChange(value: String) {
        _uiState.value = _uiState.value.copy(stockQuantity = value, stockQuantityError = null)
    }

    fun onStorageChange(storageId: Long) {
        _uiState.value = _uiState.value.copy(storageId = storageId, storageIdError = null)
    }

    fun onImagePathChange(path: String?) {
        _uiState.value = _uiState.value.copy(imagePath = path)
    }

    fun onSearchQueryChange(value: String) {
        _searchQuery.value = value
    }

    fun searchByNameOrBrand(query: String): Flow<List<Item>> =
        itemRepository.searchByNameOrBrand(query)

    fun resetState() {
        _uiState.value = ItemUiState()
    }

    fun clearIsUpdated() {
        _uiState.value = _uiState.value.copy(isUpdated = false)
    }

    fun clearIsDeleted() {
        _uiState.value = _uiState.value.copy(isDeleted = false)
    }

    private fun formatPrice(value: Double): String {
        val cents = (value * 100).toLong()
        val digits = cents.toString().padStart(3, '0')
        val centsStr = digits.takeLast(2)
        val reais = digits.dropLast(2)
        val formattedReais = reais.reversed().chunked(3).joinToString(".").reversed()
        return "$formattedReais,$centsStr"
    }

    private fun parsePrice(value: String): Double {
        val clean = value
            .replace(".", "")
            .replace(",", ".")
        return clean.toDouble()
    }

    fun getById(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val item = itemRepository.getById(id)
                if (item != null) {
                    _uiState.value = _uiState.value.copy(
                        id = item.id,
                        name = item.name,
                        brand = item.brand,
                        currentPrice = item.currentPrice.let { formatPrice(it) },
                        stockQuantity = item.stockQuantity.toString(),
                        imagePath = item.imagePath,
                        storageId = item.storageId,
                        type = item.type,
                        size = item.size,
                        createdAt = item.createdAt,
                        isLoading = false
                    )
                } else {
                    throw ItemException.NotFoundException()
                }
            } catch (e: ItemException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun createItem() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.name.isBlank()) {
                    throw ItemException.EmptyNameException()
                }
                if (state.brand.isBlank()) {
                    throw ItemException.EmptyBrandException()
                }
                if (state.storageId == 0L) {
                    throw ItemException.EmptyStorageException()
                }
                if (state.currentPrice.isBlank()) {
                    throw ItemException.InvalidPriceException()
                }
                if (state.stockQuantity.isBlank()) {
                    throw ItemException.InvalidQuantityException()
                }
                if (state.size.isBlank()) {
                    throw ItemException.EmptySizeException()
                }

                _uiState.value = state.copy(isLoading = true)

                itemRepository.insert(
                    Item(
                        name = state.name.trim(),
                        brand = state.brand.trim(),
                        currentPrice = parsePrice(state.currentPrice),
                        stockQuantity = state.stockQuantity.toInt(),
                        imagePath = state.imagePath,
                        storageId = state.storageId,
                        type = state.type,
                        size = state.size
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreated = true
                )

            } catch (e: ItemException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = when (e) {
                    is ItemException.EmptyNameException ->
                        _uiState.value.copy(nameError = e.message)
                    is ItemException.EmptyBrandException ->
                        _uiState.value.copy(brandError = e.message)
                    is ItemException.InvalidPriceException ->
                        _uiState.value.copy(currentPriceError = e.message)
                    is ItemException.InvalidQuantityException ->
                        _uiState.value.copy(stockQuantityError = e.message)
                    is ItemException.EmptyStorageException ->
                        _uiState.value.copy(storageIdError = e.message)
                    is ItemException.EmptySizeException ->
                        _uiState.value.copy(sizeError = e.message)
                    is ItemException.ItemUnknownException ->
                        _uiState.value.copy(error = e.message)
                    else -> _uiState.value.copy(error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateItem() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.name.isBlank()) {
                    throw ItemException.EmptyNameException()
                }
                if (state.brand.isBlank()) {
                    throw ItemException.EmptyBrandException()
                }
                if (state.storageId == 0L) {
                    throw ItemException.EmptyStorageException()
                }
                if (state.id == 0L) {
                    throw ItemException.InvalidIdException()
                }
                if (state.currentPrice.isBlank()) {
                  throw ItemException.InvalidPriceException()
                }
                if (state.stockQuantity.isBlank()) {
                   throw ItemException.InvalidQuantityException()
                }
                if (state.size.isBlank()) {
                    throw ItemException.EmptySizeException()
                }

                _uiState.value = state.copy(isLoading = true)

                itemRepository.update(
                    Item(
                        id = state.id,
                        name = state.name.trim(),
                        brand = state.brand.trim(),
                        currentPrice = parsePrice(state.currentPrice),
                        stockQuantity = state.stockQuantity.toInt(),
                        imagePath = state.imagePath,
                        storageId = state.storageId,
                        type = state.type,
                        size = state.size,
                        createdAt = state.createdAt
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isUpdated = true
                )

            } catch (e: ItemException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = when (e) {
                    is ItemException.EmptyNameException ->
                        _uiState.value.copy(nameError = e.message)
                    is ItemException.EmptyBrandException ->
                        _uiState.value.copy(brandError = e.message)
                    is ItemException.InvalidPriceException ->
                        _uiState.value.copy(currentPriceError = e.message)
                    is ItemException.InvalidQuantityException ->
                        _uiState.value.copy(stockQuantityError = e.message)
                    is ItemException.EmptyStorageException ->
                        _uiState.value.copy(storageIdError = e.message)
                    is ItemException.EmptySizeException ->
                        _uiState.value.copy(sizeError = e.message)
                    is ItemException.InvalidIdException ->
                        _uiState.value.copy(error = e.message)
                    is ItemException.ItemUnknownException ->
                        _uiState.value.copy(error = e.message)
                    else -> _uiState.value.copy(error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            try {
                if (id == 0L) throw ItemException.InvalidIdException()

                _uiState.value = _uiState.value.copy(isLoading = true)
                itemRepository.delete(id)
                _uiState.value = _uiState.value.copy(isLoading = false, isDeleted = true)
            } catch (e: ItemException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = ItemException.NotFoundException().message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

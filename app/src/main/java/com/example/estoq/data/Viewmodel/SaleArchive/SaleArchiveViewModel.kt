package com.example.estoq.data.Viewmodel.SaleArchive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estoq.data.Exceptions.SaleArchive.SaleArchiveException
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.PivotSaleItem.PivotSaleItem
import com.example.estoq.data.Model.SaleArchive.SaleArchive
import com.example.estoq.data.Model.SaleArchive.SaleArchivePaymentMethod
import com.example.estoq.data.Repository.Client.ClientRepository
import com.example.estoq.data.Repository.Item.ItemRepository
import com.example.estoq.data.Repository.PivotSaleItem.PivotSaleItemRepository
import com.example.estoq.data.Repository.SaleArchive.SaleArchiveRepository
import com.example.estoq.data.Ui_State.SaleArchive.CartUiItem
import com.example.estoq.data.Ui_State.SaleArchive.DetailSaleItem
import com.example.estoq.data.Ui_State.SaleArchive.SaleArchiveUiState
import com.example.estoq.notification.manager.NotificationDispatcher
import com.example.estoq.notification.model.NotificationEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SaleArchiveViewModel(
    private val saleArchiveRepository: SaleArchiveRepository,
    private val pivotSaleItemRepository: PivotSaleItemRepository,
    private val itemRepository: ItemRepository,
    private val clientRepository: ClientRepository,
    private val notificationDispatcher: NotificationDispatcher? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaleArchiveUiState())

    val uiState: StateFlow<SaleArchiveUiState> = _uiState

    val allSales: Flow<List<SaleArchive>> = saleArchiveRepository.getAll()

    val allItems: Flow<List<Item>> = itemRepository.getAll()

    val allClients = clientRepository.getAll()

    private val _selectedPaymentMethodFilter = MutableStateFlow<SaleArchivePaymentMethod?>(null)
    val selectedPaymentMethodFilter: StateFlow<SaleArchivePaymentMethod?> = _selectedPaymentMethodFilter

    fun onPaymentMethodFilter(paymentMethod: SaleArchivePaymentMethod?) {
        _selectedPaymentMethodFilter.value = paymentMethod
    }

    fun onClientChange(value: Long) {
        _uiState.value = _uiState.value.copy(clientId = value, clientIdError = null)
    }

    fun onInstallmentChange(value: String) {
        _uiState.value = _uiState.value.copy(installment = value, installmentError = null)
    }

    fun onPaymentMethodChange(paymentMethod: SaleArchivePaymentMethod) {
        val current = _uiState.value
        val resetInstallment = current.paymentMethod != paymentMethod
        _uiState.value = current.copy(
            paymentMethod = paymentMethod,
            paymentMethodError = null,
            installment = if (resetInstallment) "" else current.installment,
            installmentError = if (resetInstallment) null else current.installmentError
        )
    }

    fun onStepChange(step: Int) {
        if (step in 1..4) {
            _uiState.value = _uiState.value.copy(currentStep = step)
        }
    }

    fun selectClient(clientId: Long) {
        _uiState.value = _uiState.value.copy(
            clientId = clientId,
            clientIdError = null,
            currentStep = 2
        )
    }

    fun addToCart(item: Item, quantity: Int = 1) {
        val current = _uiState.value
        val existing = current.cartItems.find { it.itemId == item.id }

        val newQuantity = if (existing != null) existing.quantity + quantity else quantity
        if (newQuantity > item.stockQuantity) return

        val newCartItems = if (existing != null) {
            current.cartItems.map {
                if (it.itemId == item.id) {
                    it.copy(quantity = it.quantity + quantity)
                } else it
            }
        } else {
            current.cartItems + CartUiItem(
                itemId = item.id,
                name = item.name,
                brand = item.brand,
                size = item.size,
                quantity = quantity,
                unitPrice = item.currentPrice,
                stockQuantity = item.stockQuantity
            )
        }

        _uiState.value = current.copy(cartItems = newCartItems)
        recalculateTotal()
    }

    fun updateCartItemQuantity(itemId: Long, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(itemId)
            return
        }

        val current = _uiState.value
        val cartItem = current.cartItems.find { it.itemId == itemId }
        if (cartItem != null && quantity > cartItem.stockQuantity) return

        _uiState.value = current.copy(
            cartItems = current.cartItems.map {
                if (it.itemId == itemId) it.copy(quantity = quantity) else it
            }
        )
        recalculateTotal()
    }

    fun removeFromCart(itemId: Long) {
        val current = _uiState.value
        _uiState.value = current.copy(
            cartItems = current.cartItems.filter { it.itemId != itemId }
        )
        recalculateTotal()
    }

    private fun recalculateTotal() {
        val total = _uiState.value.cartItems.sumOf { it.quantity * it.unitPrice }
        _uiState.value = _uiState.value.copy(totalValue = total)
    }

    fun resetState() {
        _uiState.value = SaleArchiveUiState()
    }

    fun clearIsCreated() {
        _uiState.value = _uiState.value.copy(isCreated = false)
    }

    fun clearIsDeleted() {
        _uiState.value = _uiState.value.copy(isDeleted = false)
    }

    fun onClientSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(clientSearchQuery = query)
    }

    fun getById(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val saleArchive = saleArchiveRepository.getById(id)
                if (saleArchive != null) {
                    _uiState.value = _uiState.value.copy(
                        id = saleArchive.id,
                        clientId = saleArchive.clientId,
                        paymentMethod = saleArchive.paymentMethod,
                        installment = saleArchive.installment,
                        totalValue = saleArchive.totalValue,
                        createdAt = saleArchive.createdAt,
                        isLoading = false
                    )
                } else {
                    throw SaleArchiveException.SaleArchiveNotFoundException()
                }
            } catch (e: SaleArchiveException) {
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

    fun loadSaleDetail(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val sale = saleArchiveRepository.getById(id)
                    ?: throw SaleArchiveException.SaleArchiveNotFoundException()

                val client = clientRepository.getById(sale.clientId)

                val pivotItems = pivotSaleItemRepository.getItemsBySaleArchiveId(id)
                val detailItems = pivotItems.map { pivot ->
                    val item = itemRepository.getById(pivot.itemId)
                    DetailSaleItem(
                        name = item?.name ?: "Desconhecido",
                        brand = item?.brand ?: "",
                        size = item?.size ?: "",
                        quantity = pivot.quantity,
                        unitPrice = pivot.unitPrice
                    )
                }

                _uiState.value = _uiState.value.copy(
                    id = sale.id,
                    clientId = sale.clientId,
                    paymentMethod = sale.paymentMethod,
                    installment = sale.installment,
                    totalValue = sale.totalValue,
                    createdAt = sale.createdAt,
                    detailClientName = client?.let { "${it.firstName} ${it.lastName}".trim() }
                        ?: "Cliente desconhecido",
                    detailItems = detailItems,
                    isLoading = false
                )
            } catch (e: SaleArchiveException) {
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

    fun createSaleArchive() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.clientId == 0L) {
                    throw SaleArchiveException.EmptyClientIdException()
                }
                if (state.cartItems.isEmpty()) {
                    throw SaleArchiveException.EmptyCartException()
                }
                if (state.installment.isBlank()) {
                    throw SaleArchiveException.EmptyInstallmenteException()
                }

                _uiState.value = state.copy(isLoading = true)

                val saleId = saleArchiveRepository.insert(
                    SaleArchive(
                        clientId = state.clientId,
                        paymentMethod = state.paymentMethod,
                        installment = state.installment,
                        totalValue = state.totalValue
                    )
                )

                val pivotItems = state.cartItems.map { cartItem ->
                    PivotSaleItem(
                        saleArchiveId = saleId,
                        itemId = cartItem.itemId,
                        quantity = cartItem.quantity,
                        unitPrice = cartItem.unitPrice
                    )
                }
                pivotSaleItemRepository.insertAll(pivotItems)

                for (cartItem in state.cartItems) {
                    itemRepository.decrementStock(cartItem.itemId, cartItem.quantity)
                }

                val lowItems = mutableListOf<Item>()
                val zeroItems = mutableListOf<Item>()
                for (cartItem in state.cartItems) {
                    val item = itemRepository.getById(cartItem.itemId)
                    if (item != null) {
                        when {
                            item.stockQuantity == 0 -> zeroItems.add(item)
                            item.stockQuantity <= 5 -> lowItems.add(item)
                        }
                    }
                }
                zeroItems.forEach { item ->
                    notificationDispatcher?.dispatch(NotificationEvent.ZeroStock(item))
                }
                if (lowItems.isNotEmpty()) {
                    notificationDispatcher?.dispatch(NotificationEvent.LowStock(lowItems))
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreated = true
                )

                val client = clientRepository.getById(state.clientId)
                val clientName = client?.let { "${it.firstName} ${it.lastName}".trim() }
                    ?: "Cliente"

                notificationDispatcher?.dispatch(
                    NotificationEvent.SaleCreated(
                        saleId = saleId,
                        clientName = clientName,
                        total = state.totalValue,
                        itemCount = state.cartItems.sumOf { it.quantity }
                    )
                )

            } catch (e: SaleArchiveException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = when (e) {
                    is SaleArchiveException.EmptyClientIdException ->
                        _uiState.value.copy(clientIdError = e.message)
                    is SaleArchiveException.EmptyInstallmenteException ->
                        _uiState.value.copy(installmentError = e.message)
                    is SaleArchiveException.EmptyCartException ->
                        _uiState.value.copy(error = e.message)
                    is SaleArchiveException.SaleArchiveUnknownException ->
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

    fun deleteSaleArchive(id: Long) {
        viewModelScope.launch {
            try {
                if (id == 0L) throw SaleArchiveException.InvalidIdException()

                _uiState.value = _uiState.value.copy(isLoading = true)

                val saleArchive = saleArchiveRepository.getById(id)
                val clientName = saleArchive?.let {
                    clientRepository.getById(it.clientId)?.let { c ->
                        "${c.firstName} ${c.lastName}"
                    }
                } ?: "Cliente"

                val pivotItems = pivotSaleItemRepository.getItemsBySaleArchiveId(id)
                for (pivotItem in pivotItems) {
                    itemRepository.incrementStock(pivotItem.itemId, pivotItem.quantity)
                }

                saleArchiveRepository.delete(id)
                _uiState.value = _uiState.value.copy(isLoading = false, isDeleted = true)

                if (saleArchive != null) {
                    notificationDispatcher?.dispatch(
                        NotificationEvent.SaleDeleted(
                            saleId = id,
                            clientName = clientName,
                            total = saleArchive.totalValue
                        )
                    )
                }

                val restoredNames = pivotItems.mapNotNull { pivotItem ->
                    itemRepository.getById(pivotItem.itemId)?.name
                }
                if (restoredNames.isNotEmpty()) {
                    notificationDispatcher?.dispatch(
                        NotificationEvent.StockRestored(restoredNames)
                    )
                }

            } catch (e: SaleArchiveException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = SaleArchiveException.SaleArchiveNotFoundException().message
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

package com.example.estoq.data.Viewmodel.Client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estoq.data.Exceptions.Client.ClientException
import com.example.estoq.data.Model.Client.Client
import com.example.estoq.data.Repository.Client.ClientRepository
import com.example.estoq.data.Ui_State.Client.ClientUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClientSection(
    val letter: Char,
    val clients: List<Client>
)

@OptIn(ExperimentalCoroutinesApi::class)
class ClientViewModel(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val uiState: StateFlow<ClientUiState> = _uiState

    val clients: StateFlow<List<Client>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) clientRepository.getAll()
            else clientRepository.searchByName(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sections: StateFlow<List<ClientSection>> = clients.map { list ->
        list.groupBy {
            val fullName = "${it.firstName} ${it.lastName}".trim()
            fullName.first().uppercaseChar()
        }
            .map { (letter, clients) -> ClientSection(letter, clients) }
            .sortedBy { it.letter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun searchByClientName(query: String): Flow<List<Client>> {
        return if (query.isBlank()) clientRepository.getAll()
        else clientRepository.searchByName(query)
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, firstNameError = null)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value, lastNameError = null)
    }

    fun onTelephoneChange(value: String) {
        _uiState.value = _uiState.value.copy(telephone = value, telephoneError = null)
    }

    fun resetState() {
        _uiState.value = ClientUiState()
    }

    fun clearIsCreated() {
        _uiState.value = _uiState.value.copy(isCreated = false)
    }

    fun clearIsUpdated() {
        _uiState.value = _uiState.value.copy(isUpdated = false)
    }

    fun clearIsDeleted() {
        _uiState.value = _uiState.value.copy(isDeleted = false)
    }

    val allClients = clientRepository.getAll()

    fun getById(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val client = clientRepository.getById(id)
                if (client != null) {
                    _uiState.value = _uiState.value.copy(
                        id = client.id,
                        firstName = client.firstName,
                        lastName = client.lastName,
                        telephone = client.telephone,
                        isLoading = false
                    )
                } else {
                    throw ClientException.ClientUnknownException()
                }
            } catch (e: ClientException) {
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

    fun createClient() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.firstName.isBlank()) {
                    throw ClientException.EmptyFirstName()
                }
                if (state.lastName.isBlank()){
                    throw ClientException.EmptyLastName()
                }
                if (state.telephone.isBlank()) {
                    throw ClientException.EmptyTelephone()
                }

                _uiState.value = state.copy(isLoading = true)

                clientRepository.insert(
                    Client(
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        telephone = state.telephone.trim()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreated = true
                )

            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(isLoading = true)
                _uiState.value = when (e) {
                    is ClientException.EmptyFirstName ->
                        _uiState.value.copy(firstNameError = e.message)
                    is ClientException.EmptyLastName ->
                        _uiState.value.copy(lastNameError = e.message)
                    is ClientException.EmptyTelephone ->
                        _uiState.value.copy(telephoneError = e.message)
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

    fun updateClient() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                if (state.id == 0L) {
                    throw ClientException.InvalidIdException()
                }
                if (state.firstName.isBlank()) {
                    throw ClientException.EmptyFirstName()
                }
                if (state.lastName.isBlank()){
                    throw ClientException.EmptyLastName()
                }
                if (state.telephone.isBlank()) {
                    throw ClientException.EmptyTelephone()
                }

                _uiState.value = state.copy(isLoading = true)

                clientRepository.update(
                    Client(
                        id = state.id,
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        telephone = state.telephone.trim()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isUpdated = true
                )

            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(isLoading = true)
                _uiState.value = when (e) {
                    is ClientException.EmptyFirstName ->
                        _uiState.value.copy(firstNameError = e.message)
                    is ClientException.EmptyLastName ->
                        _uiState.value.copy(lastNameError = e.message)
                    is ClientException.EmptyTelephone ->
                        _uiState.value.copy(telephoneError = e.message)
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

    fun deleteClient(id: Long) {
        viewModelScope.launch {
            try {
                if (id == 0L) throw ClientException.InvalidIdException()

                _uiState.value = _uiState.value.copy(isLoading = true)
                clientRepository.delete(id)
                _uiState.value = _uiState.value.copy(isLoading = false, isDeleted = true)
            } catch (e: ClientException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = ClientException.InvalidIdException().message
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
